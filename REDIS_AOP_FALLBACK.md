# Redis AOP 기반 Fallback 처리

## 개요

Redis 연결 실패 또는 예외 상황에서 자동으로 대체 로직을 실행하는 AOP 기반 Fallback 처리 시스템입니다.

## 주요 구성 요소

### 1. @RedisFallback 어노테이션
```java
@RedisFallback(fallbackMethod = "customFallbackMethod")
public Object someRedisOperation() {
    // Redis 작업
}

public Object customFallbackMethod() {
    // Fallback 로직
}
```

**속성:**
- `fallbackMethod`: 커스텀 Fallback 메서드명 (선택사항)
- `exceptions`: Fallback을 실행할 예외 클래스들
- `enableLogging`: 로깅 여부 (기본값: true)
- `enableSlackNotification`: Slack 알림 여부 (기본값: true)

### 2. RedisFallbackAspect
- `@RedisFallback` 어노테이션이 적용된 메서드를 감시
- Redis 예외 발생 시 자동으로 Fallback 처리 실행
- 로깅 및 Slack 알림 기능 제공

### 3. FallbackStrategy
기본 Fallback 전략을 제공하는 인터페이스와 구현체

## 적용된 서비스

### 1. AclPolicyServiceImpl
- 메서드: `isMethodAllowed()`
- Fallback: Redis 실패 시 DB에서 직접 조회
- 보안: DB 조회도 실패하면 보수적으로 `false` 반환

### 2. RedisSessionServiceImpl
- Redis 세션 관리 전체 메서드에 적용
- Fallback: 메모리 기반 임시 스토리지 사용
- 모든 세션 관련 작업에 대해 완전한 Fallback 제공

## 사용 방법

### 1. 기본 사용법
```java
@Service
public class MyService {
    
    @RedisFallback  // 기본 Fallback 전략 사용
    public String getData(String key) {
        return redisTemplate.opsForValue().get(key);
    }
}
```

### 2. 커스텀 Fallback 메서드 사용
```java
@Service
public class MyService {
    
    @RedisFallback(fallbackMethod = "getDataFromDB")
    public String getData(String key) {
        return redisTemplate.opsForValue().get(key);
    }
    
    public String getDataFromDB(String key) {
        // DB에서 데이터 조회
        return databaseService.findByKey(key);
    }
}
```

### 3. 특정 예외만 처리
```java
@RedisFallback(
    exceptions = {RedisConnectionFailureException.class, TimeoutException.class},
    enableSlackNotification = false
)
public String getData(String key) {
    return redisTemplate.opsForValue().get(key);
}
```

## 처리되는 예외 (기본값)

- `RedisConnectionFailureException`: Redis 연결 실패
- `DataAccessException`: 데이터 액세스 예외
- `ConnectException`: 네트워크 연결 예외
- `TimeoutException`: 타임아웃 예외

## 테스트

### API 엔드포인트
Redis Fallback 기능을 테스트할 수 있는 API가 제공됩니다:

- `POST /api/redis-test/session/{sessionId}` - 세션 데이터 저장
- `GET /api/redis-test/session/{sessionId}/{key}` - 세션 데이터 조회
- `DELETE /api/redis-test/session/{sessionId}` - 세션 삭제
- `GET /api/redis-test/sessions/active` - 활성 세션 목록

### 테스트 방법

1. **정상 동작 테스트**: Redis가 정상 동작할 때 API 호출
2. **Fallback 테스트**: Redis 서버를 중지한 후 API 호출
3. **로그 확인**: Fallback 실행 시 로그 메시지 확인
4. **복구 테스트**: Redis 서버 재시작 후 정상 동작 확인

## 로그 메시지

### Fallback 실행 시
```
🔴 Redis Fallback 실행 - 메서드: methodName, 예외: exceptionMessage
🟡 커스텀 Fallback 메서드 실행: fallbackMethodName
🟡 기본 Fallback 전략 실행: DefaultFallbackStrategy
```

### 메모리 Fallback 사용 시
```
🟡 Redis Fallback - 메모리에 세션 데이터 저장: sessionId=xxx, key=yyy
🟡 Redis Fallback - 메모리에서 세션 데이터 조회: sessionId=xxx, key=yyy
```

## 설정

### application.yml
```yaml
logging:
  slack:
    webhook-redis: "your-slack-webhook-url"  # Slack 알림용
```

## 주의사항

1. **메모리 Fallback**: 서버 재시작 시 메모리 데이터는 모두 손실됩니다.
2. **성능**: Fallback 로직은 Redis보다 성능이 떨어질 수 있습니다.
3. **데이터 일관성**: Redis와 Fallback 스토리지 간 데이터 불일치 가능성이 있습니다.
4. **TTL**: 메모리 기반 Fallback에서는 TTL 기능이 제한적입니다.

## 확장 가능성

### 커스텀 Fallback 전략 추가
```java
@Component
public class DatabaseFallbackStrategy implements FallbackStrategy {
    
    @Override
    public Object executeFallback(String methodName, Object[] args, Throwable exception) {
        // 데이터베이스 기반 Fallback 로직
    }
    
    @Override
    public boolean isApplicable(String methodName) {
        return methodName.contains("cache");
    }
}
```

### 영속성 있는 Fallback 스토리지
- H2 Database 사용
- File 기반 스토리지
- 다른 NoSQL 데이터베이스 활용 
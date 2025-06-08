# RabbitMQ 설정 및 사용 가이드

## 개요

RabbitMQ는 메시지 큐 시스템으로 비동기 메시징, 이벤트 기반 아키텍처, 마이크로서비스 간 통신을 위해 프로젝트에 추가되었습니다.

## RabbitMQ 시작하기

### 1. RabbitMQ 서버 실행

```bash
# RabbitMQ Docker 컨테이너 시작
docker-compose -f rabbitmq-compose.yml up -d

# RabbitMQ 상태 확인
docker-compose -f rabbitmq-compose.yml ps

# 로그 확인
docker-compose -f rabbitmq-compose.yml logs rabbitmq
```

### 2. Management UI 접속

- URL: http://localhost:15672
- 사용자명: guest
- 비밀번호: guest

## 구성 요소

### 1. 큐 구조

#### 주요 큐들
- **user.queue**: 사용자 이벤트 처리
- **notification.queue**: 알림 메시지 처리
- **redis.fallback.queue**: Redis 장애 시 Fallback 처리
- **audit.queue**: 감사 로그 처리
- **backend.dlq**: Dead Letter Queue (실패한 메시지)

#### Exchange 구조
- **backend.topic.exchange**: Topic Exchange (패턴 매칭)
- **backend.direct.exchange**: Direct Exchange (정확한 라우팅)
- **backend.dlx.exchange**: Dead Letter Exchange

### 2. 메시지 타입

#### BaseMessage
```java
{
  "messageId": "uuid",
  "messageType": "MESSAGE_TYPE",
  "sender": "SENDER_NAME",
  "timestamp": "2024-01-01 12:00:00",
  "retryCount": 0,
  "priority": 5,
  "ttl": 60000
}
```

#### UserEventMessage
```java
{
  "userId": "user123",
  "eventType": "LOGIN",
  "clientIp": "192.168.1.100",
  "userAgent": "Mozilla/5.0...",
  "additionalData": {...}
}
```

#### NotificationMessage
```java
{
  "recipientId": "user123",
  "recipientType": "USER",
  "title": "알림 제목",
  "content": "알림 내용",
  "notificationType": "EMAIL",
  "actionUrl": "/action/url",
  "templateId": "template_001"
}
```

#### RedisFallbackMessage
```java
{
  "operationType": "SET",
  "redisKey": "session:user123",
  "redisValue": "{...}",
  "ttlSeconds": 3600,
  "failedMethod": "setSessionData",
  "failureReason": "Connection timeout",
  "sourceClass": "RedisSessionServiceImpl"
}
```

## API 사용법

### 1. 메시지 전송 테스트

#### 사용자 이벤트 전송
```bash
curl -X POST "http://localhost:50101/api/message-test/user-event" \
  -d "userId=user123&eventType=LOGIN&clientIp=192.168.1.100"
```

#### 알림 메시지 전송
```bash
curl -X POST "http://localhost:50101/api/message-test/notification" \
  -d "recipientId=user123&recipientType=USER&title=환영합니다&content=가입을축하합니다&notificationType=EMAIL"
```

#### Redis Fallback 메시지 전송
```bash
curl -X POST "http://localhost:50101/api/message-test/redis-fallback" \
  -d "operationType=SET&redisKey=session:test&redisValue=testValue&failedMethod=setData&failureReason=Connection timeout"
```

### 2. Swagger UI

- URL: http://localhost:50101/swagger-ui/index.html
- "Message Test" 섹션에서 모든 메시지 API 테스트 가능

## Redis + RabbitMQ 통합

### Redis AOP Fallback과 RabbitMQ 연동

Redis 장애 발생 시:
1. `@RedisFallback` 어노테이션이 예외를 감지
2. 기본 Fallback 처리 실행
3. 동시에 RabbitMQ로 `RedisFallbackMessage` 전송
4. 메시지 컨슈머가 Redis 복구 후 재처리

```java
@Service
public class ExampleService {
    
    @RedisFallback(fallbackMethod = "fallbackMethod")
    public String getDataFromRedis(String key) {
        // Redis 작업
        return redisTemplate.opsForValue().get(key);
    }
    
    public String fallbackMethod(String key) {
        // Fallback 로직
        return "fallback_value";
    }
}
```

## 모니터링 및 관리

### 1. Management UI에서 확인 가능한 정보

- 큐별 메시지 수
- 메시지 처리 속도
- 연결 상태
- 에러 로그

### 2. 로그 모니터링

```bash
# 애플리케이션 로그에서 RabbitMQ 관련 로그 확인
tail -f logs/application.log | grep -E "(🚀|🎯|🔔|⚠️|💥)"
```

### 3. 주요 모니터링 포인트

- 큐에 쌓인 메시지 수
- Dead Letter Queue 메시지 수
- 메시지 처리 실패율
- 컨슈머 연결 상태

## 트러블슈팅

### 1. 연결 실패

```bash
# RabbitMQ 서버 상태 확인
docker ps | grep rabbitmq

# 네트워크 확인
telnet localhost 5672
```

### 2. 메시지 처리 실패

- Dead Letter Queue 확인
- 애플리케이션 로그 확인
- Management UI에서 에러 메시지 확인

### 3. 성능 이슈

- 큐별 메시지 적체 상황 확인
- 컨슈머 수 조정
- 메시지 배치 처리 고려

## 설정 파일

### application-dev.yml
```yaml
spring:
  rabbitmq:
    host: localhost
    port: 5672
    username: guest
    password: guest
    virtual-host: /
    connection-timeout: 30s
    publisher-confirm-type: correlated
    publisher-returns: true
    listener:
      simple:
        acknowledge-mode: auto
        retry:
          enabled: true
          initial-interval: 1s
          max-attempts: 3
          max-interval: 10s
          multiplier: 2.0
```

## 다음 단계

1. **실제 환경 배포**: 프로덕션 환경에서는 클러스터 구성 고려
2. **메시지 암호화**: 민감한 데이터가 포함된 메시지의 경우 암호화 적용
3. **배치 처리**: 대량 메시지 처리를 위한 배치 처리 로직 구현
4. **모니터링 강화**: Prometheus + Grafana 연동
5. **장애 복구**: 자동 재시도 및 장애 복구 메커니즘 강화 
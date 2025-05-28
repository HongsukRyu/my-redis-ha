package com.backend.api.common.config;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.stream.Collectors;

@Component
@Profile("!prod")
public class RedisDevStartupRunner implements ApplicationRunner {

    private static final String REDIS_HOST = "localhost";
    private static final int REDIS_PORT = 6379;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        System.out.println("[RedisDevStartupRunner] Checking Redis container...");

        // 1. Redis 포트가 열려 있는지 확인
        if (isRedisRunning()) {
            System.out.println("[RedisDevStartupRunner] Redis port is already open. Skipping docker start.");
            return;
        }

        // 2. Redis 컨테이너 실행 여부 확인
        Process checkProcess = Runtime.getRuntime().exec("docker ps --filter name=redis-local --format {{.Names}}");
        String result = new BufferedReader(new InputStreamReader(checkProcess.getInputStream()))
                .lines().collect(Collectors.joining());

        // 3. Redis 컨테이너 실행
        if (!"redis-local".equals(result)) {
            System.out.println("[RedisDevStartupRunner] Redis not running. Starting container...");
            Process startProcess = Runtime.getRuntime().exec("docker compose -f redis-compose.yml up -d redis");
            startProcess.waitFor();
        } else {
            System.out.println("[RedisDevStartupRunner] Redis container is already running.");
        }
    }

    private boolean isRedisRunning() {
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(REDIS_HOST, REDIS_PORT), 200); // 200ms 타임아웃
            return true;
        } catch (IOException e) {
            return false;
        }
    }
}
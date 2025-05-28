package com.backend.api.common.config;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.httpclient5.ApacheDockerHttpClient;
import com.github.dockerjava.core.DockerClientImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class DockerClientConfiguration {

//    @Bean
//    public DockerClient dockerClient() {
//        DefaultDockerClientConfig config = DefaultDockerClientConfig.createDefaultConfigBuilder().build();
//        return DockerClientBuilder.getInstance(config)
//                .withDockerHttpClient(
//                        new ApacheHttpClient5Builder()
//                                .dockerHost(config.getDockerHost())
//                                .sslConfig(config.getSSLConfig())
//                                .build())
////                .withDockerCmdExecFactory(new HttpClient5DockerCmdExecFactory()) // 반드시 이거 있어야 함
//                .build();
//    }

    @Bean
    public DockerClient dockerClient() {
        var config = DefaultDockerClientConfig.createDefaultConfigBuilder()
                .withDockerHost("unix:///var/run/docker.sock")  // 리눅스 기반 or WSL2
                //.withDockerHost("tcp://localhost:2375")       // 윈도우 or 리모트
                .build();

        var httpClient = new ApacheDockerHttpClient.Builder()
                .dockerHost(config.getDockerHost())
                .build();

        return DockerClientImpl.getInstance(config, httpClient);
    }
}
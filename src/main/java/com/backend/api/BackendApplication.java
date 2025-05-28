package com.backend.api;

import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@EnableScheduling
@ServletComponentScan
@SpringBootApplication
@EnableCaching
@EnableEncryptableProperties
public class BackendApplication {

	final Environment env;

	@Bean
	public BCryptPasswordEncoder bCryptPasswordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Autowired
	public BackendApplication(Environment env) {
		this.env = env;
	}

	public static void main(String[] args) {
		String secretKey = System.getenv("JASYPT_ENCRYPTOR_PASSWORD");

		if (secretKey == null || secretKey.isEmpty()) {
			System.err.println("환경변수 'JASYPT_ENCRYPTOR_PASSWORD'가 설정되지 않았습니다.");
			System.exit(1);
		}
		SpringApplication.run(BackendApplication.class, args);
	}
}

package com.backend.feature_flag_platform;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class FeatureFlagPlatformApplication {

	public static void main(String[] args) {
		SpringApplication.run(FeatureFlagPlatformApplication.class, args);
	}

}

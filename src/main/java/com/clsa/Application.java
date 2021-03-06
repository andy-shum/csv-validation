package com.clsa;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
@EnableSwagger2
@EnableCaching
@Configuration
@ComponentScan
/**
 * This is the spring boot starting app
 */
public class Application {

	public static void main(final String[] args) {
		SpringApplication.run(Application.class, args);
	}

}

package com.clsa.config;

import java.util.concurrent.Executor;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@EnableAsync
/**
 * This is the configuration for our thread pool executor
 */
public class AsyncConfiguration {

	private static final Logger logger = LogManager.getLogger(AsyncConfiguration.class);
	
	private static final int POOL_SIZE = 8;

    @Bean (name = "taskExecutor")
    public Executor taskExecutor() {
        final ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(POOL_SIZE);
        executor.setMaxPoolSize(POOL_SIZE);
        // queue capacity set to same as default
        executor.setQueueCapacity(Integer.MAX_VALUE);
        executor.setThreadNamePrefix("CsvThread-");
        executor.initialize();
        return executor;
    }
}

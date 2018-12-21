package com.github.rdriskill.syscheck;

import java.util.concurrent.Executor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author rdriskill
 */
@Configuration
@EnableScheduling
@EnableAsync
@ComponentScan(basePackages = {"com.github.rdriskill.syscheck"})
@PropertySource("classpath:application.properties")
public class AppConfig {

	@Bean
    public Executor taskExecutor() {
        return new SimpleAsyncTaskExecutor();
    }
}

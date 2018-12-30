package com.github.rdriskill.syscheck;

import java.util.concurrent.Executor;

import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.HttpClientBuilder;
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
	
	public static final int TIMEOUT_SECS = 10 * 1000;

	@Bean
    public Executor taskExecutor() {
        return new SimpleAsyncTaskExecutor();
    }
	
	@Bean
	public HttpClient getHttpClient() {
		return HttpClientBuilder
				.create()
				.setDefaultRequestConfig(
						RequestConfig
							.custom()
							.setConnectTimeout(TIMEOUT_SECS)
							.setConnectionRequestTimeout(TIMEOUT_SECS)
							.setSocketTimeout(TIMEOUT_SECS).build()
				)
				.build();
	}
}

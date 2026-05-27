package com.smartbookfinder.config;

import org.springframework.boot.web.client.ClientHttpRequestFactories;
import org.springframework.boot.web.client.ClientHttpRequestFactorySettings;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestClient;

import java.time.Duration;

@Configuration
public class RestClientConfig {

    @Bean
    public RestClient.Builder restClientBuilder(ClientHttpRequestFactorySettings requestFactorySettings) {
        return RestClient.builder()
                .requestFactory(ClientHttpRequestFactories.get(requestFactorySettings))
                .defaultHeader(HttpHeaders.USER_AGENT, "SmartBookFinder/1.0 (academic project)");
    }

    @Bean
    public ClientHttpRequestFactorySettings requestFactorySettings() {
        return ClientHttpRequestFactorySettings.DEFAULTS
                .withConnectTimeout(Duration.ofSeconds(10))
                .withReadTimeout(Duration.ofSeconds(15));
    }
}

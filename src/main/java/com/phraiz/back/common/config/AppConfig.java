package com.phraiz.back.common.config;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
public class AppConfig {

//    @Bean
//    public RestTemplate restTemplate(RestTemplateBuilder builder) {
//        return builder
//                .requestFactory(() -> {
//                    var factory = new HttpComponentsClientHttpRequestFactory();
//                    factory.setConnectTimeout(5000);  // milliseconds
//                    factory.setReadTimeout(5000);     // milliseconds
//                    return factory;
//                })
//                .build();
//    }
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}


package com.phraiz.back.common.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.ConstructorBinding;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Getter
@Configuration
public class GptConfig {

    @Value("${openai.secret-key}")
    private String secretKey;

    @Value("${openai.model}")
    private String model;

    @Value("${openai.api-url}")
    private String apiUrl;

    @Value("${openai.temperature.paraphrase}")
    private Double temperatureParaphrase;

    @Value("${openai.temperature.summary}")
    private Double temperatureSummary;

    @Value("${openai.max-tokens}")
    private Integer maxTokens;

//    @Bean
//    public HttpHeaders httpHeaders() {
//        HttpHeaders headers = new HttpHeaders();
//        headers.setBearerAuth(secretKey);
//        headers.setContentType(MediaType.APPLICATION_JSON);
//        return headers;
//    }
}

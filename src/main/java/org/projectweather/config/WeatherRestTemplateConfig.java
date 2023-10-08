package org.projectweather.config;

import org.projectweather.exceptions.clientExceptions.handler.RestTemplateResponseErrorHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;


@Configuration
public class WeatherRestTemplateConfig {

    @Value("${WEATHER_API_KEY}")
    private String API_KEY;

    @Bean("weatherRestTemplate")
    public RestTemplate weatherRestTemplate(RestTemplateBuilder restTemplateBuilder) {
        return restTemplateBuilder
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader("key", API_KEY)
                .errorHandler(new RestTemplateResponseErrorHandler())
                .build();
    }

}

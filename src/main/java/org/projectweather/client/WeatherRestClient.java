package org.projectweather.client;

import io.github.resilience4j.ratelimiter.RateLimiter;
import org.projectweather.model.weatherApiDto.WeatherApiDto;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;


@Service
public class WeatherRestClient {

    @Value("${weather.api.url}")
    private String url;

    private final RestTemplate restTemplate;
    private final RateLimiter weatherRateLimiter;

    public WeatherRestClient(@Qualifier("weatherRestTemplate") RestTemplate restTemplate,
                             @Qualifier("weatherLimiter") RateLimiter rateLimiter) {
        this.weatherRateLimiter = rateLimiter;
        this.restTemplate = restTemplate;
    }

    public WeatherApiDto requestWeather(String regionName) {
        return RateLimiter.decorateSupplier(weatherRateLimiter, () -> {
            HttpHeaders headers = new HttpHeaders();
            HttpEntity<HttpHeaders> entity = new HttpEntity<>(headers);
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url + ("v1/current.json"))
                    .queryParam("q", regionName);
            ResponseEntity<WeatherApiDto> weatherApiDtoResponseEntity =
                    restTemplate.exchange(builder.toUriString(), HttpMethod.GET, entity, WeatherApiDto.class);
                       return weatherApiDtoResponseEntity.getBody();
        }).get();
    }
}

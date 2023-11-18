package org.projectweather.config;

import org.projectweather.cache.WeatherCache;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CacheConfig {

    @Value("${cache.course.size}")
    private int maxCacheSize;

    @Bean
    public WeatherCache weatherCache (){
        return new WeatherCache(maxCacheSize);

    }
}

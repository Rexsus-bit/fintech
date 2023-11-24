package org.projectweather.kafka;

import lombok.RequiredArgsConstructor;
import org.projectweather.client.WeatherRestClient;
import org.projectweather.model.weatherApiDto.WeatherApiDto;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
public class KafkaProducer {
    private final WeatherRestClient weatherRestClient;
    private final KafkaTemplate<String, WeatherApiDto> kafkaTemplate;
    private static final String TOPIC = "weatherTopic";
    private int cnt = 0;

    @Scheduled(cron = "0 * * * * ?")
    public void sendWeatherApiDto(){
        List<String> cities = Arrays.asList("Cardiff", "Moscow", "Saint Petersburg", "London", "Washington");
        WeatherApiDto weatherApiDto = weatherRestClient.requestWeather(cities.get(cnt));
        cnt++;
        cnt %= cities.size();
        String key = weatherApiDto.getLocation().getName();
        kafkaTemplate.send(TOPIC, key , weatherApiDto);
    }

}


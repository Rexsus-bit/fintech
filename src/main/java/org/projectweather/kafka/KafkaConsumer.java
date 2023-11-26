package org.projectweather.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.projectweather.model.weatherApiDto.WeatherApiDto;
import org.projectweather.model.weatherInCity.WeatherInCity;
import org.projectweather.repository.WeatherInCityJpaRepository;
import org.projectweather.service.WeatherInCityServiceJPAImpl;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.projectweather.mapper.WeatherMapper.weatherApiDtoToWeatherInCity;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaConsumer {

    private final WeatherInCityServiceJPAImpl weatherInCityServiceJPA;
    private final WeatherInCityJpaRepository weatherInCityJpaRepository;
    private final static int LIMIT = 30;

    @KafkaListener(topics = "weatherTopic", groupId = "weather group id")
    public void weatherApiDtoConsumer(WeatherApiDto weatherApiDto) {
        WeatherInCity weatherInCity = weatherApiDtoToWeatherInCity(weatherApiDto);
        weatherInCity = weatherInCityServiceJPA.createWeatherInCity(weatherInCity);
        List<WeatherInCity> list = weatherInCityJpaRepository
                .findByCityIdOrderByWeatherIdAndLimit(weatherInCity.getCity().getId(), LIMIT);
        if (list.size() == LIMIT) {
            double sum = list.stream().mapToDouble(WeatherInCity::getTemperature).sum();
            double avg = sum / LIMIT;
            double roundedAvg = Math.round(avg * 10.0) / 10.0;

            log.info("Среднее значение температуры по городу {} составило {}",
                    weatherApiDto.getLocation().getName(), roundedAvg);
            cleanOldRecords(list);
        }

    }

    private void cleanOldRecords(List<WeatherInCity> list) {
        weatherInCityJpaRepository.deleteWithOlderIdThanIndicated(list.get(29).getCity().getId(), list.get(29).getId());
    }

}

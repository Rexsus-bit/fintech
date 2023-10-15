package org.projectweather.service;

import lombok.RequiredArgsConstructor;
import org.projectweather.model.weatherInCity.WeatherInCity;
import org.projectweather.repository.WeatherInCityJDBCRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service("weatherInCityServiceJDBCImpl")
@RequiredArgsConstructor
public class WeatherInCityServiceJDBCImpl implements WeatherInCityService {

    private final WeatherInCityJDBCRepository weatherInCityJDBCRepository;

    @Override
    @Transactional
    public WeatherInCity createWeatherInCity(WeatherInCity weatherInCity) {
        return weatherInCityJDBCRepository.createWeatherInCity(weatherInCity);
    }

    @Override
    public WeatherInCity findWeatherInCityById(Long id) {
        return weatherInCityJDBCRepository.findWeatherInCityById(id);
    }

    @Override
    public List<WeatherInCity> findAllWeatherInCity() {
        return weatherInCityJDBCRepository.findAllWeatherInCity();
    }

    @Override
    @Transactional
    public WeatherInCity updateWeatherInCity(WeatherInCity weatherInCity) {
        return weatherInCityJDBCRepository.updateWeatherInCity(weatherInCity);
    }

    @Override
    @Transactional
    public void deleteWeatherInCityById(Long id) {
        weatherInCityJDBCRepository.deleteWeatherInCityById(id);
    }
}

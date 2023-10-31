package org.projectweather.service;

import org.projectweather.model.weatherInCity.WeatherInCity;

import java.util.List;

public interface WeatherInCityService {

    WeatherInCity createWeatherInCity(WeatherInCity weatherInCity);

    WeatherInCity findWeatherInCityById(Long id);

    List<WeatherInCity> findAllWeatherInCity();

    WeatherInCity updateWeatherInCity(WeatherInCity weatherInCity);

    void deleteWeatherInCityById(Long id);
}

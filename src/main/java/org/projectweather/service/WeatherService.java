package org.projectweather.service;

import org.projectweather.exceptions.WeatherIsExistedException;
import org.projectweather.exceptions.WeatherIsNotFoundException;
import org.projectweather.model.Weather;

import java.util.List;

public interface WeatherService {
    List<Weather> getWeatherForTheCurrentDate(Long regionId) throws WeatherIsNotFoundException;

    Weather createNewCity(Weather weather) throws WeatherIsExistedException;

    Weather updateWeatherTemperatureForTheCity(Weather weather);

    void deleteTheCity(Long regionId) throws WeatherIsNotFoundException;
}

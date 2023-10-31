package org.projectweather.exceptions.dataBaseQuriesExceptions;

public class WeatherInCityIsNotFoundException extends RuntimeException{

    public WeatherInCityIsNotFoundException(Long id) {
        super(String.format("Объект WeatherInCity с id=%d не найден.", id));
    }

}

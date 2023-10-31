package org.projectweather.exceptions.dataBaseQuriesExceptions;

public class WeatherInCityIsAlreadyExistException extends RuntimeException{

    public WeatherInCityIsAlreadyExistException(Long id) {
        super(String.format("Объект WeatherInCity с id=%d уже существует.", id));
    }
}

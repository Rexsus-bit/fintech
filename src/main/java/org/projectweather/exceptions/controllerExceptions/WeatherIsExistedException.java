package org.projectweather.exceptions.controllerExceptions;

import java.time.LocalDateTime;

public class WeatherIsExistedException extends RuntimeException {

    public WeatherIsExistedException(Long regionId, LocalDateTime localDateTime) {
        super(String.format("Объект Weather с id=%d localDateTime=%s уже существует.", regionId, localDateTime));
    }

}

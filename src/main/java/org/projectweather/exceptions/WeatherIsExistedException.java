package org.projectweather.exceptions;

import java.time.ZonedDateTime;

public class WeatherIsExistedException extends Exception{

    public WeatherIsExistedException(Long regionId, ZonedDateTime zonedDateTime) {
        super(String.format("Объект Weather с id=%d zonedDateTime=%s уже существует.", regionId, zonedDateTime));
    }

}

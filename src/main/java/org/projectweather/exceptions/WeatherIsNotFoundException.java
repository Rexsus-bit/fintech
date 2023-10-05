package org.projectweather.exceptions;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class WeatherIsNotFoundException extends Exception {

    public WeatherIsNotFoundException(Long regionId, LocalDate localDate) {
        super(String.format("Объект Weather с id=%d и date=%s не найден.", regionId,
                localDate.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))));
    }

    public WeatherIsNotFoundException(Long regionId) {
        super(String.format("Объект Weather с id=%d не найден.", regionId));
    }

}

package org.projectweather;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;


/**
 * Служебный класс, в котором реализован метод, создающий список с тестовыми данными для проверки работы методов
 * из задания
 */
public class Util {

    public static List<Weather> makeTestList() {

        Weather moscowWeather1 = new Weather(1, "Moscow", 10,
                ZonedDateTime.of(LocalDateTime.of(2023, 9, 20, 20, 30),
                        ZoneId.of("Europe/Moscow")));
        Weather moscowWeather2 = new Weather(1, "Moscow", 16,
                ZonedDateTime.of(LocalDateTime.of(2023, 9, 21, 22, 30),
                        ZoneId.of("Europe/Moscow")));

        Weather newYorkWeather = new Weather(2, "New York", 25,
                ZonedDateTime.of(LocalDateTime.of(2023, 9, 22, 20, 30),
                        ZoneId.of("America/New_York")));
        Weather londonWeather1 = new Weather(3, "London", 15,
                ZonedDateTime.of(LocalDateTime.of(2023, 9, 23, 19, 30),
                        ZoneId.of("Europe/London")));
        Weather londonWeather2 = new Weather(3, "London", 22,
                ZonedDateTime.of(LocalDateTime.of(2023, 9, 24, 18, 30),
                        ZoneId.of("Europe/London")));
        Weather parisWeather1 = new Weather(4, "Paris", 14,
                ZonedDateTime.of(LocalDateTime.of(2023, 9, 25, 1, 30),
                        ZoneId.of("Europe/Paris")));
        Weather parisWeather2 = new Weather(4, "Paris", 10,
                ZonedDateTime.of(LocalDateTime.of(2023, 8, 22, 5, 30),
                        ZoneId.of("Europe/Paris")));

        return new ArrayList<Weather>() {
            {
                add(moscowWeather1);
                add(moscowWeather2);
                add(newYorkWeather);
                add(londonWeather1);
                add(londonWeather2);
                add(parisWeather1);
                add(parisWeather2);
            }
        };

    }
}

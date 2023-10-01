package org.projectweather.mapper;

import org.projectweather.model.Weather;
import org.projectweather.model.WeatherDto;

public class WeatherMapper {

    public static WeatherDto toWeatherDto(Weather weather) {
        return  new WeatherDto(weather.getRegionId(), weather.getRegionName(), weather.getTemperature(),
                weather.getZonedDateTime());
    }

    public static Weather fromWeatherDto(WeatherDto weatherDto, Long regionId) {
        return new Weather(regionId, weatherDto.getRegionName(), weatherDto.getTemperature(),
                weatherDto.getZonedDateTime());
    }
}

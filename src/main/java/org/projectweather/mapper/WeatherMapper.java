package org.projectweather.mapper;

import org.projectweather.model.Weather;
import org.projectweather.model.WeatherDto;
import org.projectweather.model.weatherApiDto.WeatherApiDto;
import org.projectweather.model.weatherInCity.City;
import org.projectweather.model.weatherInCity.WeatherInCity;
import org.projectweather.model.weatherInCity.WeatherType;

public class WeatherMapper {

    public static WeatherDto toWeatherDto(Weather weather) {
        return  new WeatherDto(weather.getRegionId(), weather.getRegionName(), weather.getTemperature(),
                weather.getLocalDateTime());
    }

    public static Weather fromWeatherDto(WeatherDto weatherDto, Long regionId) {
        return new Weather(regionId, weatherDto.getRegionName(), weatherDto.getTemperature(),
                weatherDto.getLocalDateTime());
    }

    public static WeatherInCity weatherApiDtoToWeatherInCity(WeatherApiDto weatherApiDto){
        return new WeatherInCity(null, new City(null, weatherApiDto.getLocation().getName()),
                new WeatherType(null, weatherApiDto.getLocation().getName()),
                weatherApiDto.getLocation().getUnixTime(), weatherApiDto.getCurrent().getTemperature());
    }
}

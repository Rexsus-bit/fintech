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
    public static WeatherInCity weatherApiToWeatherInCity(WeatherApiDto weatherApiDto) {
        return WeatherInCity.builder()
                .city(City.builder()
                        .name(weatherApiDto.getLocation().getName())
                        .build())
                .weatherType(WeatherType.builder()
                        .name(weatherApiDto.getCurrent().getCondition().getWeatherType())
                        .build())
                .unixDateTime(weatherApiDto.getLocation().getUnixTime())
                .build();
    }

    public static WeatherInCity weatherApiDtoToWeatherInCity(WeatherApiDto weatherApiDto){
        return new WeatherInCity(null, new City(null, weatherApiDto.getLocation().getName()),
                new WeatherType(null, weatherApiDto.getLocation().getName()),
                weatherApiDto.getLocation().getUnixTime());
    }
}

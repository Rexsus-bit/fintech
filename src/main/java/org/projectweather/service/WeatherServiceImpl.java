package org.projectweather.service;

import org.projectweather.exceptions.controllerExceptions.WeatherIsExistedException;
import org.projectweather.exceptions.controllerExceptions.WeatherIsNotFoundException;
import org.projectweather.model.Weather;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Service
public class WeatherServiceImpl implements WeatherService {

    private final List<Weather> dataStorageList = new ArrayList<>();

    @Override
    public List<Weather> getWeatherForTheCurrentDate(Long regionId) throws WeatherIsNotFoundException {
        List<Weather> weatherWithRequestedIdList = dataStorageList.stream().filter(o -> o.getRegionId() == regionId
                && o.getLocalDateTime().toLocalDate().getYear() == (LocalDateTime.now().getYear())
                && o.getLocalDateTime().toLocalDate().getDayOfYear() == (LocalDateTime.now().getDayOfYear())).toList();
        if (weatherWithRequestedIdList.size() == 0) {
            throw new WeatherIsNotFoundException(regionId, LocalDate.now());
        }
        return weatherWithRequestedIdList;
    }

    @Override
    public Weather createNewCity(Weather weather) throws WeatherIsExistedException {
        if (dataStorageList.stream().anyMatch(o -> o.getRegionId() == weather.getRegionId()
                && o.getRegionName().equals(weather.getRegionName())
                && o.getTemperature() == weather.getTemperature()
                && o.getLocalDateTime().equals(weather.getLocalDateTime()))) {
            throw new WeatherIsExistedException(weather.getRegionId(), weather.getLocalDateTime());
        }
        dataStorageList.add(weather);
        return weather;
    }

    @Override
    public Weather updateWeatherTemperatureForTheCity(Weather weather) {
        dataStorageList.removeIf(o -> o.getRegionId() == weather.getRegionId()
                && o.getRegionName().equals(weather.getRegionName())
                && o.getLocalDateTime().equals(weather.getLocalDateTime()));
        dataStorageList.add(weather);
        return weather;
    }

    @Override
    public void deleteTheCity(Long regionId) throws WeatherIsNotFoundException {
        if (dataStorageList.stream().filter(o -> o.getRegionId() == regionId).toList().size() == 0) {
            throw new WeatherIsNotFoundException(regionId);
        }
        dataStorageList.removeAll(dataStorageList.stream().filter(o -> o.getRegionId() == regionId).toList());
    }
}

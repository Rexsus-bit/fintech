package org.projectweather.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.projectweather.exceptions.controllerExceptions.WeatherIsExistedException;
import org.projectweather.exceptions.controllerExceptions.WeatherIsNotFoundException;
import org.projectweather.mapper.WeatherMapper;
import org.projectweather.model.WeatherDto;
import org.projectweather.service.WeatherService;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

import static org.projectweather.mapper.WeatherMapper.fromWeatherDto;
import static org.projectweather.mapper.WeatherMapper.toWeatherDto;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/api/wheather")
@Api("Контроллер для объектов Weather")

public class WeatherController {

    private final WeatherService weatherService;

    @GetMapping("{city}")
    @ApiOperation("Метод возвращает все объекты Weather с сегодняшней датой")
    List<WeatherDto> getWeatherForTheCurrentDate(@PathVariable("city") Long regionId)
            throws WeatherIsNotFoundException {
        return weatherService.getWeatherForTheCurrentDate(regionId).stream().map(WeatherMapper::toWeatherDto).toList();
    }

    @PostMapping("/{city}")
    @ApiOperation("Метод создает новый объект Weather")
    WeatherDto createNewCity(@PathVariable("city") Long regionId, @Valid @RequestBody WeatherDto weatherDto)
            throws WeatherIsExistedException {
        return toWeatherDto(weatherService.createNewCity(fromWeatherDto(weatherDto, regionId)));
    }

    @PutMapping("/{city}")
    @ApiOperation("Метод обновляет погоду при условии совпадения id и времени объекта Weather, " +
            "в противном случае добавляет новый объект")
    WeatherDto updateWeatherTemperatureForTheCity(@PathVariable("city") Long regionId,
                                                  @Valid @RequestBody WeatherDto weatherDto) {
        return toWeatherDto(weatherService.updateWeatherTemperatureForTheCity(fromWeatherDto(weatherDto, regionId)));
    }

    @ApiOperation("Метод удаляет все объекты Weather с указанным id")
    @DeleteMapping("/{city}")
    void deleteTheCity(@PathVariable("city") Long regionId) throws WeatherIsNotFoundException {
        weatherService.deleteTheCity(regionId);
    }

}
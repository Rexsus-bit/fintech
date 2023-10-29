package org.projectweather;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.projectweather.exceptions.controllerExceptions.WeatherIsNotFoundException;
import org.projectweather.model.weatherInCity.City;
import org.projectweather.model.weatherInCity.WeatherInCity;
import org.projectweather.model.weatherInCity.WeatherType;
import org.projectweather.repository.WeatherInCityJDBCRepository;
import org.projectweather.service.WeatherInCityService;
import org.projectweather.service.WeatherInCityServiceJDBCImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.support.TransactionTemplate;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.projectweather.util.RandomDataGenerator.*;

@ExtendWith(MockitoExtension.class)
@SpringBootTest(properties = {"weather.api.url=http://api.weatherapi.com"})
public class WeatherInCityServiceJDBCImplTests {

    @Autowired
    TransactionTemplate transactionTemplate;

    @Mock
    WeatherInCityJDBCRepository mockWeatherInCityJDBCRepository;

    WeatherInCityService weatherInCityService;

    SecureRandom rand;
    String cityName;
    String weatherTypeName;
    Instant unixTime;

    WeatherInCity weatherInCity1;
    WeatherInCity weatherInCity2;

    @BeforeEach
    void setup() {
        weatherInCityService = new WeatherInCityServiceJDBCImpl(mockWeatherInCityJDBCRepository, transactionTemplate);
        rand = new SecureRandom();
        cityName = getRandomString();
        weatherTypeName = getRandomString();
        unixTime = getRandomUnixTime();

        weatherInCity1 = new WeatherInCity(getRandomLongNumber(), new City(getRandomLongNumber(), cityName),
                new WeatherType(getRandomLongNumber(), weatherTypeName), unixTime);

        weatherInCity2 = new WeatherInCity(getRandomLongNumber(), new City(getRandomLongNumber(), cityName),
                new WeatherType(getRandomLongNumber(), weatherTypeName), unixTime);
    }

    @Test
    public void shouldCreateWeatherInCityTest() {
        Mockito.when(mockWeatherInCityJDBCRepository.createWeatherInCity(weatherInCity1))
                .thenReturn(weatherInCity1);

        WeatherInCity result = weatherInCityService.createWeatherInCity(weatherInCity1);

        assertEquals(weatherInCity1, result);
        Mockito.verify(mockWeatherInCityJDBCRepository, Mockito.times(1))
                .createWeatherInCity(weatherInCity1);
    }

    @Test
    public void shouldFindWeatherInCityByIdTest() {
        Mockito.when(mockWeatherInCityJDBCRepository.findWeatherInCityById(anyLong()))
                .thenAnswer(invocationOnMock -> {
                            long id = invocationOnMock.getArgument(0, Long.class);
                            if (id == weatherInCity1.getId()) {
                                return weatherInCity1;
                            } else if (id == weatherInCity2.getId()) {
                                return weatherInCity2;
                            }
                            return null;
                        }
                );
        WeatherInCity result1 = weatherInCityService.findWeatherInCityById(weatherInCity1.getId());
        assertEquals(weatherInCity1, result1);
        WeatherInCity result2 = weatherInCityService.findWeatherInCityById(weatherInCity2.getId());
        assertEquals(weatherInCity2, result2);
        Mockito.verify(mockWeatherInCityJDBCRepository, Mockito.times(2))
                .findWeatherInCityById(anyLong());
    }

    @Test
    public void shouldFailFindWeatherInCityByIdTest() {
        long id = getRandomLongNumber();
        Mockito.when(mockWeatherInCityJDBCRepository.findWeatherInCityById(id))
                .thenThrow(new WeatherIsNotFoundException(id));
        final WeatherIsNotFoundException exception = Assertions.assertThrows(
                WeatherIsNotFoundException.class,
                () -> weatherInCityService.findWeatherInCityById(id));
        Assertions.assertEquals(String.format("Объект Weather с id=%d не найден.", id), exception.getMessage());
    }

    @Test
    public void shouldFindAllWeatherInCityTest() {
        List<WeatherInCity> list = List.of(weatherInCity1, weatherInCity2);
        Mockito.when(mockWeatherInCityJDBCRepository.findAllWeatherInCity()).thenReturn(list);
        List<WeatherInCity> resultList = weatherInCityService.findAllWeatherInCity();
        assertEquals(list, resultList);
        Mockito.verify(mockWeatherInCityJDBCRepository, Mockito.times(1))
                .findAllWeatherInCity();
    }

    @Test
    void shouldUpdateWeatherInCityTest() {
        Mockito.when(mockWeatherInCityJDBCRepository.updateWeatherInCity(weatherInCity1))
                .thenReturn(weatherInCity1);
        WeatherInCity result = weatherInCityService.updateWeatherInCity(weatherInCity1);
        assertEquals(weatherInCity1, result);
        Mockito.verify(mockWeatherInCityJDBCRepository, Mockito.times(1))
                .updateWeatherInCity(weatherInCity1);
    }

    @Test
    void shouldDeleteWeatherInCityTest() {
        long id = getRandomLongNumber();
        Mockito.doNothing().when(mockWeatherInCityJDBCRepository).deleteWeatherInCityById(id);
        weatherInCityService.deleteWeatherInCityById(id);
        Mockito.verify(mockWeatherInCityJDBCRepository, Mockito.times(1))
                .deleteWeatherInCityById(id);
    }
}

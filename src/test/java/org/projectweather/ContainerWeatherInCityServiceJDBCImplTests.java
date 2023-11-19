package org.projectweather;

import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.mockito.Mockito;
import org.projectweather.client.WeatherRestClient;
import org.projectweather.exceptions.dataBaseQuriesExceptions.WeatherInCityIsNotFoundException;
import org.projectweather.model.weatherApiDto.WeatherApiDto;
import org.projectweather.model.weatherInCity.City;
import org.projectweather.model.weatherInCity.WeatherInCity;
import org.projectweather.model.weatherInCity.WeatherType;
import org.projectweather.repository.WeatherInCityJDBCRepository;
import org.projectweather.service.WeatherInCityServiceJDBCImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

import static java.time.temporal.ChronoField.INSTANT_SECONDS;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.projectweather.mapper.WeatherMapper.weatherApiDtoToWeatherInCity;
import static org.projectweather.util.RandomDataGenerator.*;

@SpringBootTest
@Testcontainers
public class ContainerWeatherInCityServiceJDBCImplTests {

    @Container
    private final static GenericContainer<?> h2Container = new GenericContainer<>("oscarfonts/h2")
            .withEnv("H2_OPTIONS", "-ifNotExists")
            .withExposedPorts(1521)
            .waitingFor(Wait.defaultWaitStrategy());

    @DynamicPropertySource
    private static void dbProperties(DynamicPropertyRegistry registry) {
        String url = "jdbc:h2:tcp://" + h2Container.getHost() + ":" + h2Container.getFirstMappedPort() + "/test";
        registry.add("spring.datasource.url", () -> url);
        registry.add("spring.datasource.username", () -> "sa");
        registry.add("spring.datasource.password", () -> "");
    }

    @Autowired
    private WeatherInCityServiceJDBCImpl weatherInCityService;

    @SpyBean
    private WeatherInCityJDBCRepository weatherInCityJDBCRepository;

    @Autowired
    private WeatherRestClient weatherRestClient;

    private WeatherInCity weatherInCity1;
    private WeatherInCity weatherInCity2;
    private String cityName;

    @BeforeEach
    void setUp() {
        weatherInCity1 = new WeatherInCity(null, new City(null, getRandomString()),
                new WeatherType(null, getRandomString()), getRandomUnixTime());

        weatherInCity2 = new WeatherInCity(null, new City(null, getRandomString()),
                new WeatherType(null, getRandomString()), getRandomUnixTime());
    }

    @Test
    void shouldTakeDataFromWeatherApiServiceAndCreateWeatherInCityTest() {
        cityName = getRandomCityName();
        WeatherApiDto weatherApiDto = weatherRestClient.requestWeather(cityName);
        WeatherInCity weatherInCity = weatherApiDtoToWeatherInCity(weatherApiDto);
        weatherInCityService.createWeatherInCity(weatherInCity);

        Assertions.assertTrue(weatherInCityService.findAllWeatherInCity().stream().anyMatch(o ->
                o.getCity().getName().equals(weatherInCity.getCity().getName()) &&
                        o.getWeatherType().getName().equals(weatherInCity.getWeatherType().getName()) &&
                        o.getUnixDateTime().getLong(INSTANT_SECONDS) == (weatherInCity.getUnixDateTime()
                                .getLong(INSTANT_SECONDS))));
        Mockito.verify(weatherInCityJDBCRepository, Mockito.times(1)).createWeatherInCity(any());
    }

    @Test
    void shouldCreateWeatherInCityTest() {
        weatherInCityService.createWeatherInCity(weatherInCity1);
        Assertions.assertTrue(weatherInCityService.findAllWeatherInCity().stream().anyMatch(o ->
                o.getCity().getName().equals(weatherInCity1.getCity().getName()) &&
                        o.getWeatherType().getName().equals(weatherInCity1.getWeatherType().getName()) &&
                        o.getUnixDateTime().getLong(INSTANT_SECONDS) == (weatherInCity1.getUnixDateTime()
                                .getLong(INSTANT_SECONDS))));
        Mockito.verify(weatherInCityJDBCRepository, Mockito.times(1)).createWeatherInCity(any());
    }

    @Test
    void shouldFailCreateWeatherInCityTest() {
        Assert.assertThrows(NullPointerException.class, () -> weatherInCityService.createWeatherInCity(null));
    }

    @Test
    void shouldFindWeatherInCityByIdTest() {
        weatherInCityService.createWeatherInCity(weatherInCity1);
        Long id = weatherInCityService.findAllWeatherInCity().stream().filter(o ->
                o.getCity().getName().equals(weatherInCity1.getCity().getName()) &&
                        o.getWeatherType().getName().equals(weatherInCity1.getWeatherType().getName()) &&
                        o.getUnixDateTime().equals(weatherInCity1.getUnixDateTime())).toList().get(0).getId();
        WeatherInCity result = weatherInCityService.findWeatherInCityById(id);

        Assertions.assertEquals(weatherInCity1.getWeatherType().getName(), result.getWeatherType().getName());
        Assertions.assertEquals(weatherInCity1.getUnixDateTime(), result.getUnixDateTime());
        Assertions.assertEquals(weatherInCity1.getCity().getName(), result.getCity().getName());
        Mockito.verify(weatherInCityJDBCRepository, Mockito.times(1))
                .findWeatherInCityById(anyLong());
    }

    @Test
    void shouldFailFindWeatherInCityByIdTest() {
        weatherInCityService.createWeatherInCity(weatherInCity1);
        Long id = weatherInCityService.findAllWeatherInCity().stream().filter(o ->
                        o.getCity().getName().equals(weatherInCity1.getCity().getName()) &&
                                o.getWeatherType().getName().equals(weatherInCity1.getWeatherType().getName()) &&
                                o.getUnixDateTime().equals(weatherInCity1.getUnixDateTime())).toList()
                .get(0).getId();
        weatherInCityService.deleteWeatherInCityById(id);

        Assertions.assertThrows(WeatherInCityIsNotFoundException.class,
                () -> weatherInCityService.findWeatherInCityById(id));
    }

    @Test
    void shouldFindAllWeatherInCityTest() {
        weatherInCityService.createWeatherInCity(weatherInCity1);
        weatherInCityService.createWeatherInCity(weatherInCity2);
        List<WeatherInCity> list = weatherInCityService.findAllWeatherInCity();

        boolean isWeatherInCity1InList = list.stream().anyMatch(o ->
                o.getCity().getName().equals(weatherInCity1.getCity().getName()) &&
                        o.getWeatherType().getName().equals(weatherInCity1.getWeatherType().getName()) &&
                        o.getUnixDateTime().equals(weatherInCity1.getUnixDateTime()));
        boolean isWeatherInCity2InList = list.stream().anyMatch(o ->
                o.getCity().getName().equals(weatherInCity2.getCity().getName()) &&
                        o.getWeatherType().getName().equals(weatherInCity2.getWeatherType().getName()) &&
                        o.getUnixDateTime().equals(weatherInCity2.getUnixDateTime()));

        Assertions.assertTrue(isWeatherInCity1InList);
        Assertions.assertTrue(isWeatherInCity2InList);
        Mockito.verify(weatherInCityJDBCRepository, Mockito.times(1)).findAllWeatherInCity();
    }

    @Test
    void shouldUpdateWeatherInCityTest() {
        weatherInCityService.createWeatherInCity(weatherInCity1);
        WeatherInCity weatherInCityForUpdate = weatherInCityService.findAllWeatherInCity().stream().filter(o ->
                o.getCity().getName().equals(weatherInCity1.getCity().getName()) &&
                        o.getWeatherType().getName().equals(weatherInCity1.getWeatherType().getName()) &&
                        o.getUnixDateTime().equals(weatherInCity1.getUnixDateTime())).toList().get(0);
        WeatherType newWeatherType = new WeatherType(null, getRandomString());
        weatherInCityForUpdate.setWeatherType(newWeatherType);
        weatherInCityService.updateWeatherInCity(weatherInCityForUpdate);
        String updatedWeatherTypeName = weatherInCityService.findWeatherInCityById(weatherInCityForUpdate.getId())
                .getWeatherType().getName();

        Assertions.assertEquals(newWeatherType.getName(), updatedWeatherTypeName);
        Mockito.verify(weatherInCityJDBCRepository, Mockito.times(1))
                .updateWeatherInCity(any(WeatherInCity.class));
    }

    @Test
    void shouldFailUpdateWeatherInCityTest() {
        weatherInCity1.setId(getRandomNumberBetweenValues(10000, 100000));
        Assert.assertThrows(WeatherInCityIsNotFoundException.class, () -> weatherInCityService
                .updateWeatherInCity(weatherInCity1));
    }

    @Test
    void shouldDeleteWeatherInCityTest() {
        weatherInCityService.createWeatherInCity(weatherInCity1);
        Long id = weatherInCityService.findAllWeatherInCity().stream().filter(o ->
                        o.getCity().getName().equals(weatherInCity1.getCity().getName()) &&
                                o.getWeatherType().getName().equals(weatherInCity1.getWeatherType().getName()) &&
                                o.getUnixDateTime().equals(weatherInCity1.getUnixDateTime())).toList()
                .get(0).getId();
        weatherInCityService.deleteWeatherInCityById(id);

        Assertions.assertFalse(weatherInCityService.findAllWeatherInCity().stream().anyMatch(o -> o.getId().equals(id)));
        Mockito.verify(weatherInCityJDBCRepository, Mockito.times(1)).deleteWeatherInCityById(anyLong());
    }

    @Test
    void shouldFailDeleteWeatherInCityTest() {
        weatherInCityService.createWeatherInCity(weatherInCity1);

        Long id = weatherInCityService.findAllWeatherInCity().stream().filter(o ->
                        o.getCity().getName().equals(weatherInCity1.getCity().getName()) &&
                                o.getWeatherType().getName().equals(weatherInCity1.getWeatherType().getName()) &&
                                o.getUnixDateTime().equals(weatherInCity1.getUnixDateTime())).toList().get(0).getId();
        weatherInCityService.deleteWeatherInCityById(id);

        Assertions.assertThrows(WeatherInCityIsNotFoundException.class
                , () -> weatherInCityService.findWeatherInCityById(id));
    }

}

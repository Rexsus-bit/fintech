package org.projectweather;

import org.junit.Assert;
import org.junit.jupiter.api.*;
import org.projectweather.client.WeatherRestClient;
import org.projectweather.exceptions.dataBaseQuriesExceptions.WeatherInCityIsAlreadyExistException;
import org.projectweather.exceptions.dataBaseQuriesExceptions.WeatherInCityIsNotFoundException;
import org.projectweather.model.weatherApiDto.WeatherApiDto;
import org.projectweather.model.weatherInCity.City;
import org.projectweather.model.weatherInCity.WeatherInCity;
import org.projectweather.model.weatherInCity.WeatherType;
import org.projectweather.service.WeatherInCityServiceJPAImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

import static java.time.temporal.ChronoField.INSTANT_SECONDS;
import static org.projectweather.mapper.WeatherMapper.weatherApiDtoToWeatherInCity;
import static org.projectweather.util.RandomDataGenerator.*;

@SpringBootTest(properties = {"weather.api.url=http://api.weatherapi.com"})
@Testcontainers
public class ContainerWeatherInCityServiceJPAImplTests {

    @Autowired
    private WeatherInCityServiceJPAImpl weatherInCityService;

    @Container
    public static GenericContainer<?> h2Container = new GenericContainer<>("oscarfonts/h2")
            .withEnv("H2_OPTIONS", "-ifNotExists")
            .withExposedPorts(1521)
            .waitingFor(Wait.defaultWaitStrategy());

    @DynamicPropertySource
    static void dbProperties(DynamicPropertyRegistry registry) {
        String url = "jdbc:h2:tcp://" + h2Container.getHost() + ":" + h2Container.getFirstMappedPort() + "/test";
        registry.add("spring.datasource.url", () -> url);
        registry.add("spring.datasource.username", () -> "sa");
        registry.add("spring.datasource.password", () -> "");
    }

    @Autowired
    WeatherRestClient weatherRestClient;
    WeatherInCity weatherInCity1;
    WeatherInCity weatherInCity2;
    String cityName;

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
    }

    @Test
    void shouldCreateWeatherInCityTest() {
        weatherInCityService.createWeatherInCity(weatherInCity1);

        Assertions.assertTrue(weatherInCityService.findAllWeatherInCity().stream().anyMatch(o ->
                o.getCity().getName().equals(weatherInCity1.getCity().getName()) &&
                        o.getWeatherType().getName().equals(weatherInCity1.getWeatherType().getName()) &&
                        o.getUnixDateTime().equals(weatherInCity1.getUnixDateTime())));
    }

    @Test
    void shouldFailCreateWeatherInCityTest() {
        weatherInCityService.createWeatherInCity(weatherInCity1);
        Long id = weatherInCityService.findAllWeatherInCity().stream().filter(o ->
                        o.getCity().getName().equals(weatherInCity1.getCity().getName()) &&
                                o.getWeatherType().getName().equals(weatherInCity1.getWeatherType().getName()) &&
                                o.getUnixDateTime().equals(weatherInCity1.getUnixDateTime())).toList().get(0).getId();
        weatherInCity1.setId(id);

        Assert.assertThrows(WeatherInCityIsAlreadyExistException.class, () -> weatherInCityService
                .createWeatherInCity(weatherInCity1));
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
    }

    @Test
    void shouldFailFindWeatherInCityByIdTest() {
        weatherInCityService.createWeatherInCity(weatherInCity1);

        Long id = weatherInCityService.findAllWeatherInCity().stream().filter(o ->
                        o.getCity().getName().equals(weatherInCity1.getCity().getName()) &&
                                o.getWeatherType().getName().equals(weatherInCity1.getWeatherType().getName()) &&
                                o.getUnixDateTime().equals(weatherInCity1.getUnixDateTime())).toList().get(0).getId();
        weatherInCityService.deleteWeatherInCityById(id);

        Assertions.assertThrows(WeatherInCityIsNotFoundException.class
                , () -> weatherInCityService.findWeatherInCityById(id));
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
    }

    @Test
    void shouldFailUpdateWeatherInCityTest() {
        weatherInCity1.setId(getRandomNumberBetweenValues(10000, 100000));
        Assert.assertThrows(WeatherInCityIsNotFoundException.class,
                () -> weatherInCityService.updateWeatherInCity(weatherInCity1));
    }

    @Test
    void shouldDeleteWeatherInCityTest() {
        weatherInCityService.createWeatherInCity(weatherInCity1);
        Long id = weatherInCityService.findAllWeatherInCity().stream().filter(o ->
                o.getCity().getName().equals(weatherInCity1.getCity().getName()) &&
                        o.getWeatherType().getName().equals(weatherInCity1.getWeatherType().getName()) &&
                        o.getUnixDateTime().equals(weatherInCity1.getUnixDateTime())).toList().get(0).getId();
        weatherInCityService.deleteWeatherInCityById(id);

        Assertions.assertFalse(weatherInCityService.findAllWeatherInCity().stream().anyMatch(o -> o.getId().equals(id)));
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



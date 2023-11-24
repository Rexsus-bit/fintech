package org.projectweather;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.projectweather.cache.WeatherCache;
import org.projectweather.model.weatherInCity.City;
import org.projectweather.model.weatherInCity.WeatherInCity;
import org.projectweather.model.weatherInCity.WeatherType;
import org.projectweather.repository.WeatherInCityJpaRepository;
import org.projectweather.service.WeatherInCityServiceJPAImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.projectweather.util.RandomDataGenerator.getRandomDoubleNumber;
import static org.projectweather.util.RandomDataGenerator.getRandomString;
import static org.projectweather.util.RandomDataGenerator.getRandomUnixTime;


@SpringBootTest
@Testcontainers
public class ContainerWeatherInCityCacheTests {

    @Autowired
    private WeatherInCityServiceJPAImpl weatherInCityService;

    @SpyBean
    private WeatherCache weatherCache;

    @SpyBean
    private WeatherInCityJpaRepository weatherInCityJpaRepository;


    @Container
    static GenericContainer<?> h2Container = new GenericContainer<>("oscarfonts/h2")
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

    static private WeatherInCity weatherInCity1;
    static private WeatherInCity weatherInCity2;
    static private WeatherInCity weatherInCity3;
    static private WeatherInCity weatherInCity4;


    @BeforeAll
    static void setUp() {
        weatherInCity1 = new WeatherInCity(null, new City(null, getRandomString()),
                new WeatherType(null, getRandomString()), getRandomUnixTime(), getRandomDoubleNumber(-100, 100));
        weatherInCity2 = new WeatherInCity(null, new City(null, getRandomString()),
                new WeatherType(null, getRandomString()), getRandomUnixTime(), getRandomDoubleNumber(-100, 100));
        weatherInCity3 = new WeatherInCity(null, new City(null, getRandomString()),
                new WeatherType(null, getRandomString()), getRandomUnixTime(), getRandomDoubleNumber(-100, 100));
        weatherInCity4 = new WeatherInCity(null, new City(null, getRandomString()),
                new WeatherType(null, getRandomString()), getRandomUnixTime(), getRandomDoubleNumber(-100, 100));
    }

    @BeforeEach
    void clearData(){
        weatherCache.clear();
    }

    @Test
    void shouldSaveWeatherInCacheTest() {
        weatherInCity1 = weatherInCityService.createWeatherInCity(weatherInCity1);
        weatherInCityService.findWeatherInCityById(weatherInCity1.getId());
        Mockito.verify(weatherCache, Mockito.times(1)).save(any());
    }

    @Test
    void shouldGetWeatherFromCacheTest() {
        weatherInCity2 = weatherInCityService.createWeatherInCity(weatherInCity2);
        weatherCache.save(weatherInCity2);

        weatherInCityService.findWeatherInCityById(weatherInCity2.getId());

        Mockito.verify(weatherCache, Mockito.times(1)).get(anyLong());
        Mockito.verify(weatherInCityJpaRepository, Mockito.times(0)).findById(any());
    }

    @Test
    void shouldDeleteWeatherFromCacheOnWeatherUpdateTest() {
        weatherInCity3 = weatherInCityService.createWeatherInCity(weatherInCity3);
        weatherCache.save(weatherInCity3);

        weatherInCity3.getCity().setName(getRandomString());
        weatherInCityService.updateWeatherInCity(weatherInCity3);

        Mockito.verify(weatherCache, Mockito.times(1)).remove(anyLong());
        Assertions.assertTrue(weatherCache.get(weatherInCity3.getId()).isEmpty());
    }

    @Test
    void shouldDeleteWeatherFromCacheOnWeatherDeleteTest() {
        weatherInCity4 = weatherInCityService.createWeatherInCity(weatherInCity4);
        weatherCache.save(weatherInCity4);

        weatherInCityService.deleteWeatherInCityById(weatherInCity4.getId());

        Mockito.verify(weatherCache, Mockito.times(1)).remove(anyLong());
        Assertions.assertTrue(weatherCache.get(weatherInCity4.getId()).isEmpty());
    }

}
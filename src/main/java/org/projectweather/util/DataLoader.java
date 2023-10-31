package org.projectweather.util;

import org.projectweather.client.WeatherRestClient;
import org.projectweather.model.weatherApiDto.WeatherApiDto;
import org.projectweather.model.weatherInCity.WeatherInCity;
import org.projectweather.service.WeatherInCityServiceJDBCImpl;
import org.projectweather.service.WeatherInCityServiceJPAImpl;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

import java.util.stream.Stream;

import static org.projectweather.mapper.WeatherMapper.weatherApiToWeatherInCity;

@Component
public class DataLoader {

    private final WeatherRestClient weatherRestClient;
    private final WeatherInCityServiceJPAImpl weatherInCityServiceJPAImpl;
    private final WeatherInCityServiceJDBCImpl weatherInCityServiceJDBC;

    public DataLoader(WeatherRestClient weatherRestClient, WeatherInCityServiceJPAImpl weatherInCityServiceJPAImpl
            , WeatherInCityServiceJDBCImpl weatherInCityServiceJDBC) {
        this.weatherRestClient = weatherRestClient;
        this.weatherInCityServiceJPAImpl = weatherInCityServiceJPAImpl;
        this.weatherInCityServiceJDBC = weatherInCityServiceJDBC;
    }

    @PostConstruct
    public void loadDataIntoDataBaseOnStartup() {
            loadDataJPAImp();
            loadDataJDBCImp();
    }

    public void loadDataJDBCImp() {
        Stream<String> citiesStream = Stream.of("Cardiff", "Moscow", "Saint Petersburg");
        citiesStream.forEach(cityName -> {
            WeatherApiDto weatherApiDto = weatherRestClient.requestWeather(cityName);
            WeatherInCity weatherInCity = weatherApiToWeatherInCity(weatherApiDto);
            weatherInCityServiceJDBC.createWeatherInCity(weatherInCity);
        });
    }

    public void loadDataJPAImp() {
        Stream<String> citiesStream = Stream.of("Paris", "London", "Washington");
        citiesStream.forEach(cityName -> {
            WeatherApiDto weatherApiDto = weatherRestClient.requestWeather(cityName);
            WeatherInCity weatherInCity = weatherApiToWeatherInCity(weatherApiDto);
            weatherInCityServiceJPAImpl.createWeatherInCity(weatherInCity);
        });
    }


}

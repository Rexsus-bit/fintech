package org.projectweather;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.projectweather.client.WeatherRestClient;
import org.projectweather.exceptions.clientExceptions.ApiKeyIsNotProvidedException;
import org.projectweather.model.weatherApiDto.WeatherApiDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;


@SpringBootTest(properties = { "weather.api.url=http://127.0.0.1:8085" })
@AutoConfigureWireMock(port = 8085)
public class WeatherRestClientTests {


    @Autowired
    private WeatherRestClient weatherRestClient;

    @Test
    public void ShouldReturnWeatherApiDtoTest() {
        WeatherApiDto weatherApiDtoResult = weatherRestClient.requestWeather("London");

        Assertions.assertEquals("London", weatherApiDtoResult.getLocation().getName());
        Assertions.assertEquals("City of London, Greater London", weatherApiDtoResult.getLocation().getRegion());
        Assertions.assertEquals("United Kingdom", weatherApiDtoResult.getLocation().getCountry());
        Assertions.assertNotNull(weatherApiDtoResult.getLocation().getTz_id());
        Assertions.assertNotNull(weatherApiDtoResult.getLocation().getUnixTime());
        Assertions.assertNotNull(weatherApiDtoResult.getCurrent().getTemperature());
        Assertions.assertFalse(weatherApiDtoResult.getCurrent().getCondition().getWeatherType().isBlank());
    }

    @Test()
    public void ShouldReturnWeatherApiErrorDtoTest() {
        ApiKeyIsNotProvidedException exception = Assertions.assertThrows(ApiKeyIsNotProvidedException.class
                , () -> weatherRestClient.requestWeather("UnknownCity"));
        Assertions.assertEquals("Произошла ошибка № 1002. Мы уже работаем над устранением проблемы.", exception.getMessage());
    }


}

package org.projectweather;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.projectweather.controller.WeatherController;
import org.projectweather.exceptions.controllerExceptions.WeatherIsExistedException;
import org.projectweather.exceptions.controllerExceptions.WeatherIsNotFoundException;
import org.projectweather.mapper.WeatherMapper;
import org.projectweather.model.Weather;
import org.projectweather.model.WeatherDto;
import org.projectweather.service.WeatherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.projectweather.mapper.WeatherMapper.toWeatherDto;
import static org.projectweather.util.RandomDataGenerator.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = WeatherController.class)
public class WeatherControllerTests {

    @MockBean
    WeatherService weatherService;

    @MockBean
    WeatherMapper weatherMapper;

    @Autowired
    ObjectMapper mapper;

    @Autowired
    MockMvc mvc;

    Weather weather1;
    WeatherDto weatherDto1;
    Weather weather2;
    List<Weather> list;

    @BeforeEach
    void setUp() {
        weather1 = new Weather(getRandomLongNumber(), getRandomString(), (int) getRandomNumberBetweenValues(-50, 50),
                getRandomLocalDateTime());
        weatherDto1 = toWeatherDto(weather1);
        weather2 = new Weather(getRandomLongNumber(), getRandomString(), (int) getRandomNumberBetweenValues(-50, 50),
                getRandomLocalDateTime());
        list = List.of(weather1, weather2);
    }

    @Test
    void shouldCreateWeatherTest() throws Exception {
        when(weatherService.createNewCity(weather1)).thenReturn(weather1);
        mvc.perform(post("/api/wheather/{city}", weather1.getRegionId())
                        .content(mapper.writeValueAsString(weatherDto1))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.regionId", is(weatherDto1.getRegionId()), Long.class))
                .andExpect(jsonPath("$.regionName", is(weatherDto1.getRegionName()), String.class))
                .andExpect(jsonPath("$.temperature", is(weatherDto1.getTemperature()), Integer.class))
                .andExpect(jsonPath("$.localDateTime",is(weatherDto1.getLocalDateTime()
                        .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))));
    }

    @Test
    void shouldFailCreateWeatherTest() throws Exception {
        when(weatherService.createNewCity(weather1))
                .thenThrow(new WeatherIsExistedException(weather1.getRegionId(), weather1.getLocalDateTime()));
        mvc.perform(post("/api/wheather/{city}", weatherDto1.getRegionId())
                        .content(mapper.writeValueAsString(weatherDto1))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message",
                        is(String.format("Объект Weather с id=%d localDateTime=%s уже существует.",
                                weather1.getRegionId(), weather1.getLocalDateTime())), String.class));
    }

    @Test
    void ShouldGetWeatherForTheCurrentDateTest() throws Exception {
        when(weatherService.getWeatherForTheCurrentDate(anyLong())).thenReturn(list);
        mvc.perform(get("/api/wheather/{city}", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$.[0].regionId", is(weatherDto1.getRegionId()), Long.class))
                .andExpect(jsonPath("$.[0].regionName", is(weatherDto1.getRegionName()), String.class))
                .andExpect(jsonPath("$.[0].temperature", is(weatherDto1.getTemperature()), Integer.class))
                .andExpect(jsonPath("$.[0].localDateTime",is(weatherDto1.getLocalDateTime()
                        .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))));
    }

    @Test
    void ShouldFailGetWeatherForTheCurrentDateTest() throws Exception {
        when(weatherService.getWeatherForTheCurrentDate(anyLong()))
                .thenThrow(new WeatherIsNotFoundException(weather1.getRegionId(), weather1.getLocalDateTime().toLocalDate()));
        mvc.perform(get("/api/wheather/{city}", weather1.getRegionId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message",
                        is(String.format("Объект Weather с id=%d и date=%s не найден.",
                        weather1.getRegionId(), weather1.getLocalDateTime().toLocalDate()
                                .format(DateTimeFormatter.ofPattern("dd.MM.yyyy")))), String.class));
    }

    @Test
    void ShouldUpdateWeatherTemperatureForTheCity() throws Exception {
        when(weatherService.updateWeatherTemperatureForTheCity(weather1)).thenReturn(weather1);
        mvc.perform(put("/api/wheather/{city}", weatherDto1.getRegionId())
                        .content(mapper.writeValueAsString(weatherDto1))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.regionId", is(weatherDto1.getRegionId()), Long.class))
                .andExpect(jsonPath("$.regionName", is(weatherDto1.getRegionName()), String.class))
                .andExpect(jsonPath("$.temperature", is(weatherDto1.getTemperature()), Integer.class))
                .andExpect(jsonPath("$.localDateTime",is(weatherDto1.getLocalDateTime()
                        .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))));
    }

    @Test
    void ShouldFailUpdateWeatherTemperatureForTheCity() throws Exception {
        when(weatherService.updateWeatherTemperatureForTheCity(weather1))
                .thenThrow(new WeatherIsNotFoundException(weather1.getRegionId()));
        mvc.perform(put("/api/wheather/{city}", weather1.getRegionId())
                        .content(mapper.writeValueAsString(weatherDto1))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is(String.format("Объект Weather с id=%d не найден.",
                        weather1.getRegionId())), String.class));
    }

    @Test
    void ShouldDeleteTheCity() throws Exception {
        Mockito.doNothing().when(weatherService).deleteTheCity(weather1.getRegionId());
        mvc.perform(delete("/api/wheather/{city}", weather1.getRegionId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isOk());
    }

    @Test
    void ShouldFailDeleteTheCity() throws Exception {
        Mockito.doThrow(new WeatherIsNotFoundException(weather1.getRegionId()))
                .when(weatherService).deleteTheCity(weather1.getRegionId());
        mvc.perform(delete("/api/wheather/{city}", weather1.getRegionId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is(String.format("Объект Weather с id=%d не найден.",
                        weather1.getRegionId())), String.class));
    }

}

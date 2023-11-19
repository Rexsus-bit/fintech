package org.projectweather;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.projectweather.model.Weather;
import org.projectweather.model.WeatherDto;
import org.projectweather.model.user.Role;
import org.projectweather.service.WeatherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.projectweather.mapper.WeatherMapper.toWeatherDto;
import static org.projectweather.util.RandomDataGenerator.getRandomLocalDateTime;
import static org.projectweather.util.RandomDataGenerator.getRandomLongNumber;
import static org.projectweather.util.RandomDataGenerator.getRandomNumberBetweenValues;
import static org.projectweather.util.RandomDataGenerator.getRandomString;
import static org.projectweather.util.RequestPostProcessorGenerator.getRequestPostProcessor;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AuthorizationWeatherControllerTests {

    @MockBean
    private WeatherService weatherService;

    @Autowired
    private JdbcUserDetailsManager jdbcUserDetailsManager;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private MockMvc mvc;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Value("${ADMIN_NAME}")
    private String adminName;

    @Value("${ADMIN_PASSWORD}")
    private String adminPassword;

    private Weather weather1;
    private WeatherDto weatherDto1;
    private Weather weather2;
    private List<Weather> list;
    private String userName;
    private String userPassword;

    @BeforeEach
    void setUp() {
        weather1 = new Weather(getRandomLongNumber(), getRandomString(), (int) getRandomNumberBetweenValues(-50, 50),
                getRandomLocalDateTime());
        weatherDto1 = toWeatherDto(weather1);
        weather2 = new Weather(getRandomLongNumber(), getRandomString(), (int) getRandomNumberBetweenValues(-50, 50),
                getRandomLocalDateTime());
        list = List.of(weather1, weather2);

        userName = getRandomString();
        userPassword = getRandomString();
        jdbcUserDetailsManager
                .createUser(User.builder()
                        .username(userName)
                        .password(passwordEncoder.encode(userPassword))
                        .roles(Role.USER.name())
                        .build());
    }

    @Test
    void shouldCreateWeatherTest() throws Exception {
        when(weatherService.createNewCity(weather1)).thenReturn(weather1);
        RequestPostProcessor requestPostProcessor = getRequestPostProcessor(adminName, adminPassword);

        mvc.perform(post("/api/wheather/{city}", weather1.getRegionId())
                        .content(mapper.writeValueAsString(weatherDto1))
                        .with(requestPostProcessor)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.regionId", is(weatherDto1.getRegionId()), Long.class))
                .andExpect(jsonPath("$.regionName", is(weatherDto1.getRegionName()), String.class))
                .andExpect(jsonPath("$.temperature", is(weatherDto1.getTemperature()), Integer.class))
                .andExpect(jsonPath("$.localDateTime", is(weatherDto1.getLocalDateTime()
                        .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))));
    }

    @Test
    void shouldFailCreateWeatherTest() throws Exception {
        when(weatherService.createNewCity(weather1)).thenReturn(weather1);
        RequestPostProcessor requestPostProcessor = getRequestPostProcessor(userName, userPassword);

        mvc.perform(post("/api/wheather/{city}", weatherDto1.getRegionId())
                        .content(mapper.writeValueAsString(weatherDto1))
                        .with(requestPostProcessor)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isForbidden());
    }

    @Test
    void ShouldGetWeatherForTheCurrentDateTest() throws Exception {
        when(weatherService.getWeatherForTheCurrentDate(anyLong())).thenReturn(list);
        RequestPostProcessor requestPostProcessor = getRequestPostProcessor(userName, userPassword);

        mvc.perform(get("/api/wheather/{city}", 1)
                        .with(requestPostProcessor)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$.[0].regionId", is(weatherDto1.getRegionId()), Long.class))
                .andExpect(jsonPath("$.[0].regionName", is(weatherDto1.getRegionName()), String.class))
                .andExpect(jsonPath("$.[0].temperature", is(weatherDto1.getTemperature()), Integer.class))
                .andExpect(jsonPath("$.[0].localDateTime", is(weatherDto1.getLocalDateTime()
                        .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))));
    }

    @Test
    void ShouldFailGetWeatherForTheCurrentDateTest() throws Exception {
        when(weatherService.getWeatherForTheCurrentDate(anyLong())).thenReturn(list);

        userName = getRandomString();
        userPassword = getRandomString();
        RequestPostProcessor requestPostProcessor = getRequestPostProcessor(userName, userPassword);

        mvc.perform(get("/api/wheather/{city}", weather1.getRegionId())
                        .with(requestPostProcessor)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void ShouldUpdateWeatherTemperatureForTheCity() throws Exception {
        when(weatherService.updateWeatherTemperatureForTheCity(weather1)).thenReturn(weather1);
        RequestPostProcessor requestPostProcessor = getRequestPostProcessor(adminName, adminPassword);

        mvc.perform(put("/api/wheather/{city}", weatherDto1.getRegionId())
                        .with(requestPostProcessor)
                        .content(mapper.writeValueAsString(weatherDto1))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.regionId", is(weatherDto1.getRegionId()), Long.class))
                .andExpect(jsonPath("$.regionName", is(weatherDto1.getRegionName()), String.class))
                .andExpect(jsonPath("$.temperature", is(weatherDto1.getTemperature()), Integer.class))
                .andExpect(jsonPath("$.localDateTime", is(weatherDto1.getLocalDateTime()
                        .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))));
    }

    @Test
    void ShouldFailUpdateWeatherTemperatureForTheCity() throws Exception {
        when(weatherService.updateWeatherTemperatureForTheCity(weather1)).thenReturn(weather1);
        RequestPostProcessor requestPostProcessor = getRequestPostProcessor(userName, userPassword);

        mvc.perform(put("/api/wheather/{city}", weather1.getRegionId())
                        .content(mapper.writeValueAsString(weatherDto1))
                        .with(requestPostProcessor)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isForbidden());
    }

    @Test
    void ShouldDeleteTheCity() throws Exception {
        Mockito.doNothing().when(weatherService).deleteTheCity(weather1.getRegionId());
        RequestPostProcessor requestPostProcessor = getRequestPostProcessor(adminName, adminPassword);

        mvc.perform(delete("/api/wheather/{city}", weather1.getRegionId())
                        .with(requestPostProcessor)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isOk());
    }

    @Test
    void ShouldFailDeleteTheCity() throws Exception {
        Mockito.doNothing().when(weatherService).deleteTheCity(weather1.getRegionId());

        userName = getRandomString();
        userPassword = getRandomString();
        RequestPostProcessor requestPostProcessor = getRequestPostProcessor(userName, userPassword);

        mvc.perform(delete("/api/wheather/{city}", weather1.getRegionId())
                        .with(requestPostProcessor)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isUnauthorized());
    }

}


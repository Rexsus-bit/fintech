package org.projectweather;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.projectweather.model.user.UserDto;
import org.projectweather.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import java.nio.charset.StandardCharsets;

import static org.hamcrest.Matchers.is;
import static org.projectweather.util.RandomDataGenerator.getRandomString;
import static org.projectweather.util.RequestPostProcessorGenerator.getRequestPostProcessor;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {"weather.api.url=http://api.weatherapi.com"})
@AutoConfigureMockMvc
public class AuthorizationAdminControllerTests {

    @MockBean
    private AdminService adminService;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private MockMvc mvc;

    @Value("${ADMIN_NAME}")
    private String adminName;

    @Value("${ADMIN_PASSWORD}")
    private String adminPassword;

    private UserDto userDto;

    private String userName;
    private String userPassword;

    @BeforeEach
    void setUp() {
        userName = getRandomString();
        userPassword = getRandomString();
        userDto = new UserDto(userName, userPassword, true);
    }


    @Test
    void shouldCreateUserTest() throws Exception {
        Mockito.doNothing().when(adminService).createUser(userDto);
        RequestPostProcessor requestPostProcessor = getRequestPostProcessor(adminName, adminPassword);

        mvc.perform(post("/admin/create-user")
                        .content(mapper.writeValueAsString(userDto))
                        .with(requestPostProcessor)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is(String.format("Пользователь %s зарегистрирован",
                        userDto.getUsername())), String.class));
    }

    @Test
    void shouldFailCreateUserTest() throws Exception {
        Mockito.doNothing().when(adminService).createUser(userDto);
        userName = getRandomString();
        userPassword = getRandomString();
        RequestPostProcessor requestPostProcessor = getRequestPostProcessor(userName, userPassword);

        mvc.perform(post("/admin/create-user")
                        .content(mapper.writeValueAsString(userDto))
                        .with(requestPostProcessor)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isUnauthorized());
    }


}

package org.projectweather.controller;


import lombok.RequiredArgsConstructor;
import org.projectweather.model.user.UserDto;
import org.projectweather.service.AdminService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService service;

    @PostMapping(value = "/create-user")
    public ResponseEntity<Map<String, String>> createUser(@RequestBody UserDto userDto) {
        service.createUser(userDto);
        return new ResponseEntity<>(Map
                .of("message", String.format("Пользователь %s зарегистрирован", userDto.getUsername())),
                HttpStatus.OK);
    }

}


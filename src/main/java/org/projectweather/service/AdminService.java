package org.projectweather.service;


import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.projectweather.model.user.Role;
import org.projectweather.model.user.UserDto;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
@Transactional
public class AdminService {

    private final JdbcUserDetailsManager jdbcUserDetailsManager;
    private  final PasswordEncoder encoder;

    public void createUser(UserDto userDTO) {
        UserDetails user = User.builder()
                .username(userDTO.getUsername())
                .password(encoder.encode(userDTO.getPassword()))
                .roles(Role.USER.name())
                .build();
        jdbcUserDetailsManager.createUser(user);
    }

}

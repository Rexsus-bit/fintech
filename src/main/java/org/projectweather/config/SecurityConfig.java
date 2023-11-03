package org.projectweather.config;

import jakarta.servlet.DispatcherType;
import org.projectweather.model.user.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import javax.sql.DataSource;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private DataSource dataSource;

    @Autowired
    public SecurityConfig(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Bean
    public JdbcUserDetailsManager user() {
        return new JdbcUserDetailsManager(dataSource);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .dispatcherTypeMatchers(DispatcherType.FORWARD, DispatcherType.ERROR).permitAll()
                        .requestMatchers("/admin/**").hasRole(Role.ADMIN.name())
                        .requestMatchers(HttpMethod.GET, "/api/wheather/**").hasAnyRole(Role.ADMIN.name()
                                , Role.USER.name())
                        .requestMatchers(HttpMethod.POST, "/api/wheather/**").hasRole(Role.ADMIN.name())
                        .requestMatchers(HttpMethod.PUT, "/api/wheather/**").hasRole(Role.ADMIN.name())
                        .requestMatchers(HttpMethod.DELETE, "/api/wheather/**").hasRole(Role.ADMIN.name())
                        .anyRequest().authenticated())
                .httpBasic(withDefaults())
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(10);
    }
}
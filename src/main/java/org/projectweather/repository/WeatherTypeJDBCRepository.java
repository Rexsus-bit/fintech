package org.projectweather.repository;


import lombok.RequiredArgsConstructor;
import org.projectweather.model.weatherInCity.WeatherType;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Objects;
import java.util.Optional;


@Repository
@RequiredArgsConstructor
public class WeatherTypeJDBCRepository {

    private final JdbcTemplate jdbcTemplate;

    public WeatherType createWeatherType(WeatherType weatherType) {
            String sqlQuery = "INSERT INTO weather_type (weather_id, weather_name)" +
                    " VALUES (DEFAULT, ?)";
            KeyHolder keyHolder = new GeneratedKeyHolder();

            jdbcTemplate.update(connection -> {
                PreparedStatement stmt = connection.prepareStatement(sqlQuery, Statement.RETURN_GENERATED_KEYS);
                stmt.setString(1, weatherType.getName());
                return stmt;
            }, keyHolder);
            long weatherTypeIdFromDb = Objects.requireNonNull(keyHolder.getKey()).longValue();
            weatherType.setId(weatherTypeIdFromDb);
            return weatherType;
    }

    public Optional<WeatherType> findWeatherTypeByName(String name) {
        String sqlQuery = "SELECT weather_id, weather_name FROM weather_type WHERE weather_name = ?";
        try {
            return Optional.of(jdbcTemplate.queryForObject(sqlQuery, this::mapRowToWeatherType, name));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    private WeatherType mapRowToWeatherType(ResultSet rs, int rowNum) throws SQLException {
        return WeatherType.builder()
                .id(rs.getLong("weather_id"))
                .name(rs.getString("weather_name"))
                .build();
    }
}

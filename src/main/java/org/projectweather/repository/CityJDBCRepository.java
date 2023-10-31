package org.projectweather.repository;

import lombok.RequiredArgsConstructor;
import org.projectweather.model.weatherInCity.City;
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
public class CityJDBCRepository {

    private final JdbcTemplate jdbcTemplate;

    public City createCity(City city) {
        String sqlQuery = "INSERT INTO city (city_id, city_name) VALUES (DEFAULT, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQuery, Statement.RETURN_GENERATED_KEYS);
            stmt.setString(1, city.getName());
            return stmt;
        }, keyHolder);

        long cityIdFromDb = Objects.requireNonNull(keyHolder.getKey()).longValue();
        city.setId(cityIdFromDb);
        return city;
    }

    public Optional<City> findCityByName(String name) {
        String sqlQuery = "SELECT city_id, city_name  FROM city WHERE city_name = ?";
        try {
            return Optional.of(jdbcTemplate.queryForObject(sqlQuery, this::mapRowToCity, name));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    private City mapRowToCity(ResultSet rs, int rowNum) throws SQLException {
        return City.builder()
                .id(rs.getLong("city_id"))
                .name(rs.getString("city_name"))
                .build();
    }

}

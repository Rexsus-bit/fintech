package org.projectweather.repository;

import lombok.RequiredArgsConstructor;
import org.projectweather.exceptions.dataBaseQuriesExceptions.WeatherInCityIsNotFoundException;
import org.projectweather.model.weatherInCity.City;
import org.projectweather.model.weatherInCity.WeatherInCity;
import org.projectweather.model.weatherInCity.WeatherType;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Repository("weatherInCityJDBCRepository")
@RequiredArgsConstructor
public class WeatherInCityJDBCRepository {

    private final JdbcTemplate jdbcTemplate;
    private final CityJDBCRepository cityJDBCRepository;
    private final WeatherTypeJDBCRepository weatherTypeJDBCRepository;

    public WeatherInCity createWeatherInCity(WeatherInCity weatherInCity) {
        setupIdForWeatherTypeAndCity(weatherInCity);
        String sqlQuery = "INSERT INTO weather_in_city (weather_in_city_id, city_id, weather_id, weather_datetime)" +
                " VALUES (DEFAULT, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQuery, Statement.RETURN_GENERATED_KEYS);
            stmt.setLong(1, weatherInCity.getCity().getId());
            stmt.setLong(2, weatherInCity.getWeatherType().getId());
            stmt.setTimestamp(3, Timestamp.from(weatherInCity.getUnixDateTime()));
            return stmt;
        }, keyHolder);
        long weatherInCityIdFromDb = Objects.requireNonNull(keyHolder.getKey()).longValue();
        weatherInCity.setId(weatherInCityIdFromDb);
        return weatherInCity;
    }

    private void setupIdForWeatherTypeAndCity(WeatherInCity weatherInCity) {
        Optional<City> cityOpt = cityJDBCRepository.findCityByName(weatherInCity.getCity().getName());
        Optional<WeatherType> weatherTypeOpt = weatherTypeJDBCRepository.findWeatherTypeByName(weatherInCity.getWeatherType().getName());
        if (cityOpt.isEmpty()) {
            weatherInCity.getCity().setId(cityJDBCRepository.createCity(weatherInCity.getCity()).getId());
        } else {
            weatherInCity.getCity().setId(cityOpt.get().getId());
        }
        if (weatherTypeOpt.isEmpty()) {
            weatherInCity.getWeatherType().setId(weatherTypeJDBCRepository.createWeatherType(weatherInCity.getWeatherType()).getId());
        } else {
            weatherInCity.getWeatherType().setId(weatherTypeOpt.get().getId());
        }
    }

    public WeatherInCity findWeatherInCityById(Long id) {
        String sqlQuery = "SELECT win.weather_in_city_id, win.city_id, win.weather_id, win.weather_datetime, " +
                "c.city_name, w.weather_name FROM weather_in_city win JOIN city c ON win.city_id = c.city_id " +
                "JOIN weather_type w ON win.weather_id = w.weather_id WHERE win.weather_in_city_id = ?";
        try {
            return jdbcTemplate.queryForObject(sqlQuery, this::mapRowToWeatherInCity, id);
        } catch (IncorrectResultSizeDataAccessException e) {
            throw new WeatherInCityIsNotFoundException(id);
        }
    }

    public List<WeatherInCity> findAllWeatherInCity() {
        String sqlQuery = "SELECT * FROM weather_in_city win JOIN city c ON win.city_id = c.city_id " +
                "JOIN weather_type w ON win.weather_id = w.weather_id";
        return jdbcTemplate.query(sqlQuery, this::mapRowToWeatherInCity);
    }

    private WeatherInCity mapRowToWeatherInCity(ResultSet rs, int rowNum) throws SQLException {
        return WeatherInCity.builder()
                .id(rs.getLong("weather_in_city_id"))
                .city(City.builder()
                        .id(rs.getLong("city_id"))
                        .name(rs.getString("city_name"))
                        .build())
                .weatherType(WeatherType.builder()
                        .id(rs.getLong("weather_id"))
                        .name(rs.getString("weather_name"))
                        .build())
                .unixDateTime(rs.getTimestamp("weather_datetime").toInstant())
                .build();
    }

    public WeatherInCity updateWeatherInCity(WeatherInCity weatherInCity) {
        setupIdForWeatherTypeAndCity(weatherInCity);
        String sqlQuery = "UPDATE weather_in_city SET city_id = ?, weather_id = ?, weather_datetime = ?" +
                "WHERE weather_in_city_id = ?";
        int updatesNumber = jdbcTemplate.update(sqlQuery,
                weatherInCity.getCity().getId(),
                weatherInCity.getWeatherType().getId(),
                weatherInCity.getUnixDateTime(),
                weatherInCity.getId());
        if (updatesNumber == 0) throw new WeatherInCityIsNotFoundException(weatherInCity.getId());
        return weatherInCity;
    }

    public void deleteWeatherInCityById(Long id) {
        String sqlQuery = "DELETE FROM weather_in_city WHERE weather_in_city_id = ?";
        long result = jdbcTemplate.update(sqlQuery, id);
        if (result == 0) {
            throw new WeatherInCityIsNotFoundException(id);
        }
    }

}

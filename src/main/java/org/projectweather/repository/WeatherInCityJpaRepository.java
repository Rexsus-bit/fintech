package org.projectweather.repository;

import org.projectweather.model.weatherInCity.WeatherInCity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WeatherInCityJpaRepository extends JpaRepository<WeatherInCity, Long> {

}

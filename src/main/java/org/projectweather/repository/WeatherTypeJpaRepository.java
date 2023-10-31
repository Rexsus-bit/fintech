package org.projectweather.repository;

import org.projectweather.model.weatherInCity.WeatherType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WeatherTypeJpaRepository extends JpaRepository<WeatherType, Long> {

}

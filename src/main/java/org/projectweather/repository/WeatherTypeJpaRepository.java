package org.projectweather.repository;

import org.projectweather.model.weatherInCity.WeatherType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface WeatherTypeJpaRepository extends JpaRepository<WeatherType, Long> {

    Optional<WeatherType> findByName(String name);
}

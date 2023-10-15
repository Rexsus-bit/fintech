package org.projectweather.repository;

import org.projectweather.model.weatherInCity.City;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CityJpaRepository extends JpaRepository<City, Long> {

}

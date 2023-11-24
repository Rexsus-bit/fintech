package org.projectweather.repository;

import org.projectweather.model.weatherInCity.WeatherInCity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface WeatherInCityJpaRepository extends JpaRepository<WeatherInCity, Long> {

    @Query(value = "SELECT * FROM weather_in_city w WHERE w.city_id = :cityId " +
            "ORDER BY w.weather_in_city_id DESC  LIMIT :limit ", nativeQuery = true)
    List<WeatherInCity> findByCityIdOrderByWeatherIdAndLimit(@Param("cityId") long cityId, @Param("limit") int limit);

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM weather_in_city w WHERE w.city_id = :cityId AND w.weather_in_city_id < :weatherInCityId",
            nativeQuery = true)
    void deleteWithOlderIdThanIndicated(@Param("cityId") long cityId,
                                        @Param("weatherInCityId") long weatherInCityId);

}

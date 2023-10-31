package org.projectweather.service;

import lombok.RequiredArgsConstructor;
import org.projectweather.exceptions.dataBaseQuriesExceptions.WeatherInCityIsAlreadyExistException;
import org.projectweather.exceptions.dataBaseQuriesExceptions.WeatherInCityIsNotFoundException;
import org.projectweather.model.weatherInCity.City;
import org.projectweather.model.weatherInCity.WeatherInCity;
import org.projectweather.model.weatherInCity.WeatherType;
import org.projectweather.repository.CityJpaRepository;
import org.projectweather.repository.WeatherInCityJpaRepository;
import org.projectweather.repository.WeatherTypeJpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service("weatherInCityServiceJPAImpl")
@RequiredArgsConstructor
public class WeatherInCityServiceJPAImpl implements WeatherInCityService {

    private final WeatherInCityJpaRepository weatherInCityJpaRepository;
    private final CityJpaRepository cityJpaRepository;
    private final WeatherTypeJpaRepository weatherTypeJpaRepository;

    /**
     * Уровни изоляции выбраны также как в методах WeatherInCityServiceJDBCImpl
     */

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public WeatherInCity createWeatherInCity(WeatherInCity weatherInCity) {
        setupIdForWeatherTypeAndCity(weatherInCity);
        if (weatherInCity.getId() != null && weatherInCityJpaRepository.existsById(weatherInCity.getId())) {
            throw new WeatherInCityIsAlreadyExistException(weatherInCity.getId());
        }
        return weatherInCityJpaRepository.save(weatherInCity);
    }

    private void setupIdForWeatherTypeAndCity(WeatherInCity weatherInCity) {
        Optional<City> cityOpt = cityJpaRepository.findByName(weatherInCity.getCity().getName());
        Optional<WeatherType> weatherTypeOpt = weatherTypeJpaRepository
                .findByName(weatherInCity.getWeatherType().getName());
        if (cityOpt.isEmpty()) {
            weatherInCity.getCity().setId(cityJpaRepository.save(weatherInCity.getCity()).getId());
        } else {
            weatherInCity.getCity().setId(cityOpt.get().getId());
        }
        if (weatherTypeOpt.isEmpty()) {
            weatherInCity.getWeatherType().setId(weatherTypeJpaRepository.save(weatherInCity.getWeatherType()).getId());
        } else {
            weatherInCity.getWeatherType().setId(weatherTypeOpt.get().getId());
        }
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public WeatherInCity findWeatherInCityById(Long id) {
        return weatherInCityJpaRepository.findById(id).orElseThrow(() -> new WeatherInCityIsNotFoundException(id));
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public List<WeatherInCity> findAllWeatherInCity() {
        return weatherInCityJpaRepository.findAll();
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public WeatherInCity updateWeatherInCity(WeatherInCity weatherInCity) {
        setupIdForWeatherTypeAndCity(weatherInCity);
        if (!weatherInCityJpaRepository.existsById(weatherInCity.getId())) {
            throw new WeatherInCityIsNotFoundException(weatherInCity.getId());
        }
        return weatherInCityJpaRepository.save(weatherInCity);
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void deleteWeatherInCityById(Long id) {
        weatherInCityJpaRepository.deleteById(id);
    }

}

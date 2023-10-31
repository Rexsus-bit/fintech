package org.projectweather.service;

import lombok.RequiredArgsConstructor;
import org.projectweather.exceptions.dataBaseQuriesExceptions.InvalidDataException;
import org.projectweather.exceptions.dataBaseQuriesExceptions.WeatherInCityIsAlreadyExistException;
import org.projectweather.exceptions.dataBaseQuriesExceptions.WeatherInCityIsNotFoundException;
import org.projectweather.model.weatherInCity.WeatherInCity;
import org.projectweather.repository.CityJpaRepository;
import org.projectweather.repository.WeatherInCityJpaRepository;
import org.projectweather.repository.WeatherTypeJpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service("weatherInCityServiceJPAImpl")
@RequiredArgsConstructor
public class WeatherInCityServiceJPAImpl implements WeatherInCityService {

    private final WeatherInCityJpaRepository weatherInCityJpaRepository;
    private final CityJpaRepository cityJpaRepository;
    private final WeatherTypeJpaRepository weatherTypeJpaRepository;

    @Override
    @Transactional
    public WeatherInCity createWeatherInCity(WeatherInCity weatherInCity) {
        validation(weatherInCity);
        if (weatherInCity.getId() != null && weatherInCityJpaRepository.existsById(weatherInCity.getId())) {
            throw new WeatherInCityIsAlreadyExistException(weatherInCity.getId());
        }
        return weatherInCityJpaRepository.save(weatherInCity);
    }

    private void validation(WeatherInCity weatherInCity) {
        if (!cityJpaRepository.existsById(weatherInCity.getCity().getId())) {
            throw new InvalidDataException("Указанный город отсутствует в справочнике");
        }
        if (!weatherTypeJpaRepository.existsById(weatherInCity.getWeatherType().getId())) {
            throw new InvalidDataException("Указанный тип погоды отсутствует в справочнике");
        }
    }

    @Override
    public WeatherInCity findWeatherInCityById(Long id) {
        return weatherInCityJpaRepository.findById(id).orElseThrow(() -> new WeatherInCityIsNotFoundException(id));
    }

    @Override
    public List<WeatherInCity> findAllWeatherInCity() {
        return weatherInCityJpaRepository.findAll();
    }

    @Override
    @Transactional
    public WeatherInCity updateWeatherInCity(WeatherInCity weatherInCity) {
        validation(weatherInCity);
        if (!weatherInCityJpaRepository.existsById(weatherInCity.getId())) {
            throw new WeatherInCityIsNotFoundException(weatherInCity.getId());
        }
        return weatherInCityJpaRepository.save(weatherInCity);
    }

    @Override
    @Transactional
    public void deleteWeatherInCityById(Long id) {
        weatherInCityJpaRepository.deleteById(id);
    }

}

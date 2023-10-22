package org.projectweather.service;

import lombok.RequiredArgsConstructor;
import org.projectweather.model.weatherInCity.WeatherInCity;
import org.projectweather.repository.WeatherInCityJDBCRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;

import static org.springframework.transaction.TransactionDefinition.*;


@Service("weatherInCityServiceJDBCImpl")
@RequiredArgsConstructor
public class WeatherInCityServiceJDBCImpl implements WeatherInCityService {

    private final WeatherInCityJDBCRepository weatherInCityJDBCRepository;
    private final TransactionTemplate transactionTemplate;

    /**
     * Указал уровень изоляции SERIALIZABLE, чтобы избежать риска создания дубликатов объектов city или weatherType с
     * одним и тем же имененем (если два потока одноврменно увидят, что объекта с требуемым именен нет, то оба создадут
     * по объекту с одним и тем же имененем).
     */

    @Override
    public WeatherInCity createWeatherInCity(WeatherInCity weatherInCity) {
        transactionTemplate.setIsolationLevel(ISOLATION_SERIALIZABLE);
        return transactionTemplate.execute(status ->
                weatherInCityJDBCRepository.createWeatherInCity(weatherInCity)
        );
    }

    /**
     * Phantom read и non repeatable read не могут возникнуть, т.к. данные считываются однократно.
     * При этом READ_COMMITTED защищает от dirty read.
     */

    @Override
    public WeatherInCity findWeatherInCityById(Long id) {
        transactionTemplate.setIsolationLevel(ISOLATION_READ_COMMITTED);
        return transactionTemplate.execute(status -> weatherInCityJDBCRepository.findWeatherInCityById(id));
    }

    /**
     * Выбрал уровень изоляции такой же как для метода findWeatherInCityById() выше
     */

    @Override
    public List<WeatherInCity> findAllWeatherInCity() {
        transactionTemplate.setIsolationLevel(ISOLATION_READ_COMMITTED);
        return transactionTemplate.execute(status -> weatherInCityJDBCRepository.findAllWeatherInCity());
    }

    /**
     * Указал уровень изоляции SERIALIZABLE, чтобы избежать риска создания дубликатов объектов city или weatherType с
     * одним и тем же названием (если два потока одноврменно увидят, что объекта с требуемым именен нет, то оба создадут
     * по объекту с одним и тем же имененем).
     */

    @Override
    public WeatherInCity updateWeatherInCity(WeatherInCity weatherInCity) {
        transactionTemplate.setIsolationLevel(ISOLATION_SERIALIZABLE);
        return transactionTemplate.execute(status -> weatherInCityJDBCRepository.updateWeatherInCity(weatherInCity));
    }

    /**
     * Для данного метода достаточно уровня изоляции READ_COMMITTED, чтобы избежать риска удаления незакомиченных данных
     */

    @Override
    public void deleteWeatherInCityById(Long id) {
        transactionTemplate.setIsolationLevel(ISOLATION_READ_COMMITTED);
        transactionTemplate.executeWithoutResult(status -> weatherInCityJDBCRepository.deleteWeatherInCityById(id));
    }
}

package org.projectweather.cache;

import org.projectweather.model.weatherInCity.WeatherInCity;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;


public class WeatherCache {

    private final int maxCacheSize;
    private final Map<Long, WeatherInCity> map;

    public WeatherCache(int maxCacheSize) {
        this.maxCacheSize = maxCacheSize;
        this.map = Collections.synchronizedMap(new LinkedHashMap<>(maxCacheSize, 0.75f, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<Long, WeatherInCity> eldest) {
                return size() > maxCacheSize;
            }
        });
    }

    public Optional<WeatherInCity> get(Long id) {
            return Optional.ofNullable(map.get(id));
    }

    public void remove(Long id) {
            map.remove(id);
    }

    public void save(WeatherInCity weatherInCity) {
            map.put(weatherInCity.getId(), weatherInCity);
    }

    public int size() {
            return map.size();
    }
    public void clear() {
            map.clear();
    }

    public int getMaxCacheSize() {
            return maxCacheSize;
    }

}

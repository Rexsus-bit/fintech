package org.projectweather;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.projectweather.cache.WeatherCache;
import org.projectweather.model.weatherInCity.City;
import org.projectweather.model.weatherInCity.WeatherInCity;
import org.projectweather.model.weatherInCity.WeatherType;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.LongStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.projectweather.util.RandomDataGenerator.getRandomLongNumber;
import static org.projectweather.util.RandomDataGenerator.getRandomString;
import static org.projectweather.util.RandomDataGenerator.getRandomUnixTime;

public class WeatherCacheTests {

    private WeatherCache weatherCache;
    static private WeatherInCity weatherInCity1;
    static private WeatherInCity weatherInCity2;
    static private WeatherInCity weatherInCity3;
    static private WeatherInCity weatherInCity4;
    static private WeatherInCity weatherInCity5;

    @BeforeAll
    static void setUp() {
        weatherInCity1 = new WeatherInCity(getRandomLongNumber(), new City(null, getRandomString()),
                new WeatherType(getRandomLongNumber(), getRandomString()), getRandomUnixTime());
        weatherInCity2 = new WeatherInCity(getRandomLongNumber(), new City(null, getRandomString()),
                new WeatherType(getRandomLongNumber(), getRandomString()), getRandomUnixTime());
        weatherInCity3 = new WeatherInCity(getRandomLongNumber(), new City(null, getRandomString()),
                new WeatherType(getRandomLongNumber(), getRandomString()), getRandomUnixTime());
        weatherInCity4 = new WeatherInCity(getRandomLongNumber(), new City(null, getRandomString()),
                new WeatherType(getRandomLongNumber(), getRandomString()), getRandomUnixTime());
        weatherInCity5 = new WeatherInCity(getRandomLongNumber(), new City(null, getRandomString()),
                new WeatherType(getRandomLongNumber(), getRandomString()), getRandomUnixTime());
    }

    @RepeatedTest(100)
    public void shouldPutDataInConcurrentToCacheAndNoDataLost() throws Exception {
        weatherCache = new WeatherCache(10000);
        final int size = weatherCache.getMaxCacheSize();
        final ExecutorService executorService = Executors.newFixedThreadPool(5);
        CountDownLatch countDownLatch = new CountDownLatch(size);
        try {
            LongStream.range(0, size).<Runnable>mapToObj(key -> () -> {
                weatherCache.save(new WeatherInCity(key, null, null, null));
                countDownLatch.countDown();
            }).forEach(executorService::submit);
            countDownLatch.await();
        } finally {
            executorService.shutdown();
        }
        assertEquals(size, weatherCache.size());
    }

    @Test
    public void shouldEvictLast() {
        weatherCache = new WeatherCache(10000);
        weatherCache.save(weatherInCity1);
        weatherCache.save(weatherInCity2);
        int cnt = weatherCache.getMaxCacheSize();
        for (long i = 1; i < cnt; i++) {
            weatherCache.save(new WeatherInCity(getRandomLongNumber(), null, null, null));
        }
        assertEquals(weatherCache.getMaxCacheSize(), weatherCache.size());
        assertTrue(weatherCache.get(weatherInCity1.getId()).isEmpty());
        assertTrue(weatherCache.get(weatherInCity2.getId()).isPresent());
    }

    @Test
    public void shouldBumpBackUpToRecentlyCached() {
        weatherCache = new WeatherCache(10000);
        weatherCache.save(weatherInCity1);
        weatherCache.save(weatherInCity2);
        weatherCache.save(weatherInCity3);
        int cnt = weatherCache.getMaxCacheSize();
        for (long i = 3; i < cnt; i++) {
            weatherCache.save(new WeatherInCity(getRandomLongNumber(), null, null, null));
        }

        assertTrue(weatherCache.get(weatherInCity1.getId()).isPresent());

        weatherCache.save(weatherInCity4);

        assertTrue(weatherCache.get(weatherInCity2.getId()).isEmpty());
        assertTrue(weatherCache.get(weatherInCity1.getId()).isPresent());

        weatherCache.save(weatherInCity5);

        assertTrue(weatherCache.get(weatherInCity3.getId()).isEmpty());
        assertTrue(weatherCache.get(weatherInCity1.getId()).isPresent());
        assertEquals(weatherCache.getMaxCacheSize(), weatherCache.size());

    }

}

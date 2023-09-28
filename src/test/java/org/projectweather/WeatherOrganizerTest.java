package org.projectweather;

import java.time.Instant;
import java.util.concurrent.ThreadLocalRandom;

import com.github.javafaker.Faker;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.security.SecureRandom;

import java.time.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.projectweather.WeatherOrganizer.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class WeatherOrganizerTest {

    Faker faker;
    SecureRandom rand;
    Integer regionId;
    String region;

    List<Weather> weatherList;
    int firstRegionWeatherQuantity;
    int secondRegionWeatherQuantity;

    @BeforeAll
    void beforeEach() {
        faker = new Faker();
        rand = new SecureRandom();
        weatherList = new ArrayList<>();

        regionId = rand.nextInt(256);
        region = faker.address().city();
        firstRegionWeatherQuantity = getRandomNumber(2, 50);
        for (int i = 0; i < firstRegionWeatherQuantity; i++) {
            weatherList.add(new Weather(regionId, region, getRandomNumber(-50, 50), getRandomZonedDateTime()));
        }

        regionId = rand.nextInt(256);
        region = faker.address().city();
        secondRegionWeatherQuantity = getRandomNumber(2, 50);
        for (int i = 0; i < secondRegionWeatherQuantity; i++) {
            weatherList.add(new Weather(regionId, region, getRandomNumber(-50, 50), getRandomZonedDateTime()));
        }
    }

    private Instant getRandomTime() {
        long startSeconds = Instant.MIN.getEpochSecond();
        long endSeconds = Instant.now().getEpochSecond();
        long random = ThreadLocalRandom
                .current()
                .nextLong(startSeconds, endSeconds);
        return Instant.ofEpochSecond(random);
    }

    private ZonedDateTime getRandomZonedDateTime() {
        ZoneOffset zoneOffset = ZoneOffset.ofHours(getRandomNumber(-18, 18));
        Instant instant = getRandomTime();
        ZoneId id = ZoneId.ofOffset("UTC", zoneOffset);
        return ZonedDateTime.ofInstant(instant, id);
    }

    private int getRandomNumber(int min, int max) {
        return (int) (Math.random() * (max - min + 1) + min);
    }

    @Test
    void calculateAverageTemperatureTest() {
        Map<String, Double> map = calculateAverageTemperature(weatherList);

        int sum = 0;
        for (int i = 0; i < firstRegionWeatherQuantity; i++) {
            sum += weatherList.get(i).getTemperature();
        }
        double average = (double) sum / firstRegionWeatherQuantity;

        assertEquals(average, map.get(weatherList.get(0).getRegionName()));

        sum = 0;

        for (int i = firstRegionWeatherQuantity; i < weatherList.size(); i++) {
            sum += weatherList.get(i).getTemperature();
        }

        average = (double) sum / secondRegionWeatherQuantity;
        assertEquals(average, map.get(weatherList.get(weatherList.size() - 1).getRegionName()));
    }

    @Test
    void findRegionsAboveIndicatedTest() {
        int indicatedTemperature = getRandomNumber(-50, 50);

        int sum = 0;
        for (int i = 0; i < firstRegionWeatherQuantity; i++) {
            sum += weatherList.get(i).getTemperature();
        }
        double averageForTheFirstRegion = (double) sum / firstRegionWeatherQuantity;

        sum = 0;
        for (int i = firstRegionWeatherQuantity; i < weatherList.size(); i++) {
            sum += weatherList.get(i).getTemperature();
        }
        double averageForTheSecondRegion = (double) sum / secondRegionWeatherQuantity;

        if (averageForTheFirstRegion > indicatedTemperature) {
            assertTrue(findRegionsAboveIndicatedTemperature(weatherList, indicatedTemperature)
                    .contains(weatherList.get(0).getRegionName()));
        } else {
            assertFalse(findRegionsAboveIndicatedTemperature(weatherList, indicatedTemperature)
                    .contains(weatherList.get(0).getRegionName()));
        }

        if (averageForTheSecondRegion > indicatedTemperature) {
            assertTrue(findRegionsAboveIndicatedTemperature(weatherList, indicatedTemperature)
                    .contains(weatherList.get(weatherList.size() - 1).getRegionName()));
        } else {
            assertFalse(findRegionsAboveIndicatedTemperature(weatherList, indicatedTemperature)
                    .contains(weatherList.get(weatherList.size() - 1).getRegionName()));
        }
    }

    @Test
    void groupByRegionIdTest() {
        Map<Integer, List<Integer>> map = groupByRegionId(weatherList);

        for (int i = 0; i < weatherList.size(); i++) {
            assertTrue(map.get((weatherList.get(i).getRegionID())).contains(weatherList.get(i).getTemperature()));
        }
    }

    @Test
    void groupByTemperatureTest() {
        Map<Integer, Collection<Weather>> map = groupByTemperature(weatherList);

        for (int i = 0; i < weatherList.size(); i++) {
            map.get(weatherList.get(i).getTemperature()).contains(weatherList.get(i));
        }
    }
}
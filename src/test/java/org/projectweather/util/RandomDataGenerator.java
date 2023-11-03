package org.projectweather.util;

import java.security.SecureRandom;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class RandomDataGenerator {
    private static final SecureRandom rand = new SecureRandom();

    public static Instant getRandomUnixTime() {
        return Instant.now().plusSeconds(getRandomNumberBetweenValues(0, 999999999));
    }

    public static LocalDateTime getRandomLocalDateTime() {
        return LocalDateTime.now().plusSeconds(getRandomNumberBetweenValues(0, 999999999))
                .truncatedTo(ChronoUnit.SECONDS);
    }

    public static String getRandomString() {
        UUID randomUUID = UUID.randomUUID();
        return randomUUID.toString().replaceAll("-", "");
    }

    public static long getRandomNumberBetweenValues(int min, int max) {
        return (long) (Math.random() * (max - min + 1) + min);
    }

    public static long getRandomLongNumber() {
        return Math.abs(rand.nextLong());
    }

    public static String getRandomCityName(){
        List<String> list = List.of("London", "Moscow", "Saint Petersburg", "Beijing", "Washington", "Berlin");
        Random random = new Random();
        int randomIndex = random.nextInt(list.size());
        return list.get(randomIndex);
    }

}

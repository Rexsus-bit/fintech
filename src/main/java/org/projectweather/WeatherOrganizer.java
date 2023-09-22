package org.projectweather;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class WeatherOrganizer {

    public static void main(String[] args) {

        // Добавил тестовые данные для наглядности

        List<Weather> weatherList = Util.makeTestList();

        System.out.println("Среднее значение температуры в регионах:" +
                WeatherOrganizer.calculateAverageTemperature(weatherList).getAsDouble());

        System.out.println("Температура выше 15 градусов была в следующих регионах:");
        WeatherOrganizer.findRegionsAboveIndicated(weatherList, 15).forEach(System.out::println);

        Map<Integer, List<Integer>> groupedByRegionId = WeatherOrganizer.groupByRegionId(weatherList);

        Map<Integer, Collection<Weather>> groupedByTemperature = WeatherOrganizer.groupByTemperature(weatherList);
    }

    /**
     * Метод для рассчета среднего значения температуры в регионах
     */
    public static OptionalDouble calculateAverageTemperature(List<Weather> weatherList) {
        return weatherList.stream().mapToInt(Weather::getTemperature).average();
    }

    /**
     * Метод для поиска регионов, больше какой-то определенной температуры
     */
    public static List<String> findRegionsAboveIndicated(List<Weather> weatherList, int temperature) {
        return weatherList.stream().filter(o -> o.getTemperature() > temperature).map(Weather::getRegionName).distinct()
                .collect(Collectors.toList());
    }

    /**
     * Метод преобразует список в Map, у которой ключ - уникальный идентификатор, значение - список со значениями
     * температур
     */
    public static Map<Integer, List<Integer>> groupByRegionId(List<Weather> weatherList) {
        return weatherList.stream()
                .collect(Collectors.groupingBy(Weather::getRegionID,
                        Collectors.mapping(Weather::getTemperature, Collectors.toList())));
    }

    /**
     * Метод преобразует список Map, у которой ключ - температура, значение - коллекция объектов Weather,
     * которым соответствует температура, указанная в ключе
     */
    public static Map<Integer, Collection<Weather>> groupByTemperature(List<Weather> weatherList) {
        return weatherList.stream()
                .collect(Collectors.groupingBy(Weather::getTemperature,
                        Collectors.mapping(Function.identity(), Collectors.toCollection(ArrayList::new))));
    }
}
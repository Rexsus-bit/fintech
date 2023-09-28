package org.projectweather;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class WeatherOrganizer {

    public static void main(String[] args) {

    }

    /**
     * Метод для рассчета среднего значения температуры в регионах
     */
    public static Map<String, Double> calculateAverageTemperature(List<Weather> weatherList) {
        return weatherList.stream().collect(Collectors
                .groupingBy(Weather::getRegionName, Collectors.averagingDouble(Weather::getTemperature)));
    }

    /**
     * Метод для поиска регионов, больше какой-то определенной температуры
     */
    public static List<String> findRegionsAboveIndicatedTemperature(List<Weather> weatherList, int temperature) {
        return calculateAverageTemperature(weatherList).entrySet().stream().filter(o -> o.getValue() > temperature)
                .map(Map.Entry::getKey).collect(Collectors.toList());
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
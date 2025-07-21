package org.example.kafka.consumer.service.analytics;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.kafka.consumer.service.consumer.WeatherConsumer;
import org.example.kafka.consumer.service.event.WeatherEvent;
import org.example.kafka.consumer.service.event.WeatherStatus;
import org.springframework.stereotype.Service;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.*;

@Service
@Slf4j
public class WeatherAnalyticsService {
    public WeatherEvent getCityWithMostRainyDays(Map<String, List<WeatherEvent>> cityWeatherMap) {
        return cityWeatherMap.entrySet().stream()
                .max(Comparator.comparingInt(e ->
                        (int) e.getValue().stream()
                                .filter(w -> w.getWeatherStatus() == WeatherStatus.RAINY)
                                .count()))
                .map(e -> e.getValue().stream()
                        .filter(w -> w.getWeatherStatus() == WeatherStatus.RAINY)
                        .findFirst()
                        .orElse(null))
                .orElse(null);
    }

    public WeatherEvent getHottestWeatherEvent(Map<String, List<WeatherEvent>> cityWeatherMap) {
        return cityWeatherMap.values().stream()
                .flatMap(List::stream)
                .max(Comparator.comparingInt(WeatherEvent::getTemperature))
                .orElse(null);
    }

    public WeatherEvent getMinAverageTemperature(Map<String, List<WeatherEvent>> cityWeatherMap) {
        return cityWeatherMap.entrySet().stream()
                .min(Comparator.comparingDouble(e ->
                        e.getValue().stream()
                                .mapToInt(WeatherEvent::getTemperature)
                                .average()
                                .orElse(Double.MAX_VALUE)))
                .map(e -> e.getValue().stream()
                        .min(Comparator.comparingInt(WeatherEvent::getTemperature))
                        .orElse(null))
                .orElse(null);
    }


    public void getStats(Map<String, List<WeatherEvent>> cityWeatherMap) {
        String rainiestCity = getCityWithMostRainyDays(cityWeatherMap).getCity();

        WeatherEvent weatherEvent = getHottestWeatherEvent(cityWeatherMap);
        LocalDate hottestDay = weatherEvent.getDate();
        String hottestCity = weatherEvent.getCity();

        String cityWithMinAverageTemp = getMinAverageTemperature(cityWeatherMap).getCity();

        try (FileWriter writer = new FileWriter("weather-stats.txt", true)) {
            writer.write("Most rainy city: " + rainiestCity + "\n"
                    + "The hottest days in city: " + hottestCity + ", day: " + hottestDay + "\n"
                    + "Minimum average temperature in city: " + cityWithMinAverageTemp + "\n");
        } catch (IOException e) {
            log.error("Failed to write stats", e);
        }
    }

}

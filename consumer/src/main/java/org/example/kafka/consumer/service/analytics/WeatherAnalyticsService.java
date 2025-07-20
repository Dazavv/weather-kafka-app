package org.example.kafka.consumer.service.analytics;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.kafka.consumer.service.consumer.WeatherConsumer;
import org.example.kafka.consumer.service.event.WeatherEvent;
import org.example.kafka.consumer.service.event.WeatherStatus;
import org.springframework.stereotype.Service;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class WeatherAnalyticsService {
    private final WeatherConsumer weatherConsumer;
    private final Map<String, List<WeatherEvent>> cityWeatherMap = weatherConsumer.getCityWeatherMap();

    public String getCityWithMostRainyDays() {
        return cityWeatherMap.entrySet().stream()
                        .collect(Collectors.toMap(
                                Map.Entry::getKey,
                                e -> (int) e.getValue().stream()
                                        .filter(w -> w.getWeatherStatus() == WeatherStatus.RAINY)
                                        .count()
                        )).entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null);

    }

    public String getHottestDay() {
        return String.valueOf(cityWeatherMap.values().stream()
                .flatMap(List::stream)
                .max(Comparator.comparingInt(WeatherEvent::getTemperature))
                .orElse(null));
    }

    public String getMinAverageTemperature() {
        return cityWeatherMap.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> e.getValue().stream()
                                .mapToInt(WeatherEvent::getTemperature)
                                .average()
                                .orElse(0)
                ))
                .entrySet().stream()
                .min(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("—");
    }

    public void getStats() {
        try (FileWriter writer = new FileWriter("weather-stats.txt", true)) {
            writer.write("Most rainy city: " + getCityWithMostRainyDays() + "\n");

        } catch (IOException e) {
            log.error("Failed to write stats", e);
        }
    }

}

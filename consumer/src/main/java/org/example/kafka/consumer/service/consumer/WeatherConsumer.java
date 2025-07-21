package org.example.kafka.consumer.service.consumer;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.example.kafka.consumer.service.analytics.WeatherAnalyticsService;
import org.example.kafka.consumer.service.event.WeatherEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@Getter
public class WeatherConsumer {
    private final WeatherAnalyticsService weatherAnalyticsService;
    private final Map<String, List<WeatherEvent>> cityWeatherMap = new HashMap<>();

    private final int expectedTotalEvents;
    private int receivedEventsCount = 0;

    public WeatherConsumer(WeatherAnalyticsService weatherAnalyticsService,
                           @Value("${weather.expected-total-events}") int expectedTotalEvents) {
        this.weatherAnalyticsService = weatherAnalyticsService;
        this.expectedTotalEvents = expectedTotalEvents;
    }

    public void processWeatherEvent(WeatherEvent weatherEvent) {
        cityWeatherMap.computeIfAbsent(weatherEvent.getCity(), city -> new ArrayList<>()).add(weatherEvent);
        receivedEventsCount++;
        if (receivedEventsCount >= expectedTotalEvents) {
            log.info("Get stats from kafka");
            weatherAnalyticsService.getStats(cityWeatherMap);
        }
    }
}

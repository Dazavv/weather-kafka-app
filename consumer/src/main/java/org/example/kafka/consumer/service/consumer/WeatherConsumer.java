package org.example.kafka.consumer.service.consumer;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.example.kafka.consumer.service.event.WeatherEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service

public class WeatherConsumer {
    private final Map<String, List<WeatherEvent>> cityWeatherMap = new HashMap<>();

    public void processWeatherEvent(WeatherEvent weatherEvent) {
        cityWeatherMap.computeIfAbsent(weatherEvent.getCity(), city -> new ArrayList<>()).add(weatherEvent);
        log.info("Processing event: {}", weatherEvent);
    }

    public Map<String, List<WeatherEvent>> getCityWeatherMap() {
        return cityWeatherMap;
    }
}

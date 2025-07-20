package org.example.kafka.producer.service.messaging.producer;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.kafka.producer.model.*;
import org.example.kafka.producer.service.messaging.KafkaMessagingService;
import org.example.kafka.producer.service.messaging.event.WeatherEvent;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.io.*;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class WeatherProducer {
    private final KafkaMessagingService kafkaMessagingService;
    private final ModelMapper modelMapper;
    private LocalDate startDate = LocalDate.now();
    private final int minTemperature = 0;
    private final int maxTemperature = 35;
    private final int period = 7;
    private int currentCityIndex = 0;
    private int dayOffset = 0;
    private List<String> cities;
    private ObjectMapper objectMapper = new ObjectMapper();

    public void sendWeatherEvent(Weather weather) {
        kafkaMessagingService.sendWeather(modelMapper.map(weather, WeatherEvent.class));
        log.info("Send weather from producer {}", weather);
    }

    public void startWeatherSending() {
        cities = loadCitiesFromFile();
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

        scheduler.scheduleAtFixedRate(() -> {
            if (cities.isEmpty()) return;

            String city = cities.get(currentCityIndex);
            Weather weather = generateWeather(city);
            sendWeatherEvent(weather);

            dayOffset++;

            if (dayOffset >= period) {
                dayOffset = 0;
                currentCityIndex = (currentCityIndex + 1) % cities.size();
            }
        }, 0, 5, TimeUnit.SECONDS);
    }

    public Weather generateWeather(String city) {
        LocalDate date = startDate.plusDays(dayOffset);
        int temperature = ThreadLocalRandom.current().nextInt(minTemperature, maxTemperature + 1);
        WeatherStatus weatherStatus = WeatherStatus.randomWeatherStatus();

        Weather weather = new Weather(city, date, temperature, weatherStatus);
        return weather;
    }

    public List<String> loadCitiesFromFile() {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream("cities.json")) {
            return objectMapper.readValue(is, new TypeReference<>() {});
        } catch (IOException e) {
            e.printStackTrace();
            return List.of();
        }
    }


}

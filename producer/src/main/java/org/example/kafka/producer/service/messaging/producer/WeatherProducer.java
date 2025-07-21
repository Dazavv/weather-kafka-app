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
    private final ObjectMapper objectMapper = new ObjectMapper();

    private LocalDate startDate = LocalDate.now();
    private final int minTemperature = 0;
    private final int maxTemperature = 35;
    private final int period = 7;

    private List<String> cities = new ArrayList<>();
    private int currentCityIndex = 0;
    private int dayOffset = 0;
    private int citiesCount;

    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private volatile boolean running = false;

    public void sendWeatherEvent(Weather weather) {
        kafkaMessagingService.sendWeather(modelMapper.map(weather, WeatherEvent.class));
        log.info("Send weather from producer {}", weather);
    }

    public void startSending() {
        if (running) {
            log.info("WeatherProducer is already running");
            return;
        }
        running = true;

        cities = loadCitiesFromFile();
        citiesCount = cities.size();

        if (cities.isEmpty()) {
            log.warn("No cities loaded, stopping producer");
            running = false;
            return;
        }

        scheduler.scheduleAtFixedRate(() -> {
            if (!running) {
                log.info("Producer stopped, cancelling scheduled task");
                return;
            }

            String city = cities.get(currentCityIndex);
            Weather weather = generateWeather(city);
            sendWeatherEvent(weather);

            dayOffset++;

            if (dayOffset >= period) {
                dayOffset = 0;
                currentCityIndex = (currentCityIndex + 1) % cities.size();
                citiesCount--;
            }

            if (citiesCount <= 0) {
                stopSending();
            }
        }, 0, 2, TimeUnit.SECONDS);

        log.info("WeatherProducer started");
    }

    public void stopSending() {
        if (!running) {
            log.info("WeatherProducer is not running");
            return;
        }
        running = false;
        scheduler.shutdownNow();
        log.info("WeatherProducer stopped");
    }

    public Weather generateWeather(String city) {
        LocalDate date = startDate.plusDays(dayOffset);
        int temperature = ThreadLocalRandom.current().nextInt(minTemperature, maxTemperature + 1);
        WeatherStatus weatherStatus = WeatherStatus.randomWeatherStatus();

        return new Weather(city, date, temperature, weatherStatus);
    }

    public List<String> loadCitiesFromFile() {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream("cities.json")) {
            if (is == null) {
                log.error("cities.json not found");
                return List.of();
            }
            return objectMapper.readValue(is, new TypeReference<>() {});
        } catch (IOException e) {
            log.error("Failed to load cities", e);
            return List.of();
        }
    }
}

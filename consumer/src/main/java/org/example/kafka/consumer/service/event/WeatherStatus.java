package org.example.kafka.consumer.service.event;

import java.util.Random;

public enum WeatherStatus {
    RAINY, CLOUDY, WINDY, SNOWY, HOT, SUNNY;

    private static final Random PRNG = new Random();

    public static WeatherStatus randomWeatherStatus() {
        WeatherStatus[] weatherStatuses = values();
        return weatherStatuses[PRNG.nextInt(weatherStatuses.length)];
    }
}

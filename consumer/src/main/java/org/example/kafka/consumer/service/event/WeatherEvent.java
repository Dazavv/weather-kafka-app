package org.example.kafka.consumer.service.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WeatherEvent {
    private String city;
    private LocalDate date;
    private int temperature;
    private WeatherStatus weatherStatus;
}

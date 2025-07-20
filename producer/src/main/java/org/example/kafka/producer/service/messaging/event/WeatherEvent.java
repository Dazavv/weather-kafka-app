package org.example.kafka.producer.service.messaging.event;

import lombok.*;
import org.example.kafka.producer.model.WeatherStatus;

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

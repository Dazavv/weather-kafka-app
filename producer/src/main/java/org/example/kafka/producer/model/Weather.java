package org.example.kafka.producer.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Weather {
    private String city;
    private LocalDate date;
    private int temperature;
    private WeatherStatus weatherStatus;
}

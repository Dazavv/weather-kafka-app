package org.example.kafka.producer;

import org.example.kafka.producer.service.messaging.producer.WeatherProducer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class ProducerApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(ProducerApplication.class, args);
        WeatherProducer producer = context.getBean(WeatherProducer.class);
        producer.startWeatherSending();
    }
}
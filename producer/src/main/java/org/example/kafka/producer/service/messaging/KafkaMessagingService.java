package org.example.kafka.producer.service.messaging;

import lombok.RequiredArgsConstructor;
import org.example.kafka.producer.service.messaging.event.WeatherEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaMessagingService {

    @Value("${topic.send-weather}")
    private String sendClientTopic;

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void sendWeather(WeatherEvent weatherEvent) {
        kafkaTemplate.send(sendClientTopic, weatherEvent);
    }
}

package org.example.kafka.consumer.service.messaging;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.kafka.consumer.service.consumer.WeatherConsumer;
import org.example.kafka.consumer.service.event.WeatherEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@AllArgsConstructor
public class KafkaMessagingService {
    private static final String topicCreateOrder = "${topic.send-weather}";
    private static final String kafkaConsumerGroupId = "${spring.kafka.consumer.group-id}";
    private final WeatherConsumer weatherConsumer;

    @Transactional
    @KafkaListener(
            topics = topicCreateOrder,
            groupId = kafkaConsumerGroupId,
            properties = {
                    "spring.json.value.default.type=org.example.kafka.consumer.service.event.WeatherEvent"
            })
    public void readWeather(WeatherEvent weatherEvent) {
        log.info("Received weather event: {}", weatherEvent);
        weatherConsumer.processWeatherEvent(weatherEvent);
    }
}

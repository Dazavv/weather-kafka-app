# Weather Kafka App

Проект для генерации и обработки погодных событий с использованием Apache Kafka.

## 📌 Описание

Приложение состоит из двух сервисов:

- **Producer** — генерирует события о погоде на 7 дней вперёд для различных городов и отправляет их в Kafka-топик.
- **Consumer** — принимает события из Kafka и сохраняет статистику:
  - самый дождливый город,
  - самый жаркий день и город,
  - город с минимальной средней температурой.

Результаты записываются в файл `weather-stats.txt` и выводятся в логах.

---

## Технологии

- Java 17+
- Spring Boot
- Apache Kafka
- Docker & Docker Compose
- JSON

---

## ⚙️ Сборка и запуск

### 1. Собери JAR-файлы продюсера и консьюмера:

```bash
mvn clean package
````

### 2. Запусти всё через Docker Compose:

```bash
docker-compose up --build
```

Сервисы:

* Kafka UI: [http://localhost:8082](http://localhost:8082)

---

## 🗂 Структура проекта

```
.
├── docker-compose.yml
├── producer/
│   ├── Dockerfile
│   └── src/main/java/.../WeatherProducer.java
├── consumer/
│   ├── Dockerfile
│   └── src/main/java/.../WeatherConsumer.java
└── weather-stats.txt
```

---

## Producer

### Поведение:

* Загружает список городов из JSON файла.
* Генерирует погоду на 7 дней для каждого города.
* Отправляет данные в Kafka топик `weather-send-events`.
* После генерации для всех городов — останавливается.

### Формат события:

```json
{
  "city": "Moscow",
  "date": "2025-07-21",
  "temperature": 25,
  "weatherStatus": "SUNNY"
}
```

---

## Consumer

* Слушает Kafka топик `weather-send-events`.
* Сохраняет все события в Map.
* После завершения — считает статистику и пишет в `weather-stats.txt` и выводит результат в логах.

Пример вывода:
```bash
consumer-1                | 2025-07-21T21:44:44.127Z  INFO 1 --- [ntainer#0-0-C-1] o.e.k.c.s.a.WeatherAnalyticsService      : Weather Statistics:  
consumer-1                | Most rainy city: Novosibirsk
consumer-1                | The hottest day in city: Yekaterinburg, day: 2025-07-24
consumer-1                | Minimum average temperature in city: Saint Petersburg     
```

---

## Тестирование

...в разработке

---


## Просмотр логов

```bash
docker-compose logs producer
docker-compose logs consumer
```

Пример логов:
```bash
producer-1 | 2025-07-21T21:44:22.966Z  INFO 1 --- [pool-2-thread-1] o.e.k.p.s.m.producer.WeatherProducer     : Send weather from producer Weather(city=Yekaterinburg, date=2025-07-23, temperature=20, weatherStatus=SUNNY)
consumer-1 | 2025-07-21T21:44:22.978Z  INFO 1 --- [ntainer#0-0-C-1] o.e.k.c.s.m.KafkaMessagingService        : Received weather event: WeatherEvent(city=Yekaterinburg, date=2025-07-23, temperature=20, weatherStatus=SUNNY)                                                           
```

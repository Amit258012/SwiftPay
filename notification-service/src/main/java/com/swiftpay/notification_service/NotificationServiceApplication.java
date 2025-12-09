package com.swiftpay.notification_service;

import jakarta.annotation.PostConstruct;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.EnableKafka;

import java.util.Properties;

@EnableKafka
@SpringBootApplication
public class NotificationServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(NotificationServiceApplication.class, args);
	}
	@PostConstruct
	public void testKafkaConnection() {
		try {
			Properties props = new Properties();
			props.put("bootstrap.servers", "localhost:9092");
			props.put("group.id", "test-connection");
			props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
			props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");

			KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);
			consumer.listTopics();

			System.out.println("🔥 Notification-service connected to Kafka successfully!");
			consumer.close();
		} catch (Exception e) {
			System.out.println("❌ Notification-service FAILED to connect to Kafka");
			e.printStackTrace();
		}
	}

}

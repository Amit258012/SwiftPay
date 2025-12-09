package com.swiftpay.transaction_service.config;

import com.swiftpay.transaction_service.dto.TransferRequest;
import jakarta.annotation.PostConstruct;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaProducerConfig {

    @PostConstruct
    public void loaded() {
        System.out.println("🔥 KafkaProducerConfig LOADED");
    }

    @Bean
    @Primary
    public ProducerFactory<String, TransferRequest> producerFactory() {
        Map<String, Object> config = new HashMap<>();
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");

        JsonSerializer<TransferRequest> jsonSerializer = new JsonSerializer<>();
        jsonSerializer.setAddTypeInfo(false); // 🚀 FIX

        return new DefaultKafkaProducerFactory<>(
                config,
                new StringSerializer(),
                jsonSerializer
        );
    }

    @Bean
    @Primary
    public KafkaTemplate<String, TransferRequest> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }
}
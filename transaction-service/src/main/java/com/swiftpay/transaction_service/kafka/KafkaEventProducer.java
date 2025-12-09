package com.swiftpay.transaction_service.kafka;

import com.swiftpay.transaction_service.dto.TransferRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;


@Component
public class KafkaEventProducer {

    private static final String TOPIC = "txn-initiated";
    private final KafkaTemplate<String, TransferRequest> kafkaTemplate;

    @Autowired
    public KafkaEventProducer(KafkaTemplate<String, TransferRequest> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendTransactionEvent(String key, TransferRequest TransferRequest) {
        kafkaTemplate.send(TOPIC, key, TransferRequest);
        System.out.println("🚀 Sent transaction: " + TransferRequest);
    }
}


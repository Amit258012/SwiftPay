package com.swiftpay.notification_service.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.swiftpay.notification_service.entity.Notification;
import com.swiftpay.notification_service.dto.Transaction;
import com.swiftpay.notification_service.repository.NotificationRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class NotificationConsumer {
    private final NotificationRepository notificationRepository;
    private final ObjectMapper mapper;

    public NotificationConsumer(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;

        // Setup ObjectMapper with JavaTimeModule to handle LocalDateTime
        this.mapper = new ObjectMapper();
        this.mapper.registerModule(new JavaTimeModule());
        this.mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @PostConstruct
    public void init() {
        System.out.println("🚀 NotificationConsumer initialized — waiting for messages...");
    }


    @KafkaListener(
            topics = "txn-initiated",
            groupId = "notification-group-v2"
    )
    public void consumeTransaction(Transaction event) {

        Long txnId = event.getTransactionId();
        System.out.println("📥 Received transaction: " + event);

        try {
            if (notificationRepository.existsByTransactionId(txnId)) {
                System.out.println("⚠️ Duplicate notification ignored for txn " + txnId);
                return; // ✅ SUCCESS
            }

            Notification notification = new Notification();
            notification.setTransactionId(txnId);
            notification.setUserId(String.valueOf(event.getSenderId()));
            notification.setMessage("Payment successful");
            notification.setSentAt(LocalDateTime.now());

            notificationRepository.save(notification);

        } catch (DataIntegrityViolationException ex) {
            // 🔥 THIS IS THE KEY FIX
            System.out.println("⚠️ Duplicate notification detected at DB for txn " + txnId);
            // swallow exception → Kafka commits offset
        }
    }

}

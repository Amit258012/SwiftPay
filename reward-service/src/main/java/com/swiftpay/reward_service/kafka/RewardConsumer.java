package com.swiftpay.reward_service.kafka;

import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.swiftpay.reward_service.dto.Transaction;
import com.swiftpay.reward_service.entity.Reward;
import com.swiftpay.reward_service.repository.RewardRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;

@Component
public class RewardConsumer {
    private final RewardRepository rewardRepository;
    private final ObjectMapper mapper;

    public RewardConsumer(RewardRepository rewardRepository){
        this.rewardRepository = rewardRepository;

        this.mapper = new ObjectMapper();
        this.mapper.registerModule(new JavaTimeModule());
        this.mapper.disable(SerializationFeature.WRITE_DATE_KEYS_AS_TIMESTAMPS);
    }



    @PostConstruct
    public void init(){
        System.out.println("🤩 RewardService initialized waiting for messages...");
    }

    @KafkaListener(topics = "txn-initiated", groupId = "reward-group")
    public void consumerTransaction(Transaction transaction){
        try {
            Long txnId = transaction.getTransactionId();

            // 🔥 Idempotency check
            if (rewardRepository.existsByTransactionId(txnId)) {
                System.out.println("⚠️ Duplicate event ignored for txn " + txnId);
                return;
            }
            Reward reward = new Reward();
            reward.setUserId(transaction.getSenderId());
            reward.setPoints(transaction.getAmount() * 100);
            reward.setSentAt(LocalDateTime.now());
            reward.setTransactionId(transaction.getTransactionId());

            rewardRepository.save(reward);
            System.out.println("✅ Reward saved: " + reward);
        }catch (Exception e){
            System.err.println("❌ Failed to process transaction " + transaction.getSenderId() + ": " + e.getMessage());
            throw e; // Let Spring Kafka handle the retry
        }
    }
}

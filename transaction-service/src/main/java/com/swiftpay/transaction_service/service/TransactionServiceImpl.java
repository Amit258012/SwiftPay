package com.swiftpay.transaction_service.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.swiftpay.transaction_service.entity.Transaction;
import com.swiftpay.transaction_service.dto.TransferRequest;
import com.swiftpay.transaction_service.kafka.KafkaEventProducer;
import com.swiftpay.transaction_service.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TransactionServiceImpl implements TransactionService{
    private final TransactionRepository transactionRepository;
    private final ObjectMapper objectMapper;
    private final KafkaEventProducer kafkaEventProducer;

    @Autowired
    public TransactionServiceImpl(TransactionRepository transactionRepository, ObjectMapper objectMapper, KafkaEventProducer kafkaEventProducer){
        this.transactionRepository = transactionRepository;
        this.objectMapper = objectMapper;
        this.kafkaEventProducer = kafkaEventProducer;
    }
    @Override
    public Transaction createTransaction(Transaction request) {
        System.out.println("🚀 Entered createTransaction()");

        Long senderId = request.getSenderID();
        Long receiverId = request.getReceiverID();
        Double amount = request.getAmount();

        Transaction transaction = new Transaction();
        transaction.setSenderID(senderId);
        transaction.setReceiverID(receiverId);
        transaction.setAmount(amount);
        transaction.setTimeStamp(LocalDateTime.now());
        transaction.setStatus("SUCCESS");

        System.out.println("📥 Incoming Transaction object: " + transaction);

        Transaction saved = transactionRepository.save(transaction);
        System.out.println("💾 Saved Transaction from DB: " + saved);

        // ❗ Convert ENTITY → DTO
        TransferRequest dto = new TransferRequest();
        dto.setSenderId(saved.getSenderID());
        dto.setReceiverId(saved.getReceiverID());
        dto.setAmount(saved.getAmount());


        try {
            String key = String.valueOf(saved.getId());
            kafkaEventProducer.sendTransactionEvent(key, dto);  // send DTO, not entity
            System.out.println("🚀 Kafka message sent");
        } catch (Exception e) {
            System.err.println("❌ Failed to send Kafka event: " + e.getMessage());
            e.printStackTrace();
        }

        return saved;
    }


    @Override
    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }
}

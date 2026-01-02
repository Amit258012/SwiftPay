package com.swiftpay.transaction_service.entity;

import jakarta.persistence.*;
import jakarta.persistence.PrePersist;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "transaction")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long senderId;

    @Column( nullable = false)
    private Long receiverId;

    @Column(nullable = false)
    @Positive(message = "Amount must be positive")
    private Double amount;

    @Column(nullable = false)
    private LocalDateTime timeStamp;

    @Column(nullable = false)
    private String status;

    // Check timeStamp and status before transaction and set some default value
    @PrePersist
    public void perPersist(){
        if (timeStamp == null){
            timeStamp = LocalDateTime.now();
        }
        if (status == null){
            status = "PENDING";
        }
    }
}

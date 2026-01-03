package com.swiftpay.reward_service.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "rewards",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "transaction_id")
        }
)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Reward {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Double points;
    private LocalDateTime sentAt;
    private Long userId;

    @Column(name = "transaction_id", nullable = false)
    private Long transactionId;
}






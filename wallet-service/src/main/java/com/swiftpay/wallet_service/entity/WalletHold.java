package com.swiftpay.wallet_service.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "wallet_holds",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"transaction_id"})
        }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WalletHold {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "wallet_id", nullable = false)
    private Wallet wallet;

    // 🔥 THIS IS THE IDEMPOTENCY KEY
    @Column(name = "transaction_id", nullable = false)
    private Long transactionId;

    @Column(name = "hold_reference", nullable = false, unique = true)
    private String holdReference;

    @Column(nullable = false)
    private Double amount;

    @Column(nullable = false)
    private String status = "ACTIVE"; // ACTIVE, CAPTURED, RELEASED

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "expires_at")
    private LocalDateTime expiresAt;
}

package com.swiftpay.transaction_service.dto;

import lombok.Data;

@Data
public class TransactionEvent {
    private Long senderId;
    private Long receiverId;
    private Double amount;
    private Long transactionId;
}

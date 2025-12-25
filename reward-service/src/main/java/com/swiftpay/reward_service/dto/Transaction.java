package com.swiftpay.reward_service.dto;

import lombok.Data;

@Data
public class Transaction {
    private Long senderId;
    private Long receiverId;
    private Double amount;
}

package com.swiftpay.wallet_service.dto;

import lombok.Data;

@Data
public class HoldRequest {
    private Long userId;
    private String currency;
    private Long amount;
    private Long transactionId;
}

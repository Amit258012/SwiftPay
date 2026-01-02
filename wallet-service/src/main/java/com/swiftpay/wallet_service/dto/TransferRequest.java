package com.swiftpay.wallet_service.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransferRequest {
    private String transactionId;
    private Long userId;
    private Long amount;
    private String currency;
}

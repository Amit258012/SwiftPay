package com.swiftpay.wallet_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HoldResponse {
    private String holdReference;
    private Double amount;
    private String status;
    private Long transactionId;
}

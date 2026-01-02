package com.swiftpay.transaction_service.dto;

import lombok.Data;

@Data
public class CreateWalletRequest {
    private Long userId;
    private String currency;
}

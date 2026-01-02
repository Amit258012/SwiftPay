package com.swiftpay.wallet_service.dto;

import lombok.Data;

@Data
public class CreateWalletRequest {
    private Long userId;
    private String currency;
}

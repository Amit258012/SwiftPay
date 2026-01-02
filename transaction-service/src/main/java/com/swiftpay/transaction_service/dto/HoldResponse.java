package com.swiftpay.transaction_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HoldResponse {
    private String holdReference;
    private Long amount;
    private String status;
}

package com.swiftpay.user_service.client;


import com.swiftpay.user_service.dto.CreateWalletRequest;
import com.swiftpay.user_service.dto.WalletResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "wallet-service")
public interface WalletClient {

    @PostMapping("/api/v1/wallets")
    WalletResponse createWallet(
            @RequestBody CreateWalletRequest request
    );
}


package com.swiftpay.api_gateway.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
@RestController
@RequestMapping("/fallback")
public class FallbackController {

    @RequestMapping("/user")
    public Mono<ResponseEntity<String>> userFallback() {
        return Mono.just(
                ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                        .body("User Service is temporarily unavailable")
        );
    }

    @RequestMapping("/transaction")
    public Mono<ResponseEntity<String>> transactionFallback() {
        return Mono.just(
                ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                        .body("Transaction Service is temporarily unavailable")
        );
    }

    @RequestMapping("/wallet")
    public Mono<ResponseEntity<String>> walletFallback() {
        return Mono.just(
                ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                        .body("Wallet Service is temporarily unavailable")
        );
    }

    @RequestMapping("/reward")
    public Mono<ResponseEntity<String>> rewardFallback() {
        return Mono.just(
                ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                        .body("Reward Service is temporarily unavailable")
        );
    }

    @RequestMapping("/notification")
    public Mono<ResponseEntity<String>> notificationFallback() {
        return Mono.just(
                ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                        .body("Notification Service is temporarily unavailable")
        );
    }
}

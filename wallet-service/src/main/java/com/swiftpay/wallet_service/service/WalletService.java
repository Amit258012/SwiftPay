package com.swiftpay.wallet_service.service;

import com.swiftpay.wallet_service.dto.*;
import com.swiftpay.wallet_service.entity.Wallet;
import com.swiftpay.wallet_service.entity.WalletHold;
import com.swiftpay.wallet_service.exception.InsufficientFundsException;
import com.swiftpay.wallet_service.exception.WalletNotFoundException;
import com.swiftpay.wallet_service.repository.WalletHoldRepository;
import com.swiftpay.wallet_service.repository.WalletRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class WalletService {

    private static final String LOG_PREFIX = "[WALLET-SERVICE]";

    private final WalletRepository walletRepository;
    private final WalletHoldRepository walletHoldRepository;

    public WalletService(
            WalletRepository walletRepository,
            WalletHoldRepository walletHoldRepository
    ) {
        this.walletRepository = walletRepository;
        this.walletHoldRepository = walletHoldRepository;
    }

    // ---------------- CREATE WALLET ----------------

    @Transactional
    public WalletResponse createWallet(CreateWalletRequest request) {

        System.out.println(LOG_PREFIX + " [CREATE] Request received | userId="
                + request.getUserId() + ", currency=" + request.getCurrency());

        Wallet wallet = new Wallet(
                request.getUserId(),
                request.getCurrency()
        );

        Wallet saved = walletRepository.save(wallet);

        System.out.println(LOG_PREFIX + " [CREATE] Wallet created | walletId="
                + saved.getId());

        return new WalletResponse(
                saved.getId(),
                saved.getUserId(),
                saved.getCurrency(),
                saved.getBalance(),
                saved.getAvailableBalance()
        );
    }

    // ---------------- CREDIT ----------------
    // Used for add-money, refunds, cashback, etc.

    @Transactional
    public WalletResponse credit(CreditRequest request) {

        System.out.println(LOG_PREFIX + " [CREDIT] Incoming | userId="
                + request.getUserId() + ", amount="
                + request.getAmount());

        Wallet wallet = walletRepository
                .findByUserIdAndCurrency(
                        request.getUserId(),
                        request.getCurrency()
                )
                .orElseThrow(() -> {
                    System.out.println(LOG_PREFIX + " [CREDIT][ERROR] Wallet not found");
                    return new WalletNotFoundException("Wallet not found");
                });

        wallet.setBalance(wallet.getBalance() + request.getAmount());
        wallet.setAvailableBalance(
                wallet.getAvailableBalance() + request.getAmount()
        );

        Wallet saved = walletRepository.save(wallet);

        System.out.println(LOG_PREFIX + " [CREDIT][SUCCESS] walletId="
                + saved.getId()
                + ", balance=" + saved.getBalance()
                + ", available=" + saved.getAvailableBalance());

        return new WalletResponse(
                saved.getId(),
                saved.getUserId(),
                saved.getCurrency(),
                saved.getBalance(),
                saved.getAvailableBalance()
        );
    }

    // ---------------- DEBIT ----------------
    // Logical debit = HOLD (authorization)

    @Transactional
    public WalletResponse debit(DebitRequest request) {

        System.out.println("💸 DEBIT request received: userId=" + request.getUserId() +
                ", amount=" + request.getAmount() +
                ", currency=" + request.getCurrency());

        Wallet wallet = walletRepository.findByUserIdAndCurrency(request.getUserId(), "INR")
                .orElseThrow(() -> new WalletNotFoundException("Wallet not found for user: "+ request.getUserId()));

        if (wallet.getAvailableBalance() < request.getAmount()) {
            throw new InsufficientFundsException("Not enough balance");
        }

        wallet.setBalance(wallet.getBalance() - request.getAmount());
        wallet.setAvailableBalance(wallet.getAvailableBalance() - request.getAmount());
        Wallet saved = walletRepository.save(wallet);

        System.out.println("✅ DEBIT done: walletId=" + saved.getId() +
                ", newBalance=" + saved.getBalance() +
                ", availableBalance=" + saved.getAvailableBalance());

        return new WalletResponse(
                saved.getId(), saved.getUserId(), saved.getCurrency(),
                saved.getBalance(), saved.getAvailableBalance()
        );
    }

    // ---------------- HOLD ----------------
    // Called before payment execution

    @Transactional
    public HoldResponse placeHold(HoldRequest request) {

        System.out.println("🚨 HOLD REQUEST RECEIVED: " + request);
        System.out.println("🚨 transactionId = " + request.getTransactionId());

        Wallet wallet = walletRepository.findByUserIdAndCurrency(request.getUserId(), request.getCurrency())
                .orElseThrow(() -> new WalletNotFoundException("Wallet not found for user: " + request.getUserId()));

        Optional<WalletHold> existing =
                walletHoldRepository.findByTransactionId(request.getTransactionId());

        if (existing.isPresent()) {
            WalletHold hold = existing.get();
            return new HoldResponse(
                    hold.getHoldReference(),
                    hold.getAmount(),
                    hold.getStatus(),
                    hold.getTransactionId()
            );
        } // 👈 idempotent return

        if (wallet.getAvailableBalance() < request.getAmount()) {
            throw new InsufficientFundsException("Not enough balance to hold");
        }

        wallet.setAvailableBalance(wallet.getAvailableBalance() - request.getAmount());

        WalletHold hold = new WalletHold();
        hold.setWallet(wallet);
        hold.setAmount(Double.valueOf(request.getAmount()));
        hold.setHoldReference("HOLD-" + request.getTransactionId());
        hold.setTransactionId(request.getTransactionId());
        hold.setStatus("ACTIVE");

        walletRepository.save(wallet);
        walletHoldRepository.save(hold);

        return new HoldResponse(hold.getHoldReference(), hold.getAmount(), hold.getStatus(), hold.getTransactionId());
    }

    // ---------------- CAPTURE ----------------
    // Final debit after payment success

    @Transactional
    public WalletResponse captureHold(CaptureRequest request) {
        WalletHold hold = walletHoldRepository.findByHoldReference(request.getHoldReference())
                .orElseThrow(() -> new WalletNotFoundException("Hold not found"));

        if (!"ACTIVE".equals(hold.getStatus())) {
            throw new IllegalStateException("Hold is not active");
        }

        Wallet wallet = hold.getWallet();
        wallet.setBalance((long) (wallet.getBalance() - hold.getAmount()));

        hold.setStatus("CAPTURED");
        walletRepository.save(wallet);
        walletHoldRepository.save(hold);

        return new WalletResponse(wallet.getId(), wallet.getUserId(),
                wallet.getCurrency(), wallet.getBalance(), wallet.getAvailableBalance());
    }
    // ---------------- RELEASE ----------------
    // Called on failure / timeout

    @Transactional
    public HoldResponse releaseHold(String holdReference) {
        WalletHold hold = walletHoldRepository.findByHoldReference(holdReference)
                .orElseThrow(() -> new WalletNotFoundException("Hold not found"));

        if (!"ACTIVE".equals(hold.getStatus())) {
            throw new IllegalStateException("Hold is not active");
        }

        Wallet wallet = hold.getWallet();
        wallet.setAvailableBalance((long) (wallet.getAvailableBalance() + hold.getAmount()));


        hold.setStatus("RELEASED");
        walletRepository.save(wallet);
        walletHoldRepository.save(hold);

        return new HoldResponse(hold.getHoldReference(), hold.getAmount(), hold.getStatus(), hold.getTransactionId());
    }

    // ---------------- GET WALLET ----------------

    public WalletResponse getWallet(Long userId) {
        Wallet wallet = walletRepository.findByUserId(userId)
                .orElseThrow(() -> new WalletNotFoundException("Wallet not found for user: " + userId));

        return new WalletResponse(
                wallet.getId(), wallet.getUserId(), wallet.getCurrency(),
                wallet.getBalance(), wallet.getAvailableBalance()
        );
    }
}

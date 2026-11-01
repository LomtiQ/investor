package ru.investor.service;

import lombok.RequiredArgsConstructor;
import ru.investor.entity.Wallet;
import ru.investor.repository.WalletRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@RequiredArgsConstructor
@Service
public class WalletService {

    private final WalletRepository walletRepository;

    @Transactional(readOnly = true)
    public Wallet getWallet() {
        return walletRepository.findAll().stream()
                .findFirst()
                .orElseGet(() -> walletRepository.save(new Wallet(BigDecimal.ZERO)));
    }

    @Transactional
    public Wallet deposit(BigDecimal amount) {
        Wallet wallet = getWallet();
        wallet.deposit(amount);
        return walletRepository.save(wallet);
    }

    @Transactional
    public Wallet withdraw(BigDecimal amount) {
        Wallet wallet = getWallet();
        wallet.withdraw(amount);
        return walletRepository.save(wallet);
    }
}
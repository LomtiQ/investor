package ru.investor.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.investor.dto.WalletOperationRequest;
import ru.investor.entity.Wallet;
import ru.investor.repository.WalletRepository;
import ru.investor.service.WalletService;

import java.math.BigDecimal;

@RestController                    // ✅ это просто бин
@RequestMapping("/api/v1/wallet")
public class WalletController {

    private final WalletRepository walletRepository;
    private final WalletService walletService;

    public WalletController(WalletRepository walletRepository, WalletService walletService) {
        this.walletRepository = walletRepository;
        this.walletService = walletService;
    }

    @GetMapping("/get_value")
    public Wallet get_value(){
        // Берем первый кошелек или создаем новый с нулем
        return walletRepository.findAll().stream()
                .findFirst()
                .orElseGet(() -> walletRepository.save(new Wallet(BigDecimal.ZERO)));
    }

    @PostMapping("/deposit")
    public ResponseEntity<?> deposit(@RequestBody WalletOperationRequest req) {
        try {
            return ResponseEntity.ok(walletService.deposit(req.getAmount()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/withdraw")
    public ResponseEntity<?> withdraw(@RequestBody WalletOperationRequest req) {
        try {
            return ResponseEntity.ok(walletService.withdraw(req.getAmount()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

}

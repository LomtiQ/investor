package ru.investor.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.investor.dto.WalletOperationRequest;
import ru.investor.entity.Wallet;
import ru.investor.service.WalletService;

@RestController
@RequestMapping("/api/v1/wallet")
public class WalletController {

    private final WalletService walletService;

    public WalletController(WalletService walletService) {
        this.walletService = walletService;
    }

    @GetMapping("/get_value")
    public Wallet get_value(){
        // Берем первый кошелек или создаем новый с нулем
        return walletService.getWallet();
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

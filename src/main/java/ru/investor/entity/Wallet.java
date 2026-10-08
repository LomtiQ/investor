package ru.investor.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "wallet")
public class Wallet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "total_capital", nullable = false, precision = 19, scale = 2)
    private BigDecimal totalValue;

    public Wallet() {
    }

    public Wallet(BigDecimal totalCapital) {
        this.totalValue = totalCapital;
    }

    // Геттеры и сеттеры
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public BigDecimal getTotalValue() { return totalValue; }
    public void setTotalValue(BigDecimal totalCapital) { this.totalValue = totalCapital; }

    /** Пополнение: плюс к балансу */
    public void deposit(BigDecimal amount) {
        if (amount == null || amount.signum() <= 0) {
            throw new IllegalArgumentException("Сумма пополнения должна быть положительной");
        }
        this.totalValue = this.totalValue.add(amount);
    }

    /** Снятие: минус к балансу, но не больше, чем есть */
    public void withdraw(BigDecimal amount) {
        if (amount == null || amount.signum() <= 0) {
            throw new IllegalArgumentException("Сумма снятия должна быть положительной");
        }
        if (this.totalValue.compareTo(amount) < 0) {
            throw new IllegalArgumentException("Недостаточно средств на кошельке");
        }
        this.totalValue = this.totalValue.subtract(amount);
    }
}
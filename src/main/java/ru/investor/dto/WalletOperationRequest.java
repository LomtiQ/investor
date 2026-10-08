package ru.investor.dto;
import java.math.BigDecimal;

public class WalletOperationRequest {
    private BigDecimal amount;

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
}

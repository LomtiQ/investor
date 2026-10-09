package ru.investor.dto;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OperationDto {
    private String name;
    private String securityType;
    private String operationType;
    private int count;
    private BigDecimal cost;
    private BigDecimal nominal_value;
    private BigDecimal coupon;
    private LocalDate maturity_date;
    private int payout;
}


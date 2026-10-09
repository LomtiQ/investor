package ru.investor.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "bond")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Bond {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @Column(name = "nominal_value", nullable = false)
    private BigDecimal nominal_value;

    @Column(name = "maturity_date", nullable = false)
    private LocalDate maturity_date;

    @Column(name = "coupon", nullable = false)
    private BigDecimal coupon;

    @Column(name = "count", nullable = false)
    private int count;

    @Column(name = "cost", nullable = false)
    private BigDecimal cost;

    @Column(name = "payout", nullable = false)
    private int payout;
}

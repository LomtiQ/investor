package ru.investor.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.investor.dto.ForecastPoint;
import ru.investor.dto.ForecastResponseDto;
import ru.investor.entity.Bond;
import ru.investor.repository.BondRepository;
import ru.investor.repository.WalletRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class ForecastService {
    private static final DateTimeFormatter ISO = DateTimeFormatter.ISO_LOCAL_DATE;

    private final BondRepository bondRepository;
    private final WalletRepository walletRepository;

    public ForecastService(BondRepository bondRepository,
                           WalletRepository walletRepository) {
        this.bondRepository = bondRepository;
        this.walletRepository = walletRepository;
    }

    @Transactional(readOnly = true)
    public ForecastResponseDto buildForecast() {
        LocalDate today = LocalDate.now();

        // 1. Текущий капитал = свободные деньги + стоимость бумаг
        BigDecimal walletBalance = walletRepository.findBalance()
                .orElse(BigDecimal.ZERO);

        List<Bond> bonds = bondRepository.findAll();

        // 1. Ищем самую дальнюю дату погашения
        LocalDate horizon = bonds.stream()
                .map(Bond::getMaturity_date)
                .filter(java.util.Objects::nonNull)
                .max(LocalDate::compareTo)
                .orElse(today);

        BigDecimal currentCapital = walletBalance;
        for (Bond b : bonds) {
            currentCapital = currentCapital.add(
                    b.getCost()
            );
        }

        // 2. Собираем события
        List<Event> events = new ArrayList<>();

        for (Bond bond : bonds) {
            int count = bond.getCount();

            BigDecimal coupon    = nvl(bond.getCoupon());
            BigDecimal nominal   = nvl(bond.getNominal_value());
            BigDecimal buyPrice  = nvl(bond.getCost());
            LocalDate maturity   = bond.getMaturity_date();

            // --- Купоны (шаг = 1 месяц; при необходимости поменяйте на квартал) ---
            if (coupon.signum() > 0 && maturity != null && maturity.isAfter(today)) {
                LocalDate next = today.plusMonths(1);
                LocalDate limit = maturity.isBefore(horizon) ? maturity : horizon;
                while (!next.isAfter(limit)) {
                    events.add(new Event(
                            next,
                            coupon.multiply(BigDecimal.valueOf(count)),
                            "Купон: " + bond.getName()
                    ));
                    next = next.plusMonths(1);
                }
            }

            // --- Погашение по номиналу ---
            if (maturity != null
                    && maturity.isAfter(today)
                    && !maturity.isAfter(horizon)) {
                BigDecimal profit = nominal.subtract(buyPrice)
                        .multiply(BigDecimal.valueOf(count));
                events.add(new Event(
                        maturity,
                        profit,
                        "Погашение: " + bond.getName()
                ));
            }
        }

        // 3. Сортируем и накапливаем
        events.sort(Comparator.comparing(Event::date));

        List<ForecastPoint> points = new ArrayList<>();
        points.add(new ForecastPoint(today.format(ISO), currentCapital.doubleValue(), null));

        BigDecimal running = currentCapital;
        for (Event ev : events) {
            running = running.add(ev.amount());
            points.add(new ForecastPoint(
                    ev.date().format(ISO),
                    running.setScale(2, RoundingMode.HALF_UP).doubleValue(),
                    ev.description()
            ));
        }

        return new ForecastResponseDto(
                currentCapital.setScale(2, RoundingMode.HALF_UP).doubleValue(),
                running.setScale(2, RoundingMode.HALF_UP).doubleValue(),
                points
        );
    }

    private static BigDecimal nvl(BigDecimal v) {
        return v == null ? BigDecimal.ZERO : v;
    }

    private record Event(LocalDate date, BigDecimal amount, String description) {}
}

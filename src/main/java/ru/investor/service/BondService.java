package ru.investor.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;
import ru.investor.entity.Bond;
import ru.investor.repository.BondRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BondService {
    private final BondRepository bondRepository;

    public BondService(BondRepository bondRepository) {
        this.bondRepository = bondRepository;
    }

    @Transactional(readOnly = true)
    public List<Bond> getBonds() {
        return bondRepository.findAll();
    }

    @Transactional(readOnly = true)
    public BigDecimal getSumCost() {
        return bondRepository.sumCost();
    }

    @Transactional(readOnly = true)
    public Bond setBonds(Bond bond) {
        return bondRepository.save(bond);
    }

    public List<String> getAllBondNames() {
        return bondRepository.findAll().stream()
                .map(Bond::getName)
                .distinct()
                .collect(Collectors.toList());
    }

    public Bond getBondInfo(String name) {
        return bondRepository.findByName(name)
                .orElseThrow(() -> new RuntimeException("Облигация не найдена"));
    }
}

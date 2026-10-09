package ru.investor.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.investor.entity.Bond;
import ru.investor.repository.BondRepository;

import java.math.BigDecimal;
import java.util.List;

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
}

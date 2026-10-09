package ru.investor.service;

import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import ru.investor.entity.Bond;
import ru.investor.repository.BondRepository;

@RequiredArgsConstructor
public class OperationService {

    private final BondRepository bondRepository;

    @Transactional(readOnly = true)
    public Bond purchase(Bond bond) {
        return bondRepository.save(bond);
    }

    @Transactional(readOnly = true)
    public Bond sale(Bond bond) {
        return bondRepository.save(bond);
    }
}

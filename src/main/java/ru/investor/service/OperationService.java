package ru.investor.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.investor.entity.Bond;
import ru.investor.repository.BondRepository;

@RequiredArgsConstructor
@Service
public class OperationService {

    private final BondRepository bondRepository;

    @Transactional(readOnly = true)
    public Bond purchase(Bond bond) {
        return bondRepository.save(bond);
    }

    @Transactional()
    public void sale(String name) {
        Bond bond = bondRepository.findByName(name)
                .orElseThrow(() -> new RuntimeException("Бумага не найдена: name"));
        bondRepository.delete(bond);
    }
}

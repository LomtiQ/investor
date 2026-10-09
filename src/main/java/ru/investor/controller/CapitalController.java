package ru.investor.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.investor.dto.CapitalDto;
import ru.investor.service.BondService;
import ru.investor.service.WalletService;

@RestController
@RequestMapping("/api/v1/capital")
@RequiredArgsConstructor
public class CapitalController {

    private final WalletService walletService;
    private final BondService bondService;

    @GetMapping("/get_value")
    public CapitalDto get_value(){
        // Берем первый кошелек или создаем новый с нулем
        CapitalDto dto = new CapitalDto(walletService.getWallet().getTotalValue());
        dto.setTotalCapital(dto.getTotalCapital().add(bondService.getSumCost()));
        return dto;
    }

}

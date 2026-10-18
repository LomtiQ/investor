package ru.investor.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.investor.dto.CapitalDto;
import ru.investor.dto.ForecastResponseDto;
import ru.investor.service.BondService;
import ru.investor.service.ForecastService;
import ru.investor.service.WalletService;

@RestController
@RequestMapping("/api/v1/capital")
@RequiredArgsConstructor
public class CapitalController {

    private final WalletService walletService;
    private final BondService bondService;
    private final ForecastService forecastService;

    @GetMapping("/get_value")
    public CapitalDto get_value(){
        // Берем первый кошелек или создаем новый с нулем
        CapitalDto dto = new CapitalDto(walletService.getWallet().getTotalValue());
        dto.setTotalCapital(dto.getTotalCapital().add(bondService.getSumCost()));
        return dto;
    }

    @GetMapping("/forecast")
    public ForecastResponseDto forecast(){
        return forecastService.buildForecast();
    }

//    @GetMapping("/forecast")
//    public String forecast(){
//        return "";
//    }
}

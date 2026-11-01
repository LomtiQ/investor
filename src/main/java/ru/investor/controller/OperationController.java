package ru.investor.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.investor.dto.OperationDto;
import ru.investor.dto.OperationStatusDto;
import ru.investor.entity.Bond;
import ru.investor.repository.BondRepository;
import ru.investor.service.BondService;
import ru.investor.service.OperationService;
import ru.investor.service.WalletService;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/v1/operation")
@RequiredArgsConstructor
public class OperationController {

    private final BondService bondService;
    private final OperationService operationService;
    private final WalletService wallet;

    private final BondRepository bondRepository;

    @PostMapping("/new_operation")
    public OperationStatusDto new_operation(@RequestBody OperationDto operationDto){
        return operationService.new_operation(operationDto);
    }

}

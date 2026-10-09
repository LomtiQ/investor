package ru.investor.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.investor.dto.OperationDto;
import ru.investor.entity.Bond;
import ru.investor.repository.BondRepository;
import ru.investor.service.BondService;
import ru.investor.service.OperationService;

@RestController
@RequestMapping("/api/v1/operation")
@RequiredArgsConstructor
public class OperationController {

    private final BondService bondService;
    private final OperationService operationService;

    private final BondRepository bondRepository;

    @PostMapping("/new")
    public OperationDto new_operation(@RequestBody OperationDto bondDto){
        /// 1 - Определить тип бумпги и операцию
        /// 2 - если покупка облигации, то
        ///     - Проврить есть ли достаточное количество денег на кошельке + списание
        ///     - Существует ли такая бумага или она новая
        Bond bond = new Bond();
        switch (bondDto.getOperationType()){
            case "BUY" :

                bond.setName(bondDto.getName());
                bond.setCount(bondDto.getCount());
                bond.setCost(bondDto.getCost());
                bond.setNominal_value(bondDto.getNominal_value());
                bond.setCoupon(bondDto.getCoupon());
                bond.setName(bondDto.getName());
                bond.setMaturity_date(bondDto.getMaturity_date());
                bond.setPayout(bondDto.getPayout());
                operationService.purchase(bond);
                break;
            case "SELL":

                bond.setName(bondDto.getName());
                bond.setCount(bondDto.getCount());
                bond.setCost(bondDto.getCost());
                bond.setNominal_value(bondDto.getNominal_value());
                bond.setCoupon(bondDto.getCoupon());
                bond.setName(bondDto.getName());
                bond.setMaturity_date(bondDto.getMaturity_date());
                bond.setPayout(bondDto.getPayout());
                operationService.sale(bond);
                break;
            case "COUPON":
                break;
            case "REDEMPTION":
                break;
            default:
                break;
        }

        return bondDto;
    }

}

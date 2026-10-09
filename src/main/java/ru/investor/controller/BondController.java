package ru.investor.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.investor.entity.Bond;
import ru.investor.service.BondService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/bond")
public class BondController {
    private final BondService bondService;

    public BondController(BondService bondService) {
        this.bondService = bondService;
    }

    @GetMapping("/get_all")
    public List<Bond> get_all(){
        return bondService.getBonds();
    }

}

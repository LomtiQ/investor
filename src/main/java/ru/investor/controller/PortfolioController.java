package ru.investor.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;

@Controller
public class PortfolioController {

    @GetMapping("/")
    public String index(){
        return "home";
    }

    @GetMapping("/bond")
    public String bond(){
        return "bond";
    }

}

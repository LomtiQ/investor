package ru.investor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}

/// что нужно доработать:
/// - при покупке снимать с кошелька
/// - при продаже зачислять на кошелек
/// - при продаже учитывать количество бумаг
/// - сделать страничку с историей операций
/// - доработать операции купон и погашение
/// - вынести операции в отдельные модальные окна
/// - выводить информацию о доходности по каждой отдельной бумаге
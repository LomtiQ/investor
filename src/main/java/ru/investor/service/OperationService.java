package ru.investor.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.investor.dto.OperationDto;
import ru.investor.dto.OperationStatusDto;
import ru.investor.entity.Bond;
import ru.investor.repository.BondRepository;

import java.math.BigDecimal;

@RequiredArgsConstructor
@Service
public class OperationService {

    private final BondRepository bondRepository;

    private final WalletService wallet;

    public OperationStatusDto new_operation(OperationDto operationDto){
        OperationStatusDto operationStatusDto = new OperationStatusDto();
        Bond bond = new Bond();

        switch (operationDto.getOperationType()){
            case "BUY" :
                // если достаточно средств, нужно добавить покупку и снять с кошелька
                int difference = wallet.getWallet().getTotalValue().compareTo(operationDto.getCost());
                if (difference >= 0){
                    // проверяем есть ли информация о бумаге
                    if(it_has(operationDto.getName())){
                        //инкремент количества и стоимости
                        wallet.withdraw(BigDecimal.valueOf(difference));
                        add_count(operationDto.getName(), operationDto.getCount());
                    }else{
                        bond.setName(operationDto.getName());
                        bond.setCount(operationDto.getCount());
                        bond.setCost(operationDto.getCost());
                        bond.setNominal_value(operationDto.getNominal_value());
                        bond.setCoupon(operationDto.getCoupon());
                        bond.setMaturity_date(operationDto.getMaturity_date());
                        bond.setPayout(operationDto.getPayout());
                        purchase(bond);
                        wallet.withdraw(BigDecimal.valueOf(difference));
                    }
                    operationStatusDto.setStatus("ok");
                    operationStatusDto.setMessage("");
                }else {
                    operationStatusDto.setStatus("error");
                    operationStatusDto.setMessage("Недостаточно средств");
                }
                break;
            case "SELL":
                /// если есть такая бумага и в должном колличестве
                if (it_has(operationDto.getName())){
                    if (get_count(operationDto.getName()) - operationDto.getCount() > 0){
                        reduce_count(bond.getName(), operationDto.getCount());
                        wallet.deposit(operationDto.getCost());
                        operationStatusDto.setStatus("ok");
                        operationStatusDto.setMessage("");
                    } else if (get_count(operationDto.getName()) - operationDto.getCount() == 0) {
                        full_sale(bond.getName());
                        wallet.deposit(operationDto.getCost());
                        operationStatusDto.setStatus("ok");
                        operationStatusDto.setMessage("");
                    } else {
                        operationStatusDto.setStatus("error");
                        operationStatusDto.setMessage("Нет сколько в наличии");
                    }
                }else {
                    operationStatusDto.setStatus("error");
                    operationStatusDto.setMessage("Нет такой бумаги");
                }
                break;
            case "COUPON":
                // если есть такая бумага
                if(it_has(operationDto.getName())){
                    // Вычисляем размер выплаты
                    BigDecimal payout =
                            bondRepository.findByName(operationDto.getName()).get().getCoupon()
                                    .multiply(
                                            BigDecimal.valueOf(bondRepository.findByName(operationDto.getName()).get().getCount())
                                    );
                    // Зачисляем выплату на кошелек
                    wallet.deposit(payout);
                    // Уменьшаем количество оставшихся выплат
                    reduce_payout(operationDto.getName());
                    operationStatusDto.setStatus("ok");
                    operationStatusDto.setMessage("");
                }else {
                    operationStatusDto.setStatus("error");
                    operationStatusDto.setMessage("Нет такой бумаги");
                }
                break;
            case "REDEMPTION":
                /// если есть такая бумага
                if(it_has(operationDto.getName())){
                    // Вычисляем размер выплаты
                    BigDecimal payout =
                            bondRepository.findByName(operationDto.getName()).get().getCoupon()
                                    .multiply(
                                            BigDecimal.valueOf(bondRepository.findByName(operationDto.getName()).get().getCount())
                                    );
                    // Зачисляем выплату на кошелек
                    wallet.deposit(payout);
                    // Вычисляем стоимость по номиналу
                    BigDecimal nominal_cost =
                            bondRepository.findByName(operationDto.getName()).get().getNominal_value()
                                    .multiply(
                                            BigDecimal.valueOf(bondRepository.findByName(operationDto.getName()).get().getCount())
                                    );
                    // Зачисляем стоимость по номиналу на кошелек
                    wallet.deposit(nominal_cost);
                    // Удаляем бумагу
                    full_sale(bond.getName());
                    operationStatusDto.setStatus("ok");
                    operationStatusDto.setMessage("");
                }else {
                    operationStatusDto.setStatus("error");
                    operationStatusDto.setMessage("Нет такой бумаги");
                }
                break;
            default:
                break;
        }
        return operationStatusDto;
    }

    /// Покупка новой бумаги
    @Transactional(readOnly = true)
    private Bond purchase(Bond bond) {
        return bondRepository.save(bond);
    }

    /// проверка на существование такой бумаги
    private boolean it_has(String name){
        return bondRepository.findByName(name).isPresent();
    }

    /// Возвращает колличество
    private int get_count(String name){
        return bondRepository.findByName(name).get().getCount();
    }

    /// увеличение количества бумаг
    @Transactional
    private void add_count(String name, int count){
        Bond bond = bondRepository.findByName(name).orElseThrow(() -> new EntityNotFoundException("Bond not found: " + name));;
        bond.setCount(bond.getCount() + count);
        bondRepository.save(bond);
    }

    /// уменьшение количества бумаг
    @Transactional
    private void reduce_count(String name, int count){
        Bond bond = bondRepository.findByName(name).orElseThrow(() -> new EntityNotFoundException("Bond not found: " + name));;
        if (bond.getCount() - count >= 0){
            bond.setCount(bond.getCount() - count);
            bondRepository.save(bond);
        }
    }

    /// уменьшение количества выплат
    @Transactional
    private void reduce_payout(String name){
        Bond bond = bondRepository.findByName(name).orElseThrow(() -> new EntityNotFoundException("Bond not found: " + name));;
        if (bond.getPayout() - 1 > 0){
            bond.setPayout(bond.getPayout() - 1);
            bondRepository.save(bond);
        }
    }

    /// Продажа бумаги полностью
    @Transactional()
    private void full_sale(String name) {
        Bond bond = bondRepository.findByName(name)
                .orElseThrow(() -> new RuntimeException("Бумага не найдена: name"));
        bondRepository.delete(bond);
    }
}

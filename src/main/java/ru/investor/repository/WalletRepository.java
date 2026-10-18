package ru.investor.repository;

import org.springframework.data.jpa.repository.Query;
import ru.investor.entity.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Optional;

@Repository
public interface WalletRepository extends JpaRepository<Wallet, Long> {

    @Query("select w.totalValue from Wallet w order by w.id asc")
    Optional<BigDecimal> findBalance();

}
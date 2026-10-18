package ru.investor.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.investor.entity.Bond;

import java.math.BigDecimal;
import java.util.Optional;

public interface BondRepository extends JpaRepository<Bond, Long> {

    @Query("SELECT COALESCE(SUM(b.cost), 0) FROM Bond b")
    BigDecimal sumCost();

    Optional<Bond> findByName(String name);

}

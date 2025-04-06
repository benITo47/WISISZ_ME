package me.wisisz.repository;

import me.wisisz.model.Currency;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CurrencyRepository extends JpaRepository<Currency, String> {
    // Custom queries can be added here if needed
}

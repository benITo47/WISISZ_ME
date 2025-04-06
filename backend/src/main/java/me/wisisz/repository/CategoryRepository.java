package me.wisisz.repository;

import me.wisisz.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Integer> {
    // Custom queries can be added here if needed
}

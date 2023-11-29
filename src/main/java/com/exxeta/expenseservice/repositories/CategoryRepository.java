package com.exxeta.expenseservice.repositories;

import com.exxeta.expenseservice.entities.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    Category findCategoryByName(String name);
    Optional<Category> findCategoryByUserIdAndName(String userId, String name);
    List<Category> findAllByUserId(String userId);
}

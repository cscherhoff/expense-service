package com.exxeta.expenseservice.repositories;


import com.exxeta.expenseservice.entities.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    List<Expense> findExpensesByDateIsGreaterThanEqualAndUserId(LocalDate date, long userId);
    List<Expense> findAllByUserId(long userId);
//    List<Expense> findExpensesByUserId(long userId);
}

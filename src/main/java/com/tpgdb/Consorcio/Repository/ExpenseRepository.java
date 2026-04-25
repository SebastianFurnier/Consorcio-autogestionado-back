package com.tpgdb.Consorcio.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tpgdb.Consorcio.Model.Expense;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {
        List<Expense> findByConsorcioId(Long consorcioId);
}
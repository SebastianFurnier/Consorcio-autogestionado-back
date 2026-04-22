package com.tpgdb.Consorcio.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tpgdb.Consorcio.Model.Expense;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {
}
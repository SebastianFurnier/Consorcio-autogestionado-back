package com.tpgdb.Consorcio.Repository;

import java.util.List;
import java.time.LocalDate;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tpgdb.Consorcio.Model.Expense;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long> {
        List<Expense> findByConsorcioId(Long consorcioId);

        List<Expense> findApprovedByConsorcioId(Long consorcioId);

        List<Expense> findByConsorcioIdAndDateBetween(Long consorcioId, LocalDate start, LocalDate end);
}

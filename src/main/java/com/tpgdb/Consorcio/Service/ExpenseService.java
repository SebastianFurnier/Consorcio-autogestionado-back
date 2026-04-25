package com.tpgdb.Consorcio.Service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.tpgdb.Consorcio.Dto.ExpenseRequestDto;
import com.tpgdb.Consorcio.Exception.InvalidConsorcioException;
import com.tpgdb.Consorcio.Model.Consorcio;
import com.tpgdb.Consorcio.Model.Expense;
import com.tpgdb.Consorcio.Repository.ConsorcioRepository;
import com.tpgdb.Consorcio.Repository.ExpenseRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ExpenseService {
        private final ExpenseRepository expenseRepository;
        private final ConsorcioRepository consorcioRepository;      
        
        public void createExpense(ExpenseRequestDto dto) {      
            Consorcio consorcio = consorcioRepository
            .findByIdAndActiveIsTrue(dto.getConsorcioId())
            .orElseThrow(() -> new InvalidConsorcioException("El consorcio no existe"));
            Expense expense = new Expense(
                    dto.getAmount(),
                    dto.getDescription(),
                    dto.getDate(),
                    consorcio
            );  
            expenseRepository.save(expense);
        }

        public List<ExpenseRequestDto> getAllExpensesOfConsortium(Long consorcioId) {
                List<Expense> expenses = expenseRepository.findByConsorcioId(consorcioId);
                return expenses
                        .stream()
                        .map( expense -> new ExpenseRequestDto(
                                expense.getId(),
                                expense.getAmount(), 
                                expense.getDescription(), 
                                expense.getDate(), 
                                expense.getConsorcio().getId())
                        ).toList();
        }
}

package com.tpgdb.Consorcio.Service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.tpgdb.Consorcio.Dto.ExpenseRequestDto;
import com.tpgdb.Consorcio.Exception.InvalidPartnerIDException;
import com.tpgdb.Consorcio.Model.Expense;
import com.tpgdb.Consorcio.Model.Partner;
import com.tpgdb.Consorcio.Repository.ExpenseRepository;
import com.tpgdb.Consorcio.Repository.PartnerRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ExpenseService {
        private final ExpenseRepository expenseRepository;
        private final PartnerRepository partnerRepository;      
        
        public void createExpense(ExpenseRequestDto dto) {      
            Partner partner = partnerRepository
                    .findByIdAndActiveIsTrue(dto.getPartnerId())
                    .orElseThrow(() ->
                            new InvalidPartnerIDException("El id no esta asociado a ningun socio")); 
            Expense expense = new Expense(
                    dto.getAmount(),
                    dto.getDescription(),
                    dto.getDate(),
                    partner
            );  
            expenseRepository.save(expense);
        }

        public List<ExpenseRequestDto> getAllExpenses() {
                List<Expense> expenses = expenseRepository.findAll();
                return expenses
                        .stream()
                        .map( expense -> new ExpenseRequestDto(
                                expense.getId(),
                                expense.getAmount(), 
                                expense.getDescription(), 
                                expense.getDate(), 
                                expense.getPartner().getId())
                        ).toList();
        }
}

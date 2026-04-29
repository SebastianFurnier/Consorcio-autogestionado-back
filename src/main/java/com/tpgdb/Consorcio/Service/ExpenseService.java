package com.tpgdb.Consorcio.Service;

import java.util.List;

import com.tpgdb.Consorcio.Dto.Expense.ExpenseResponseDto;
import com.tpgdb.Consorcio.Exception.InvalidDataPartnerException;
import com.tpgdb.Consorcio.Model.Partner;
import com.tpgdb.Consorcio.Repository.PartnerRepository;
import org.springframework.stereotype.Service;

import com.tpgdb.Consorcio.Dto.Expense.ExpenseRequestDto;
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
        private final PartnerRepository partnerRepository;
        
        public void createExpense(ExpenseRequestDto dto) {      
            Consorcio consorcio = consorcioRepository
            .findByIdAndActiveIsTrue(dto.getConsorcioId())
            .orElseThrow(() -> new InvalidConsorcioException("El consorcio no existe"));

            Partner partner = partnerRepository.findById(dto.getPartnerId())
                    .orElseThrow(() -> new InvalidDataPartnerException("No existe partner con este ID."));

            Expense expense = new Expense(
                    dto.getAmount(),
                    dto.getDescription(),
                    dto.getDate(),
                    partner,
                    consorcio,
                    dto.getCategory()
            );  
            expenseRepository.save(expense);
        }

        public List<ExpenseResponseDto> getAllExpensesOfConsortium(Long consorcioId) {
                List<Expense> expenses = expenseRepository.findByConsorcioId(consorcioId);
                return expenses
                        .stream()
                        .map( expense ->  {
                                    Partner partner = expense.getPartner();
                                    return new ExpenseResponseDto(
                                            expense.getId(),
                                            expense.getDate(),
                                            expense.getDescription(),
                                            expense.getCategory(),
                                            partner.getId(),
                                            expense.getAmount(),
                                            expense.isApproved()
                                    );
                                }
                        ).toList();
        }
}

package com.tpgdb.Consorcio.Service;

import com.tpgdb.Consorcio.Dto.Debt.DebtResponseDto;
import com.tpgdb.Consorcio.Model.Debt;
import com.tpgdb.Consorcio.Model.Expense;
import com.tpgdb.Consorcio.Repository.DebtRepository;
import com.tpgdb.Consorcio.Repository.ExpenseRepository;
import com.tpgdb.Consorcio.Repository.PartnerRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class DebtService {
    private final DebtRepository debtRepository;
    private final PartnerRepository partnerRepository;

    public List<DebtResponseDto> getDebtFromPartnerAndConsorcio(Long consorcioId, Long partnerId) {
        List<Debt> debtList = debtRepository.findByPaidIsFalseAndConsorcio_idAndPartner_id(
                consorcioId, partnerId);

        return debtList.stream()
                .map(debt -> {
                    DebtResponseDto debtResponseDto = new DebtResponseDto();
                    debtResponseDto.setAmount(debt.getAmount());
                    debtResponseDto.setId(debt.getId());

                    Expense expense = debt.getExpense();
                    debtResponseDto.setDescription(expense.getDescription());

                    return debtResponseDto;
                }).toList();
    }

    public Integer getSociosAlDia(Long consorcioId) {
        int amountPartner = partnerRepository.countPartnerByActiveTrueAndConsorcio_id(consorcioId);
        return amountPartner - debtRepository.countDistinctPartnersWithUnpaidDebts(consorcioId);
    }
}

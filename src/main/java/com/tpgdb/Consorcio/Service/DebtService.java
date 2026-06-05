package com.tpgdb.Consorcio.Service;

import com.tpgdb.Consorcio.Dto.Debt.DebtResponseDto;
import com.tpgdb.Consorcio.Model.ConsorcioSettings;
import com.tpgdb.Consorcio.Model.Debt;
import com.tpgdb.Consorcio.Model.Expense;
import com.tpgdb.Consorcio.Repository.DebtRepository;
import com.tpgdb.Consorcio.Repository.PartnerRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class DebtService {
    private final DebtRepository debtRepository;
    private final PartnerRepository partnerRepository;
    private final DebtStatusCalculator debtStatusCalculator;
    private final LateFeeCalculatorService lateFeeCalculatorService;

    public List<DebtResponseDto> getDebtFromPartnerAndConsorcio(Long consorcioId, Long partnerId) {
        List<Debt> debtList = debtRepository.findByPaidIsFalseAndConsorcio_idAndPartner_id(
                consorcioId, partnerId);

        // Cargamos la configuración una vez para todas las deudas del consorcio
        ConsorcioSettings settings = lateFeeCalculatorService.getOrDefaultSettings(consorcioId);
        int gracePeriodDays = settings.getGracePeriodDays();

        return debtList.stream()
                .map(debt -> {
                    DebtResponseDto dto = new DebtResponseDto();
                    dto.setAmount(debt.getAmount());
                    dto.setId(debt.getId());
                    dto.setDueDate(debtStatusCalculator.getDueDate(debt));
                    dto.setStatus(debtStatusCalculator.getStatus(debt, java.time.LocalDate.now(), gracePeriodDays));
                    dto.setDaysOverdue(debtStatusCalculator.getDaysOverdue(debt));
                    dto.setDaysInMorosity(debtStatusCalculator.getDaysInMorosity(debt, gracePeriodDays));

                    // Interés acumulado por mora
                    float interest = lateFeeCalculatorService.calculateAccruedInterest(debt, settings);
                    dto.setInterestAccrued(interest);
                    dto.setTotalOwed(debt.getAmount() + interest);

                    Expense expense = debt.getExpense();
                    dto.setDescription(expense.getDescription());
                    dto.setExpenseId(expense != null ? expense.getId() : null);

                    return dto;
                }).toList();
    }

    public Integer getSociosAlDia(Long consorcioId) {
        int amountPartner = partnerRepository.countPartnerByActiveTrueAndConsorcio_id(consorcioId);
        return amountPartner - debtRepository.countDistinctPartnersWithUnpaidDebts(consorcioId);
    }
}

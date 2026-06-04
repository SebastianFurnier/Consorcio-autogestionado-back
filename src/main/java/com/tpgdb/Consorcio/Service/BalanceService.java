package com.tpgdb.Consorcio.Service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.tpgdb.Consorcio.Dto.Balance.BalanceResponseDto;
import com.tpgdb.Consorcio.Dto.Balance.PartnerBalance;
import com.tpgdb.Consorcio.Model.Debt;
import com.tpgdb.Consorcio.Model.DebtStatus;
import com.tpgdb.Consorcio.Model.Expense;
import com.tpgdb.Consorcio.Model.Partner;
import com.tpgdb.Consorcio.Model.Payment;
import com.tpgdb.Consorcio.Repository.DebtRepository;
import com.tpgdb.Consorcio.Repository.ExpenseRepository;
import com.tpgdb.Consorcio.Repository.PartnerRepository;
import com.tpgdb.Consorcio.Repository.PaymentRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BalanceService {
        private final PartnerRepository partnerRepository;
        private final DebtRepository debtRepository;
        private final ExpenseRepository expenseRepository;
        private final PaymentRepository paymentRepository;
        private final DebtStatusCalculator debtStatusCalculator;

        public BalanceResponseDto getBalanceOfConsorcio(Long consorcioId) {
                return getBalanceOfConsorcio(consorcioId, null);
        }

        public BalanceResponseDto getBalanceOfConsorcio(Long consorcioId, String period) {
                final LocalDate start;
                final LocalDate end;

                if (period != null) {
                        LocalDate base = LocalDate.parse(period);
                        YearMonth ym = YearMonth.from(base);
                        start = ym.atDay(1);
                        end = ym.atEndOfMonth();
                } else {
                        start = null;
                        end = null;
                }

                // Expenses (total + count)
                float totalExpenses;
                int countExpenses;
                List<Expense> expensesInRange;
                if (start != null && end != null) {
                        expensesInRange = expenseRepository.findByConsorcioIdAndDateBetween(consorcioId, start, end);
                } else {
                        expensesInRange = expenseRepository.findByConsorcioId(consorcioId);
                }
                totalExpenses = (float) expensesInRange.stream().mapToDouble(Expense::getAmount).sum();
                countExpenses = expensesInRange.size();

                // Payments (total + count)
                float totalPayments;
                int countPayments;
                List<Payment> paymentsInRange;
                if (start != null && end != null) {
                        paymentsInRange = paymentRepository.findByConsorcioIdAndPeriodBetween(consorcioId, start, end);
                } else {
                        paymentsInRange = paymentRepository.findByConsorcioId(consorcioId);
                }
                totalPayments = (float) paymentsInRange.stream().mapToDouble(Payment::getAmount).sum();
                countPayments = paymentsInRange.size();

                // Debts in period (for counts)
                List<Debt> debtsInRange;
                if (start != null && end != null) {
                        debtsInRange = debtRepository.findByConsorcio_idAndExpense_DateBetween(consorcioId, start, end);
                } else {
                        debtsInRange = debtRepository.findByConsorcio_id(consorcioId);
                }
                int countDebtsPending = (int) debtsInRange.stream().filter(d -> !d.isPaid()).count();
                int countOverdueDebts = (int) debtsInRange.stream()
                                .filter(debt -> debtStatusCalculator.getStatus(debt) == DebtStatus.VENCIDA)
                                .count();
                int countMoroseDebts = (int) debtsInRange.stream()
                                .filter(debt -> debtStatusCalculator.getStatus(debt) == DebtStatus.EN_MORA)
                                .count();
                float totalOverdueDebt = (float) debtsInRange.stream()
                                .filter(debt -> debtStatusCalculator.getStatus(debt) == DebtStatus.VENCIDA
                                                || debtStatusCalculator.getStatus(debt) == DebtStatus.EN_MORA)
                                .mapToDouble(Debt::getAmount)
                                .sum();
                float totalMoroseDebt = (float) debtsInRange.stream()
                                .filter(debt -> debtStatusCalculator.getStatus(debt) == DebtStatus.EN_MORA)
                                .mapToDouble(Debt::getAmount)
                                .sum();

                BalanceResponseDto response = new BalanceResponseDto(totalExpenses);
                response.setTotalPayments(totalPayments);
                response.setCountExpenses(countExpenses);
                response.setCountPayments(countPayments);
                response.setCountDebtsPending(countDebtsPending);
                response.setDiferencia(totalPayments - totalExpenses);
                response.setTotalMora(totalMoroseDebt);
                response.setTotalOverdueDebt(totalOverdueDebt);
                response.setTotalMoroseDebt(totalMoroseDebt);
                response.setCountOverdueDebts(countOverdueDebts);
                response.setCountMoroseDebts(countMoroseDebts);

                Map<Long, PartnerBalance> partnerBalances = new HashMap<Long, PartnerBalance>();
                List<Partner> partners = partnerRepository.findByConsorcioIdAndActiveIsTrue(consorcioId);
                int countPartnersWithDebt = 0;
                int countPartnersWithOverdueDebt = 0;
                int countPartnersInMorosity = 0;

                for (Partner partner : partners) {
                        Long partnerId = partner.getId();

                        // Debt for this partner in the period
                        float partnerDebt;
                        List<Debt> partnerDebts;
                        if (start != null && end != null) {
                                partnerDebts = debtRepository
                                                .findByConsorcio_idAndPartner_idAndExpense_DateBetween(consorcioId, partnerId, start, end);
                        } else {
                                partnerDebts = debtRepository.findByConsorcio_idAndPartner_id(consorcioId, partnerId);
                        }
                        partnerDebt = (float) partnerDebts.stream().mapToDouble(Debt::getAmount).sum();

                        // Payments for this partner in the period (from paymentsInRange)
                        float partnerPayments = (float) paymentsInRange.stream()
                                                .filter(p -> p.getPartner() != null && p.getPartner().getId().equals(partnerId))
                                                .mapToDouble(Payment::getAmount)
                                                .sum();

                        PartnerBalance partnerBalance = new PartnerBalance(partnerId, partnerPayments, partnerDebt);
                        float outstandingDebt = (float) partnerDebts.stream()
                                        .filter(debt -> !debt.isPaid())
                                        .mapToDouble(Debt::getAmount)
                                        .sum();
                        float overdueDebt = (float) partnerDebts.stream()
                                        .filter(debt -> debtStatusCalculator.getStatus(debt) == DebtStatus.VENCIDA
                                                        || debtStatusCalculator.getStatus(debt) == DebtStatus.EN_MORA)
                                        .mapToDouble(Debt::getAmount)
                                        .sum();
                        float moroseDebt = (float) partnerDebts.stream()
                                        .filter(debt -> debtStatusCalculator.getStatus(debt) == DebtStatus.EN_MORA)
                                        .mapToDouble(Debt::getAmount)
                                        .sum();
                        int pendingDebts = (int) partnerDebts.stream().filter(debt -> !debt.isPaid()).count();
                        int overdueDebts = (int) partnerDebts.stream()
                                        .filter(debt -> debtStatusCalculator.getStatus(debt) == DebtStatus.VENCIDA)
                                        .count();
                        int moroseDebts = (int) partnerDebts.stream()
                                        .filter(debt -> debtStatusCalculator.getStatus(debt) == DebtStatus.EN_MORA)
                                        .count();

                        DebtStatus aggregateStatus = DebtStatus.PAGADA;
                        if (moroseDebts > 0) {
                                aggregateStatus = DebtStatus.EN_MORA;
                        } else if (overdueDebts > 0) {
                                aggregateStatus = DebtStatus.VENCIDA;
                        } else if (pendingDebts > 0) {
                                aggregateStatus = DebtStatus.PENDIENTE;
                        }

                        LocalDate nextDueDate = partnerDebts.stream()
                                        .filter(debt -> !debt.isPaid())
                                        .map(debtStatusCalculator::getDueDate)
                                        .filter(date -> date != null)
                                        .min(LocalDate::compareTo)
                                        .orElse(null);
                        LocalDate oldestDueDate = partnerDebts.stream()
                                        .filter(debt -> debtStatusCalculator.getStatus(debt) == DebtStatus.VENCIDA
                                                        || debtStatusCalculator.getStatus(debt) == DebtStatus.EN_MORA)
                                        .map(debtStatusCalculator::getDueDate)
                                        .filter(date -> date != null)
                                        .min(LocalDate::compareTo)
                                        .orElse(null);

                        partnerBalance.setOutstandingDebt(outstandingDebt);
                        partnerBalance.setOverdueDebt(overdueDebt);
                        partnerBalance.setMoroseDebt(moroseDebt);
                        partnerBalance.setPendingDebts(pendingDebts);
                        partnerBalance.setOverdueDebts(overdueDebts);
                        partnerBalance.setMoroseDebts(moroseDebts);
                        partnerBalance.setDebtStatus(aggregateStatus);
                        partnerBalance.setNextDueDate(nextDueDate);
                        partnerBalance.setOldestDueDate(oldestDueDate);

                        if (outstandingDebt > 0.01f) {
                                countPartnersWithDebt++;
                        }
                        if (overdueDebt > 0.01f) {
                                countPartnersWithOverdueDebt++;
                        }
                        if (moroseDebt > 0.01f) {
                                countPartnersInMorosity++;
                        }
                        partnerBalances.put(partnerId, partnerBalance);
                }

                response.setCountPartnersWithDebt(countPartnersWithDebt);
                response.setCountPartnersWithOverdueDebt(countPartnersWithOverdueDebt);
                response.setCountPartnersInMorosity(countPartnersInMorosity);
                response.setMorosityRate(partners.isEmpty() ? 0.0f : ((float) countPartnersInMorosity / partners.size()) * 100.0f);
                response.setPerPartnerBalance(new ArrayList<>(partnerBalances.values()));
                return response;
        }
}

package com.tpgdb.Consorcio.Service;

import java.util.List;
import com.tpgdb.Consorcio.Dto.Expense.ExpenseResponseDto;
import com.tpgdb.Consorcio.Dto.payment.PaymentRequestDto;
import com.tpgdb.Consorcio.Exception.InvalidDataPartnerException;
import com.tpgdb.Consorcio.Model.*;
import com.tpgdb.Consorcio.Repository.*;
import org.springframework.stereotype.Service;
import com.tpgdb.Consorcio.Dto.Expense.ExpenseRequestDto;
import com.tpgdb.Consorcio.Exception.InvalidConsorcioException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ExpenseService {
        private final ExpenseRepository expenseRepository;
        private final ConsorcioRepository consorcioRepository;
        private final PartnerRepository partnerRepository;
        private final DebtRepository debtRepository;
        private final PaymentRepository paymentRepository;

        public void createExpense(ExpenseRequestDto dto) {
                Consorcio consorcio = consorcioRepository
                                .findByIdAndActiveIsTrue(dto.getConsorcioId())
                                .orElseThrow(() -> new InvalidConsorcioException("El consorcio no existe"));

                Partner payer = partnerRepository.findById(dto.getPartnerId())
                                .orElseThrow(() -> new InvalidDataPartnerException("No existe partner con este ID."));

                Expense expense = new Expense(
                                dto.getAmount(),
                                dto.getDescription(),
                                dto.getDate(),
                                payer,
                                consorcio,
                                dto.getCategory());

                expenseRepository.save(expense);

                List<Partner> debtors = partnerRepository.findByConsorcioIdAndActiveIsTrue(consorcio.getId());

                float partialAmount = dto.getAmount() / debtors.size();

                for (Partner debtor : debtors) {
                    if (debtor.getId() != payer.getId()) {
                        Debt debt = new Debt(debtor, consorcio, expense, partialAmount);
                        debtRepository.save(debt);
                    }
                }

                Payment payment = new Payment();
                payment.setExpense(expense);
                payment.setReceiptUrl("");
                payment.setAmount(dto.getAmount() / debtors.size());
                payment.setDescription(dto.getDescription());
                payment.setPaymentMethod(Payment.PaymentMethod.OTHER);
                payment.setPeriod(dto.getDate());
                payment.setPartner(payer);
                payment.setPaymentDate(dto.getDate());
                payment.setConsorcioId(consorcio.getId());

                paymentRepository.save(payment);
        }

        public List<ExpenseResponseDto> getAllExpensesOfConsortium(Long consorcioId) {
                List<Expense> expenses = expenseRepository.findByConsorcioId(consorcioId);

                return expenses.stream()
                                .map(expense -> {
                                        // Verificación segura del Partner
                                        Long partnerId = (expense.getPartner() != null) ? expense.getPartner().getId()
                                                        : null;

                                        return new ExpenseResponseDto(
                                                        expense.getId(),
                                                        expense.getDate(),
                                                        expense.getDescription(),
                                                        expense.getCategory(),
                                                        partnerId,
                                                        expense.getAmount(),
                                                        expense.isApproved());
                                }).toList();
        }

        public List<ExpenseResponseDto> getApprovedExpensesOfConsortium(Long consorcioId) {
                List<Expense> approvedExpenses = expenseRepository.findApprovedByConsorcioId(consorcioId);
                return approvedExpenses.stream()
                                .map(expense -> {
                                        Long partnerId = (expense.getPartner() != null) ? expense.getPartner().getId()
                                                        : null;
                                        return new ExpenseResponseDto(
                                                        expense.getId(),
                                                        expense.getDate(),
                                                        expense.getDescription(),
                                                        expense.getCategory(),
                                                        partnerId,
                                                        expense.getAmount(),
                                                        expense.isApproved());
                                }).toList();
        }
}
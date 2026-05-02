package com.tpgdb.Consorcio.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.tpgdb.Consorcio.Dto.Balance.BalanceResponseDto;
import com.tpgdb.Consorcio.Dto.Balance.PartnerBalance;
import com.tpgdb.Consorcio.Model.Expense;
import com.tpgdb.Consorcio.Model.Partner;
import com.tpgdb.Consorcio.Model.Payment;
import com.tpgdb.Consorcio.Repository.ExpenseRepository;
import com.tpgdb.Consorcio.Repository.PartnerRepository;
import com.tpgdb.Consorcio.Repository.PaymentRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BalanceService {
        private final ExpenseRepository expenseRepository;
        private final PaymentRepository paymentRepository;
        private final PartnerRepository partnerRepository;

        public BalanceResponseDto getBalanceOfConsorcio(Long consorcioId) {
                
                float gastoTotal = (float) expenseRepository
                .findApprovedByConsorcioId(consorcioId)
                .stream()
                .mapToDouble(Expense::getAmount)
                .sum();
                
                BalanceResponseDto response = new BalanceResponseDto(gastoTotal);

                Map<Long, PartnerBalance> partnerBalances = new HashMap<Long, PartnerBalance>();        
                List<Partner> partners = partnerRepository.findByConsorcioIdAndActiveIsTrue(consorcioId);                
                partners.forEach((partner) -> {
                        Long partnerId = partner.getId();
                        PartnerBalance partnerBalance = new PartnerBalance(partnerId, partner.getParticipation() * gastoTotal / 100);
                        float monto = (float) paymentRepository.findByPartnerId(partnerId)
                        .stream()
                        .mapToDouble(Payment::getAmount)
                        .sum();
                        
                        partnerBalance.setPayments(monto);
                        partnerBalances.put(partnerId, partnerBalance);
                        response.addPayment(monto);
                });      
                response.setPerPartnerBalance(new ArrayList<>(partnerBalances.values()));
                // escoger la logica de mora
                return response;
        }
}

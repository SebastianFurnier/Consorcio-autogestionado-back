package com.tpgdb.Consorcio.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.tpgdb.Consorcio.Dto.Balance.BalanceResponseDto;
import com.tpgdb.Consorcio.Dto.Balance.PartnerBalance;
import com.tpgdb.Consorcio.Model.Debt;
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

        public BalanceResponseDto getBalanceOfConsorcio(Long consorcioId) {
                
                float gastoTotal = (float) debtRepository
                .findByConsorcio_id(consorcioId)
                .stream()
                .mapToDouble(Debt::getAmount)
                .sum();
                
                BalanceResponseDto response = new BalanceResponseDto(gastoTotal);

                Map<Long, PartnerBalance> partnerBalances = new HashMap<Long, PartnerBalance>();        
                List<Partner> partners = partnerRepository.findByConsorcioIdAndActiveIsTrue(consorcioId);                
                partners.forEach((partner) -> {
                        Long partnerId = partner.getId();

                        float partnerPayedAmount = (float) debtRepository.findByPaidIsTrueConsorcio_idAndPartner_id(consorcioId, partnerId)
                        .stream()
                        .mapToDouble(Debt::getAmount)
                        .sum();
                        float partnerNotPayedAmount = (float) debtRepository.findByPaidIsFalseAndConsorcio_idAndPartner_id(consorcioId, partnerId)
                        .stream()
                        .mapToDouble(Debt::getAmount)
                        .sum();
                        
                        // el total de las deudas del parter son el monto de las pagadas + las no pagadas
                        PartnerBalance partnerBalance = new PartnerBalance(partnerId, partnerPayedAmount, partnerNotPayedAmount + partnerPayedAmount);

                        partnerBalances.put(partnerId, partnerBalance);
                        response.addPayment(partnerPayedAmount);
                });

                response.setPerPartnerBalance(new ArrayList<>(partnerBalances.values()));
                return response;
        }
}

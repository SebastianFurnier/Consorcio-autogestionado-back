package com.tpgdb.Consorcio.Dto.Balance;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class BalanceResponseDto {
        private float totalPayments;
        private float totalExpenses;
        private List<PartnerBalance> perPartnerBalance;
        
        public BalanceResponseDto(float totalExpenses) {
                this.totalExpenses = totalExpenses;
                totalPayments = 0;
        }

        public void addPayment(float amount) {
                this.totalPayments += amount;
        }
}

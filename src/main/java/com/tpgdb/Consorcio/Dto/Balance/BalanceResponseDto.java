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
        private float diferencia;
        private float totalMora;
        private float totalOverdueDebt;
        private float totalMoroseDebt;
        private int countExpenses;
        private int countPayments;
        private int countDebtsPending;
        private int countOverdueDebts;
        private int countMoroseDebts;
        private int countPartnersWithDebt;
        private int countPartnersWithOverdueDebt;
        private int countPartnersInMorosity;
        private float morosityRate;
        private List<PartnerBalance> perPartnerBalance;

        public BalanceResponseDto(float totalExpenses) {
                this.totalExpenses = totalExpenses;
                this.totalPayments = 0;
                this.diferencia = 0;
                this.totalMora = 0.0f;
                this.totalOverdueDebt = 0.0f;
                this.totalMoroseDebt = 0.0f;
                this.countExpenses = 0;
                this.countPayments = 0;
                this.countDebtsPending = 0;
                this.countOverdueDebts = 0;
                this.countMoroseDebts = 0;
                this.countPartnersWithDebt = 0;
                this.countPartnersWithOverdueDebt = 0;
                this.countPartnersInMorosity = 0;
                this.morosityRate = 0.0f;
        }

        public void addPayment(float amount) {
                this.totalPayments += amount;
        }
}

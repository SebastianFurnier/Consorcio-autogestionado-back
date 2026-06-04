package com.tpgdb.Consorcio.Dto.Balance;

import com.tpgdb.Consorcio.Model.DebtStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class PartnerBalance {
        private Long partnerId;
        private float payments;
        private float debt;
        private float penaltyForLatePayment;
        private float outstandingDebt;
        private float overdueDebt;
        private float moroseDebt;
        private int pendingDebts;
        private int overdueDebts;
        private int moroseDebts;
        private DebtStatus debtStatus;
        private LocalDate nextDueDate;
        private LocalDate oldestDueDate;

        public PartnerBalance(Long partnerId, float payments, float debt) {
                this.partnerId = partnerId;
                this.payments = payments;
                this.debt = debt;
                this.penaltyForLatePayment = 0.0f;
                this.outstandingDebt = Math.max(debt - payments, 0.0f);
                this.overdueDebt = 0.0f;
                this.moroseDebt = 0.0f;
                this.pendingDebts = 0;
                this.overdueDebts = 0;
                this.moroseDebts = 0;
                this.debtStatus = DebtStatus.PENDIENTE;
        }

        public void addPayment(float amount) {
                this.payments += amount;
        }
}

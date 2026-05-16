package com.tpgdb.Consorcio.Dto.Balance;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class PartnerBalance {
        private Long partnerId;
        private float payments;
        private float debt;
        private float penaltyForLatePayment;

        public PartnerBalance(Long partnerId, float payments, float debt) {
                this.partnerId = partnerId;
                this.payments = payments;
                this.debt = debt;
                this.penaltyForLatePayment = 0.0f;
                
                if (payments < debt) {
                        penaltyForLatePayment = (debt-payments)*1.05f;
                }
        }

        public void addPayment(float amount) {
                this.payments += amount;
        }
}
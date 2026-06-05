package com.tpgdb.Consorcio.Dto.Debt;

import com.tpgdb.Consorcio.Model.DebtStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class DebtResponseDto {
    private Long id;
    private String description;
    private float amount;
    private LocalDate dueDate;
    private DebtStatus status;
    private long daysOverdue;
    private long daysInMorosity;
    /** Interés acumulado por mora calculado dinámicamente (0 si no está en mora). */
    private float interestAccrued;
    /** Monto total a pagar: amount + interestAccrued */
    private float totalOwed;
    private Long expenseId;
}

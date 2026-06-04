package com.tpgdb.Consorcio.Dto.Report;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class MonthlySummaryDto {
    private String period;

    private Float totalExpenses;

    private Float totalPayments;

    private Float pendingDebt;

    private Integer totalPartners;

    private Integer partnersInDebt;

    private Float balance;

}
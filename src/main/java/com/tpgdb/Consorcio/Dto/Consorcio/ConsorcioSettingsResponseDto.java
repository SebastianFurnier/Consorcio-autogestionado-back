package com.tpgdb.Consorcio.Dto.Consorcio;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ConsorcioSettingsResponseDto {
    private Long consorcioId;
    private float monthlyInterestRate;
    private int gracePeriodDays;
    private float fixedPenalty;
}

package com.tpgdb.Consorcio.Dto.Debt;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class DebtResponseDto {
    private Long id;
    private String description;
    private float amount;
}

package com.tpgdb.Consorcio.Dto.Expense;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ExpenseResponseDto {
    private Long id;
    private LocalDate date;
    private String description;
    private String category;
    private Long partnerId;
    private float amount;
    private boolean approved;
}

package com.tpgdb.Consorcio.Dto;
import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class ExpenseRequestDto {
        private Long id;
        @NotNull(message = "El monto es obligatorio.")
        @Positive(message = "El monto debe ser mayor a cero.")
        private float amount;

        @NotBlank(message = "La descripción no debe estar vacia.")
        private String description;


        @NotNull(message = "La fecha es obligatoria.")
        private LocalDate date;
        
        private Long consorcioId;
}

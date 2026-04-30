package com.tpgdb.Consorcio.Dto.Consorcio;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ConsorcioRequestDto {
    @NotBlank(message = "El nombre del consorcio es obligatorio")
    private String nombre;

    @NotNull(message = "La cantidad máxima de partners es obligatoria")
    @Min(value = 1, message = "La cantidad máxima de partners debe ser mayor a 0")
    private Integer maxPartners;
}

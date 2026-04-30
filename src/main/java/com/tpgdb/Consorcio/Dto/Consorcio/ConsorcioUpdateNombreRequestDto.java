package com.tpgdb.Consorcio.Dto.Consorcio;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ConsorcioUpdateNombreRequestDto {
    @NotBlank(message = "El nombre del consorcio no puede estar vacío o contener solo espacios")
    private String nombre;
}

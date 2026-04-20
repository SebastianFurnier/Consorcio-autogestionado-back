package com.tpgdb.Consorcio.Dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class PartnerRequestDto {
    private Long id;
    @NotBlank(message = "El nombre no debe estar vacio.")
    private String name;
    @NotBlank(message = "El numero de departamento no debe estar vacio.")
    private String apartment;
    @NotNull(message = "La participacion es obligatoria.")
    @Positive(message = "La participacion debe ser mayor a cero.")
    private float participation;
    @Email(message = "El email no es válido")
    @NotBlank(message = "El email es obligatorio")
    private String email;
    @NotBlank(message = "El teléfono es obligatorio")
    private String phone;
}

package com.tpgdb.Consorcio.Dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class PartnerRequestDto {

    private Long id;

    @NotNull(message = "El userId es obligatorio")
    private Long userId;

    @NotNull(message = "El consorcioId es obligatorio")
    private Long consorcioId;

    private String apartment;
    private float participation;

    @NotBlank(message = "El role es obligatorio")
    private String role;
}
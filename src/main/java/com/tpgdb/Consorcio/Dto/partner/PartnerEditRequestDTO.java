package com.tpgdb.Consorcio.Dto.partner;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class PartnerEditRequestDTO {
    private Long id;
    @NotBlank
    private String apartment;
    @Positive
    private float participation;
    @NotBlank(message = "El role es obligatorio")
    private String role;
}

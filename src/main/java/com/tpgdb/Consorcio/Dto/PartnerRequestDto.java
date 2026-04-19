package com.tpgdb.Consorcio.Dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class PartnerRequestDto {
    private Long id;
    @NotBlank
    private String name;
    @NotBlank
    private String apartment;
    @Positive
    private float participation;
    @Email
    private String email;
    @NotBlank
    private String phone;
}

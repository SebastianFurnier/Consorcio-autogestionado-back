package com.tpgdb.Consorcio.Dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class PartnerRequestDto {
    private Long id;
    private String name;
    private String apartment;
    private float participation;
    private String email;
    private String phone;
}

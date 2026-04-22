package com.tpgdb.Consorcio.Dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class PartnerResponseDto {

    private Long id;
    private Long userId;
    private Long consorcioId;
    private String name;
    private String email;
    private String apartment;
    private float participation;
    private String role;
}

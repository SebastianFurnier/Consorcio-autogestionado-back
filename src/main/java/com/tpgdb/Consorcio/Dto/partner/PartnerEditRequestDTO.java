package com.tpgdb.Consorcio.Dto.partner;

import com.tpgdb.Consorcio.Model.Partner;
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
    private Partner.PartnerRole role;
}

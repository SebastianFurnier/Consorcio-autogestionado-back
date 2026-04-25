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
public class UnirseConsorcioRequestDto {
    @NotBlank(message = "El código de invitación es obligatorio")
    private String codigoInvitacion;
}

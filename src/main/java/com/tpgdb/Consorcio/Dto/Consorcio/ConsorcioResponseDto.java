package com.tpgdb.Consorcio.Dto.Consorcio;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ConsorcioResponseDto {
    private Long id;
    private String nombre;
    private String codigoInvitacion;
    private String creadoPor;
    private LocalDateTime fechaCreacion;
    private String rol; // ADMIN o MEMBER
    private long cantidadMiembros;
}

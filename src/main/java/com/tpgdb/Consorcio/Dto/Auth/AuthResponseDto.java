package com.tpgdb.Consorcio.Dto.Auth;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class AuthResponseDto {
    private String token;
    private Long userId;
    private String email;
    private String nombre;
}

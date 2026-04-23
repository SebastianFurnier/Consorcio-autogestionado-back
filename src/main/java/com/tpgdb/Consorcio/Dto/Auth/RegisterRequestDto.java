package com.tpgdb.Consorcio.Dto.Auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class RegisterRequestDto {
    @NotBlank(message = "El nombre es obligatorio")
    @Pattern(regexp = "^[a-zA-Z\\s]+$", message = "El nombre solo puede contener letras y espacios")
    private String nombre;

    @NotBlank(message = "El teléfono es obligatorio")
    @Pattern(regexp = "^\\+\\d{1,3}\\s?\\d{1,4}\\s?\\d{1,4}\\s?\\d{1,4}$", message = "El teléfono debe tener un formato internacional válido (ej: +54 9 11 1234 5678)")
    private String telefono;

    @Email(message = "El email no es válido")
    @NotBlank(message = "El email es obligatorio")
    private String email;

    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres")
    @Pattern(regexp = "^[A-Z].*", message = "La contraseña debe comenzar con una mayúscula")
    @Pattern(regexp = ".*\\d.*", message = "La contraseña debe contener al menos un número")
    @Pattern(regexp = ".*[!@#$%^&*(),.?\":{}|<>].*", message = "La contraseña debe contener al menos un símbolo especial")
    private String password;
}

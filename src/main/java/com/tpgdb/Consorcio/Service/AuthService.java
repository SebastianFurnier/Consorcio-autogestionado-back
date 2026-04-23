package com.tpgdb.Consorcio.Service;

import com.tpgdb.Consorcio.Dto.Auth.LoginRequestDto;
import com.tpgdb.Consorcio.Dto.Auth.RegisterRequestDto;
import com.tpgdb.Consorcio.Dto.Auth.AuthResponseDto;
import com.tpgdb.Consorcio.Exception.InvalidCredentialsException;
import com.tpgdb.Consorcio.Exception.UserAlreadyExistsException;
import com.tpgdb.Consorcio.Model.User;
import com.tpgdb.Consorcio.Repository.UserRepository;
import com.tpgdb.Consorcio.Security.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

    public AuthResponseDto register(RegisterRequestDto registerDto) {
        // Validar que el email no exista
        if (userRepository.existsByEmail(registerDto.getEmail())) {
            throw new UserAlreadyExistsException("El email ya está registrado");
        }

        User user = new User(
                registerDto.getEmail(),
                passwordEncoder.encode(registerDto.getPassword()),
                registerDto.getNombre(),
                registerDto.getTelefono());

        User savedUser = userRepository.save(user);

        String token = jwtProvider.generateToken(savedUser.getId(), savedUser.getEmail());

        return new AuthResponseDto(
                token,
                savedUser.getId(),
                savedUser.getEmail(),
                savedUser.getNombre());
    }

    public AuthResponseDto login(LoginRequestDto loginDto) {
        User user = userRepository.findByEmail(loginDto.getEmail())
                .orElseThrow(() -> new InvalidCredentialsException("Credenciales inválidas"));

        if (!passwordEncoder.matches(loginDto.getPassword(), user.getPassword())) {
            throw new InvalidCredentialsException("Credenciales inválidas");
        }
        String token = jwtProvider.generateToken(user.getId(), user.getEmail());

        return new AuthResponseDto(
                token,
                user.getId(),
                user.getEmail(),
                user.getNombre());
    }
}

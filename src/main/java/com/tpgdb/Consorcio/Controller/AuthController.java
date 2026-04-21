package com.tpgdb.Consorcio.Controller;

import com.tpgdb.Consorcio.Dto.Auth.LoginRequestDto;
import com.tpgdb.Consorcio.Dto.Auth.RegisterRequestDto;
import com.tpgdb.Consorcio.Dto.Auth.AuthResponseDto;
import com.tpgdb.Consorcio.Service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
@CrossOrigin("*")
public class AuthController {
    
    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponseDto> register(@Valid @RequestBody RegisterRequestDto registerDto) {
        AuthResponseDto response = authService.register(registerDto);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> login(@Valid @RequestBody LoginRequestDto loginDto) {
        AuthResponseDto response = authService.login(loginDto);
        return ResponseEntity.ok(response);
    }
}

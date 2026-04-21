package com.tpgdb.Consorcio.Controller;

import com.tpgdb.Consorcio.Dto.Consorcio.ConsorcioRequestDto;
import com.tpgdb.Consorcio.Dto.Consorcio.ConsorcioResponseDto;
import com.tpgdb.Consorcio.Dto.Consorcio.UnirseConsorcioRequestDto;
import com.tpgdb.Consorcio.Service.ConsorcioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/consorcios")
@CrossOrigin("*")
public class ConsorcioController {
    
    private final ConsorcioService consortioService;

    @PostMapping
    public ResponseEntity<ConsorcioResponseDto> crearConsorcio(
            @Valid @RequestBody ConsorcioRequestDto requestDto,
            Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        ConsorcioResponseDto response = consortioService.crearConsorcio(requestDto, userId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/unirse")
    public ResponseEntity<ConsorcioResponseDto> unirseConsorcio(
            @Valid @RequestBody UnirseConsorcioRequestDto requestDto,
            Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        ConsorcioResponseDto response = consortioService.unirseConsorcio(requestDto, userId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/mios")
    public ResponseEntity<Map<String, List<ConsorcioResponseDto>>> getMisConsorcios(
            Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        List<ConsorcioResponseDto> consorcios = consortioService.obtenerMisConsorcios(userId);
        return ResponseEntity.ok(Map.of("consorcios", consorcios));
    }
}

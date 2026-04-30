package com.tpgdb.Consorcio.Controller;

import com.tpgdb.Consorcio.Dto.Consorcio.ConsorcioRequestDto;
import com.tpgdb.Consorcio.Dto.Consorcio.ConsorcioResponseDto;
import com.tpgdb.Consorcio.Dto.Consorcio.UnirseConsorcioRequestDto;
import com.tpgdb.Consorcio.Dto.Consorcio.ConsorcioUpdateNombreRequestDto;
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

    @GetMapping("/{id}")
    public ResponseEntity<ConsorcioResponseDto> obtenerConsorcio(
            @PathVariable Long id,
            Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        ConsorcioResponseDto consorcio = consortioService.obtenerConsorcioById(id, userId);
        return ResponseEntity.ok(consorcio);
    }

    @PutMapping("/{id}/nombre")
    public ResponseEntity<Map<String, Object>> actualizarNombre(
            @PathVariable Long id,
            @Valid @RequestBody ConsorcioUpdateNombreRequestDto requestDto,
            Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        ConsorcioResponseDto consorcio = consortioService.actualizarNombreConsorcio(id, requestDto.getNombre(), userId);
        return ResponseEntity.ok(Map.of(
                "message", "Nombre del consorcio actualizado exitosamente",
                "consorcio", consorcio));
    }
}

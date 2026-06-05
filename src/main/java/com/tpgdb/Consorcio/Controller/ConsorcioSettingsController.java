package com.tpgdb.Consorcio.Controller;

import com.tpgdb.Consorcio.Dto.Consorcio.ConsorcioSettingsRequestDto;
import com.tpgdb.Consorcio.Dto.Consorcio.ConsorcioSettingsResponseDto;
import com.tpgdb.Consorcio.Model.ConsorcioSettings;
import com.tpgdb.Consorcio.Service.LateFeeCalculatorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/consorcios/{consorcioId}/settings")
public class ConsorcioSettingsController {

    private final LateFeeCalculatorService lateFeeCalculatorService;

    @GetMapping
    public ResponseEntity<ConsorcioSettingsResponseDto> getSettings(@PathVariable Long consorcioId) {
        ConsorcioSettings settings = lateFeeCalculatorService.getOrDefaultSettings(consorcioId);
        ConsorcioSettingsResponseDto response = new ConsorcioSettingsResponseDto(
                settings.getConsorcioId(),
                settings.getMonthlyInterestRate(),
                settings.getGracePeriodDays(),
                settings.getFixedPenalty()
        );
        return ResponseEntity.ok(response);
    }

    @PutMapping
    public ResponseEntity<ConsorcioSettingsResponseDto> updateSettings(
            @PathVariable Long consorcioId,
            @Valid @RequestBody ConsorcioSettingsRequestDto requestDto) {
        ConsorcioSettings settings = lateFeeCalculatorService.saveOrUpdateSettings(consorcioId, requestDto);
        ConsorcioSettingsResponseDto response = new ConsorcioSettingsResponseDto(
                settings.getConsorcioId(),
                settings.getMonthlyInterestRate(),
                settings.getGracePeriodDays(),
                settings.getFixedPenalty()
        );
        return ResponseEntity.ok(response);
    }
}

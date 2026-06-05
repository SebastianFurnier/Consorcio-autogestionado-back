package com.tpgdb.Consorcio.Controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.tpgdb.Consorcio.Dto.Balance.BalanceResponseDto;
import com.tpgdb.Consorcio.Service.BalanceService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/balance")
public class BalanceController {

        private final BalanceService service;

        @GetMapping()
        public ResponseEntity<BalanceResponseDto> getBalance(@RequestParam Long consorcioId,
                                                                                                                  @RequestParam(name = "period", required = false) String period) {
                                return ResponseEntity.ok(service.getBalanceOfConsorcio(consorcioId, period));
        }
}

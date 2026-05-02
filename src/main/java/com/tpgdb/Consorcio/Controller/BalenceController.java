package com.tpgdb.Consorcio.Controller;

import java.util.List;

import com.tpgdb.Consorcio.Dto.Expense.ExpenseResponseDto;
import com.tpgdb.Consorcio.Dto.payment.PaymentResponseDto;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.tpgdb.Consorcio.Dto.Balance.BalanceResponseDto;
import com.tpgdb.Consorcio.Dto.Expense.ExpenseRequestDto;
import com.tpgdb.Consorcio.Service.BalanceService;
import com.tpgdb.Consorcio.Service.ExpenseService;
import com.tpgdb.Consorcio.Service.PaymentService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/balance")
public class BalenceController {

        private final BalanceService service;

        @GetMapping()
        public ResponseEntity<BalanceResponseDto> getBalance(@RequestParam Long consorcioId) {
                return ResponseEntity.ok(service.getBalanceOfConsorcio(consorcioId));
        }
}

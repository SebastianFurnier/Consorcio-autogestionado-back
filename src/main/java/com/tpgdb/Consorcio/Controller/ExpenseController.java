package com.tpgdb.Consorcio.Controller;

import java.util.List;
import java.util.Map;
import java.time.LocalDate;

import com.tpgdb.Consorcio.Dto.Expense.ExpenseResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.tpgdb.Consorcio.Dto.Expense.ExpenseRequestDto;
import com.tpgdb.Consorcio.Service.ExpenseService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/expense")
public class ExpenseController {

    private final ExpenseService service;

    @PostMapping("/save")
    public ResponseEntity<?> createExpense(@Valid @RequestBody ExpenseRequestDto dto) {

        service.createExpense(dto);

        return ResponseEntity.ok().build();
    }

    /**
     * Obtiene los gastos filtrados por consorcio.
     * * @param consorcioId ID que viene del frontend como ?consorcioId=...
     */
    @GetMapping("/all")
    public ResponseEntity<Map<String, List<ExpenseResponseDto>>> getAll(@RequestParam Long consorcioId) {
        List<ExpenseResponseDto> allExpenses = service.getAllExpensesOfConsortium(consorcioId);
        return ResponseEntity.ok(Map.of("response", allExpenses));
    }

    @GetMapping("/approved")
    public ResponseEntity<Map<String, List<ExpenseResponseDto>>> getApproved(@RequestParam Long consorcioId,
                                                                             Authentication authentication) {
        List<ExpenseResponseDto> approvedExpenses = service.getApprovedExpensesOfConsortium(consorcioId);
        return ResponseEntity.ok(Map.of("response", approvedExpenses));
    }

    @GetMapping("/period")
    public ResponseEntity<Map<String, List<ExpenseResponseDto>>> getExpensesByDateBetween(@RequestParam Long consorcioId, @RequestParam String paymentDate) {
        LocalDate date = LocalDate.parse(paymentDate);
        LocalDate startDate = date.withDayOfMonth(1);
        LocalDate endDate = date.withDayOfMonth(date.lengthOfMonth());
        List<ExpenseResponseDto> expenses = service.getExpensesOfConsortiumAndDateBetween(consorcioId, startDate, endDate);
        return ResponseEntity.ok(Map.of("response", expenses));
    }

}
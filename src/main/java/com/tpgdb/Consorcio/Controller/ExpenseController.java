package com.tpgdb.Consorcio.Controller;

import java.util.List;
import java.util.Map;

import com.tpgdb.Consorcio.Dto.Expense.ExpenseResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.tpgdb.Consorcio.Dto.Expense.ExpenseRequestDto;
import com.tpgdb.Consorcio.Service.ExpenseService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/expense")
@CrossOrigin("*")
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
    public ResponseEntity<Map<String, List<ExpenseResponseDto>>> getApproved(@RequestParam Long consorcioId) {
        List<ExpenseResponseDto> approvedExpenses = service.getApprovedExpensesOfConsortium(consorcioId);
        return ResponseEntity.ok(Map.of("response", approvedExpenses));
    }

}
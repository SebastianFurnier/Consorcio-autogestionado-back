package com.tpgdb.Consorcio.Controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tpgdb.Consorcio.Dto.ExpenseRequestDto;
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

    @GetMapping("/all")
    public ResponseEntity<Map<String, List<ExpenseRequestDto>>> getAll() {
        List<ExpenseRequestDto> allExpenses = service.getAllExpenses();
        return ResponseEntity.ok(Map.of("response", allExpenses));
    }

}
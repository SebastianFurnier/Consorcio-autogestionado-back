package com.tpgdb.Consorcio.Controller;

import com.tpgdb.Consorcio.Model.Expense;
import com.tpgdb.Consorcio.Model.Payment;
import com.tpgdb.Consorcio.Repository.ExpenseRepository;
import com.tpgdb.Consorcio.Repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.YearMonth;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/periods")
public class PeriodController {
    private final ExpenseRepository expenseRepository;
    private final PaymentRepository paymentRepository;

    @GetMapping
    public ResponseEntity<List<String>> getPeriods(@RequestParam(name = "consorcioId") Long consorcioId) {
        List<Expense> expenses = expenseRepository.findByConsorcioId(consorcioId);
        List<Payment> payments = paymentRepository.findByConsorcioId(consorcioId);

        Set<YearMonth> months = new HashSet<>();

        for (Expense e : expenses) {
            if (e.getDate() != null) months.add(YearMonth.from(e.getDate()));
        }
        for (Payment p : payments) {
            if (p.getPeriod() != null) months.add(YearMonth.from(p.getPeriod()));
        }

        List<String> sorted = months.stream()
                .sorted()
                .map(ym -> ym.atDay(1).toString())
                .collect(Collectors.toList());

        return ResponseEntity.ok(sorted);
    }
}

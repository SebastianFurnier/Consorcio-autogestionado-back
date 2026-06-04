package com.tpgdb.Consorcio.Model;

import java.time.LocalDate;
import java.time.YearMonth;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Debt {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "partner_id")
    private Partner partner;
    @ManyToOne
    @JoinColumn(name = "consorcio_id")
    private Consorcio consorcio;
    boolean paid;
    @ManyToOne
    @JoinColumn(name = "expense_id")
    private Expense expense;
    private float amount;
    private LocalDate dueDate;

    public Debt (Partner partner, Consorcio consorcio, Expense expense, float amount) {
        this.partner = partner;
        this.consorcio = consorcio;
        this.expense = expense;
        this.amount = amount;
        this.dueDate = calculateDueDate(expense);
        paid = false;

    }

    private LocalDate calculateDueDate(Expense expense) {
        if (expense == null || expense.getDate() == null) {
            return null;
        }
        return YearMonth.from(expense.getDate()).atEndOfMonth();
    }
}

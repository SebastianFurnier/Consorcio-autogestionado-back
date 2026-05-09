package com.tpgdb.Consorcio.Model;

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

    public Debt (Partner partner, Consorcio consorcio, Expense expense, float amount) {
        this.partner = partner;
        this.consorcio = consorcio;
        this.expense = expense;
        this.amount = amount;
        paid = false;

    }
}

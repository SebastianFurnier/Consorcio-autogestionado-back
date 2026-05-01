package com.tpgdb.Consorcio.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDate;
import jakarta.validation.constraints.Positive;

@Entity
@Table(name = "payments")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "partner_id", nullable = false)
    private Partner partner;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "expense_id", nullable = false)
    private Expense expense;

    @Column(nullable = false)
    private LocalDate paymentDate;

    @Column(nullable = false)
    private LocalDate period;

    @Column(nullable = false)
    @Positive(message = "El monto debe ser positivo")
    private float amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentMethod paymentMethod = PaymentMethod.CASH;
    
    @Column(length = 255)
    private String description;

    public Payment(Partner partner, LocalDate paymentDate, LocalDate period, float amount, PaymentMethod paymentMethod) {
        this.partner = partner;
        this.paymentDate = paymentDate;
        this.period = period;   // Primer día del mes
        this.amount = amount;
        this.paymentMethod = paymentMethod;
        this.description = null;
    }

    public enum PaymentMethod {
        CASH,
        CREDIT_CARD,
        DEBIT_CARD,
        TRANSFER,
        OTHER
    }
}
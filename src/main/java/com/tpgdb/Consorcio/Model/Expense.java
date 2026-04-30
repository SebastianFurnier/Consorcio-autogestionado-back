package com.tpgdb.Consorcio.Model;

import java.time.LocalDate;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor
@Getter
@Setter
public class Expense {
        @Id
        @GeneratedValue(strategy = GenerationType.AUTO)
        private Long id;
        private float amount;
        private String description;
        private LocalDate date;
        @ManyToOne
        @JoinColumn(name = "partner_id")
        private Partner partner;
        @ManyToOne
        @JoinColumn(name = "consorcio_id")
        private Consorcio consorcio;
        private String category;
        private boolean approved;

        public Expense(float amount, String description, LocalDate date,
                        Partner partner, Consorcio consorcio, String category) {
                this.amount = amount;
                this.description = description;
                this.date = date;
                this.consorcio = consorcio;
                this.partner = partner;
                this.category = category;
                this.approved = true;
        }
}
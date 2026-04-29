package com.tpgdb.Consorcio.Model;

import java.time.LocalDate;

import jakarta.persistence.*;
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
        @OneToOne
        @JoinColumn(name = "partner_id")
        private Partner partner;
        
        @ManyToOne
        @JoinColumn(name = "consorcio_id")
        private Consorcio consorcio;

        public Expense(float amount, String description, LocalDate date, Consorcio consorcio, Partner partner) {
                this.amount = amount;
                this.description = description;
                this.date = date;
                this.consorcio = consorcio;
                this.partner = partner;
        }
}

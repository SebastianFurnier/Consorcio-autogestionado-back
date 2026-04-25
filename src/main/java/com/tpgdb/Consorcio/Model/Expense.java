package com.tpgdb.Consorcio.Model;

import java.time.LocalDate;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

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
        @JoinColumn(name = "consorcio_id")
        private Consorcio consorcio;

        public Expense(float amount, String description, LocalDate date, Consorcio consorcio) {
                this.amount = amount;
                this.description = description;
                this.date = date;
                this.consorcio = consorcio;
        }
}

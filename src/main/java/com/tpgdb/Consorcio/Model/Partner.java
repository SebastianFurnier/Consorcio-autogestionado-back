package com.tpgdb.Consorcio.Model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor
@Getter
@Setter
public class Partner {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String name;
    private String apartment;
    private float participation;
    private String email;
    private String phone;
    private boolean active;

    public Partner(String name, String apartment, float participation, String email, String phone) {
        this.name = name;
        this.apartment = apartment;
        this.participation = participation;
        this.email = email;
        this.phone = phone;
        active = true;
    }
}

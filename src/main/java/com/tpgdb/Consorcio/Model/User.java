package com.tpgdb.Consorcio.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "users")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    private String nombre;
    private String telefono;

    @Column(nullable = false)
    private boolean active = true;

    public User(String email, String password, String nombre, String telefono) {
        this.email = email;
        this.password = password;
        this.nombre = nombre;
        this.telefono = telefono;
        this.active = true;
    }
}

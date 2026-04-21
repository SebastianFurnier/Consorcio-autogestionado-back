package com.tpgdb.Consorcio.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Table(name = "consorcios")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Consorcio {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Column(unique = true, nullable = false, length = 10)
    private String codigoInvitacion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creado_por_id", nullable = false)
    private User creadoPor;

    @Column(nullable = false)
    private LocalDateTime fechaCreacion = LocalDateTime.now();

    @Column(nullable = false)
    private boolean active = true;

    public Consorcio(String nombre, String codigoInvitacion, User creadoPor) {
        this.nombre = nombre;
        this.codigoInvitacion = codigoInvitacion;
        this.creadoPor = creadoPor;
        this.fechaCreacion = LocalDateTime.now();
        this.active = true;
    }
}

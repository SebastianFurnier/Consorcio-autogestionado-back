package com.tpgdb.Consorcio.Model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "consorcio_settings")
@Getter
@Setter
@NoArgsConstructor
public class ConsorcioSettings {

    @Id
    private Long consorcioId;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "consorcio_id")
    private Consorcio consorcio;

    /**
     * Porcentaje de interés mensual por mora. Ejemplo: 3.0 = 3% mensual.
     * Se aplica prorrateado por día si la deuda está en estado EN_MORA.
     */
    @Column(nullable = false)
    private float monthlyInterestRate = 3.0f;

    /**
     * Días de gracia después del vencimiento antes de aplicar recargo.
     * 0 = mora comienza el día siguiente al vencimiento.
     */
    @Column(nullable = false)
    private int gracePeriodDays = 0;

    /**
     * Recargo fijo en moneda que se aplica al entrar en mora (adicional al interés proporcional).
     * 0 = sin recargo fijo.
     */
    @Column(nullable = false)
    private float fixedPenalty = 0.0f;

    public ConsorcioSettings(Consorcio consorcio) {
        this.consorcio = consorcio;
        this.monthlyInterestRate = 3.0f;
        this.gracePeriodDays = 0;
        this.fixedPenalty = 0.0f;
    }
}

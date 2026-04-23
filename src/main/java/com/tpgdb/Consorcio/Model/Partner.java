package com.tpgdb.Consorcio.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "partners")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Partner {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "consorcio_id", nullable = false)
    private Consorcio consorcio;

    private String apartment;
    private float participation = 0;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PartnerRole role = PartnerRole.MEMBER;

    @Column(nullable = false)
    private boolean active = true;

    public Partner(User user, Consorcio consorcio, PartnerRole role) {
        this.user = user;
        this.consorcio = consorcio;
        this.role = role;
        this.participation = 0;
        this.active = true;
    }

    public enum PartnerRole {
        ADMIN,
        MEMBER
    }
}

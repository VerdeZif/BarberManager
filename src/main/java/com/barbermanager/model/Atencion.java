package com.barbermanager.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "atenciones")
public class Atencion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "cita_id", nullable = false, unique = true)
    private Cita cita;

    @Column(name = "inicio_real", nullable = false)
    private LocalDateTime inicioReal;

    @Column(name = "fin_real")
    private LocalDateTime finReal;

    @Column(columnDefinition = "TEXT")
    private String observaciones;

    public Atencion() { }

    public Long getId() { return id; }
    public Cita getCita() { return cita; }
    public void setCita(Cita cita) { this.cita = cita; }
    public LocalDateTime getInicioReal() { return inicioReal; }
    public void setInicioReal(LocalDateTime inicioReal) { this.inicioReal = inicioReal; }
    public LocalDateTime getFinReal() { return finReal; }
    public void setFinReal(LocalDateTime finReal) { this.finReal = finReal; }
    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }
}

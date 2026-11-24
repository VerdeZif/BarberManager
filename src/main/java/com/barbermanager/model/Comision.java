package com.barbermanager.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "comisiones")
public class Comision {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "barbero_id", nullable = false)
    private Barbero barbero;

    @OneToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "atencion_id", nullable = false, unique = true)
    private Atencion atencion;

    @Column(name = "base_servicio", nullable = false, precision = 10, scale = 2)
    private BigDecimal baseServicio;

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal porcentaje;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal monto;

    @Column(nullable = false, length = 20)
    private String estado;

    @Column(name = "fecha_generacion", insertable = false, updatable = false)
    private LocalDateTime fechaGeneracion;

    @Column(name = "fecha_pago")
    private LocalDateTime fechaPago;

    public Comision() { }

    public Long getId() { return id; }
    public Barbero getBarbero() { return barbero; }
    public void setBarbero(Barbero barbero) { this.barbero = barbero; }
    public Atencion getAtencion() { return atencion; }
    public void setAtencion(Atencion atencion) { this.atencion = atencion; }
    public BigDecimal getBaseServicio() { return baseServicio; }
    public void setBaseServicio(BigDecimal baseServicio) { this.baseServicio = baseServicio; }
    public BigDecimal getPorcentaje() { return porcentaje; }
    public void setPorcentaje(BigDecimal porcentaje) { this.porcentaje = porcentaje; }
    public BigDecimal getMonto() { return monto; }
    public void setMonto(BigDecimal monto) { this.monto = monto; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public LocalDateTime getFechaGeneracion() { return fechaGeneracion; }
    public LocalDateTime getFechaPago() { return fechaPago; }
    public void setFechaPago(LocalDateTime fechaPago) { this.fechaPago = fechaPago; }
}

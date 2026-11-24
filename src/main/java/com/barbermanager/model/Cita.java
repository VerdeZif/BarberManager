package com.barbermanager.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "citas")
public class Cita {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "barbero_id", nullable = false)
    private Barbero barbero;

    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "servicio_id", nullable = false)
    private Servicio servicio;

    @Column(nullable = false)
    private LocalDateTime inicio;

    @Column(nullable = false)
    private LocalDateTime fin;

    @Column(name = "precio_reservado", nullable = false, precision = 10, scale = 2)
    private BigDecimal precioReservado;

    @Column(nullable = false, length = 20)
    private String estado;

    @Column(name = "creada_en", insertable = false, updatable = false)
    private LocalDateTime creadaEn;

    public Cita() { }

    public Long getId() { return id; }
    public Cliente getCliente() { return cliente; }
    public void setCliente(Cliente cliente) { this.cliente = cliente; }
    public Barbero getBarbero() { return barbero; }
    public void setBarbero(Barbero barbero) { this.barbero = barbero; }
    public Servicio getServicio() { return servicio; }
    public void setServicio(Servicio servicio) { this.servicio = servicio; }
    public LocalDateTime getInicio() { return inicio; }
    public void setInicio(LocalDateTime inicio) { this.inicio = inicio; }
    public LocalDateTime getFin() { return fin; }
    public void setFin(LocalDateTime fin) { this.fin = fin; }
    public BigDecimal getPrecioReservado() { return precioReservado; }
    public void setPrecioReservado(BigDecimal precioReservado) { this.precioReservado = precioReservado; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public LocalDateTime getCreadaEn() { return creadaEn; }
}

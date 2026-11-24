package com.barbermanager.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "pagos")
public class Pago {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "venta_id", nullable = false)
    private Venta venta;

    @Column(nullable = false, length = 20)
    private String metodo;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal monto;

    @Column(nullable = false)
    private boolean simulado = true;

    @Column(insertable = false, updatable = false)
    private LocalDateTime fecha;

    public Pago() { }

    public Long getId() { return id; }
    public Venta getVenta() { return venta; }
    public void setVenta(Venta venta) { this.venta = venta; }
    public String getMetodo() { return metodo; }
    public void setMetodo(String metodo) { this.metodo = metodo; }
    public BigDecimal getMonto() { return monto; }
    public void setMonto(BigDecimal monto) { this.monto = monto; }
    public boolean isSimulado() { return simulado; }
    public void setSimulado(boolean simulado) { this.simulado = simulado; }
    public LocalDateTime getFecha() { return fecha; }
}

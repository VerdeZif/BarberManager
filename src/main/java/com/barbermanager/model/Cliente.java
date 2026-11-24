package com.barbermanager.model;

import jakarta.persistence.*;

@Entity
@Table(name = "clientes")
public class Cliente {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToOne @JoinColumn(name = "usuario_id", unique = true)
    private Usuario usuario;
    @Column(nullable = false, length = 100) private String nombres;
    @Column(nullable = false, length = 100) private String apellidos;
    @Column(length = 20) private String telefono;
    @Column(nullable = false) private boolean activo = true;

    protected Cliente() { }

    public Cliente(Usuario usuario, String nombres, String apellidos, String telefono) {
        this.usuario = usuario;
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.telefono = telefono;
    }
    public Long getId() { return id; }
    public Usuario getUsuario() { return usuario; }
    public String getNombres() { return nombres; }
    public String getApellidos() { return apellidos; }
    public String getTelefono() { return telefono; }
    public boolean isActivo() { return activo; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }
    public void setNombres(String nombres) { this.nombres = nombres; }
    public void setApellidos(String apellidos) { this.apellidos = apellidos; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    public void setActivo(boolean activo) { this.activo = activo; }
}

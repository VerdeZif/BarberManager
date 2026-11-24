package com.barbermanager.service.inventario;

import com.barbermanager.model.Producto;
import com.barbermanager.service.Transacciones;
import com.barbermanager.service.Validaciones;
import jakarta.persistence.EntityManagerFactory;
import java.math.BigDecimal;

public class ProductoService {
    private final Transacciones transacciones;
    public ProductoService(EntityManagerFactory factory) { transacciones = new Transacciones(factory); }
    public Long guardar(Long id, String nombre, String categoria, BigDecimal precio, int minimo, boolean activo) {
        String nombreValido = Validaciones.texto(nombre, 100, "Nombre");
        String categoriaValida = Validaciones.texto(categoria, 50, "Categoría");
        Validaciones.importe(precio, 10, "Precio");
        if (minimo < 0) throw new IllegalArgumentException("Stock mínimo inválido");
        return transacciones.ejecutar(em -> {
            Producto producto = id == null ? new Producto() : em.find(Producto.class, id);
            if (producto == null) throw new IllegalArgumentException("Producto no encontrado");
            producto.setNombre(nombreValido); producto.setCategoria(categoriaValida);
            producto.setPrecioVenta(precio); producto.setStockMinimo(minimo); producto.setActivo(activo);
            if (id == null) { producto.setStockActual(0); em.persist(producto); em.flush(); }
            return producto.getId();
        });
    }
}

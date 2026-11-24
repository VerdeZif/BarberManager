package com.barbermanager.service.inventario;

import com.barbermanager.model.MovimientoInventario;
import com.barbermanager.model.Producto;
import com.barbermanager.model.Usuario;
import com.barbermanager.repository.inventario.ProductoRepository;
import com.barbermanager.service.Transacciones;
import jakarta.persistence.EntityManagerFactory;
import java.util.Set;

public class InventarioService {
    private static final Set<String> TIPOS = Set.of("ENTRADA", "SALIDA", "AJUSTE_ENTRADA", "AJUSTE_SALIDA");
    private final Transacciones transacciones;
    public InventarioService(EntityManagerFactory factory) { transacciones = new Transacciones(factory); }

    public void registrarMovimiento(Long productoId, Long usuarioId, String tipo, int cantidad, String motivo) {
        if (tipo == null || !TIPOS.contains(tipo) || cantidad <= 0)
            throw new IllegalArgumentException("Tipo o cantidad de movimiento inválido");
        if (motivo != null && motivo.length() > 255) throw new IllegalArgumentException("Motivo demasiado largo");
        transacciones.ejecutar(em -> {
            Producto producto = new ProductoRepository(em).buscarPorIdConBloqueo(productoId)
                    .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado"));
            Usuario usuario = em.find(Usuario.class, usuarioId);
            if (usuario == null || !usuario.isActivo() || !producto.isActivo())
                throw new IllegalArgumentException("Usuario o producto inactivo");
            boolean entrada = tipo.equals("ENTRADA") || tipo.equals("AJUSTE_ENTRADA");
            long nuevoStock = (long) producto.getStockActual() + (entrada ? cantidad : -((long) cantidad));
            if (nuevoStock < 0 || nuevoStock > Integer.MAX_VALUE) throw new IllegalStateException("Stock insuficiente o fuera de rango");
            producto.setStockActual((int) nuevoStock);
            MovimientoInventario movimiento = new MovimientoInventario();
            movimiento.setProducto(producto);
            movimiento.setUsuario(usuario);
            movimiento.setTipo(tipo);
            movimiento.setCantidad(cantidad);
            movimiento.setMotivo(motivo);
            em.persist(movimiento);
            return null;
        });
    }
}

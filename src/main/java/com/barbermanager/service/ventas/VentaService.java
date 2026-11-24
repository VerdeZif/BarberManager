package com.barbermanager.service.ventas;

import com.barbermanager.model.*;
import com.barbermanager.repository.inventario.ProductoRepository;
import com.barbermanager.service.Transacciones;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.LockModeType;
import java.math.BigDecimal;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

public class VentaService {
    private final Transacciones transacciones;
    public VentaService(EntityManagerFactory factory) { transacciones = new Transacciones(factory); }

    /** Cantidades por producto; el orden de bloqueo evita bloqueos cruzados entre ventas. */
    public Long crear(Long usuarioId, Long clienteId, Long atencionId, Map<Long, Integer> productos) {
        Map<Long, Integer> cantidades = productos == null ? Map.of() : new TreeMap<>(productos);
        if (atencionId == null && cantidades.isEmpty()) throw new IllegalArgumentException("La venta no tiene productos");
        for (Map.Entry<Long, Integer> entrada : cantidades.entrySet())
            if (entrada.getKey() == null || entrada.getValue() == null || entrada.getValue() <= 0)
                throw new IllegalArgumentException("Producto o cantidad inválida");
        return transacciones.ejecutar(em -> {
            Usuario usuario = em.find(Usuario.class, usuarioId);
            if (usuario == null || !usuario.isActivo()) throw new IllegalArgumentException("Usuario inválido");
            Cliente cliente = clienteId == null ? null : em.find(Cliente.class, clienteId);
            if (clienteId != null && (cliente == null || !cliente.isActivo())) throw new IllegalArgumentException("Cliente inválido");
            Atencion atencion = atencionId == null ? null : em.find(Atencion.class, atencionId, LockModeType.PESSIMISTIC_WRITE);
            if (atencionId != null && (atencion == null || !"FINALIZADA".equals(atencion.getCita().getEstado())))
                throw new IllegalArgumentException("Atención no finalizada");
            if (atencion != null) {
                if (cliente != null && !cliente.getId().equals(atencion.getCita().getCliente().getId()))
                    throw new IllegalArgumentException("La atención pertenece a otro cliente");
                cliente = atencion.getCita().getCliente();
                Long existentes = em.createQuery("select count(v) from Venta v where v.atencion.id = :id", Long.class)
                        .setParameter("id", atencionId).getSingleResult();
                if (existentes > 0) throw new IllegalStateException("La atención ya tiene una venta");
            }
            Venta venta = new Venta();
            venta.setUsuario(usuario); venta.setCliente(cliente); venta.setAtencion(atencion);
            venta.setNumeroTicket("BM-" + UUID.randomUUID().toString().replace("-", "").substring(0, 26));
            venta.setEstadoPago("PENDIENTE");
            venta.setTotal(BigDecimal.ZERO);
            em.persist(venta);
            BigDecimal total = BigDecimal.ZERO;
            if (atencion != null) {
                Cita cita = atencion.getCita();
                DetalleVenta detalle = new DetalleVenta();
                detalle.setVenta(venta); detalle.setTipo("SERVICIO"); detalle.setServicio(cita.getServicio());
                detalle.setDescripcion(cita.getServicio().getNombre()); detalle.setCantidad(1);
                detalle.setPrecioUnitario(cita.getPrecioReservado()); detalle.setSubtotal(cita.getPrecioReservado());
                em.persist(detalle); total = total.add(detalle.getSubtotal());
            }
            for (Map.Entry<Long, Integer> entrada : cantidades.entrySet()) {
                Producto producto = new ProductoRepository(em).buscarPorIdConBloqueo(entrada.getKey())
                        .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado"));
                int cantidad = entrada.getValue();
                if (!producto.isActivo() || producto.getStockActual() < cantidad) throw new IllegalStateException("Stock insuficiente");
                producto.setStockActual(producto.getStockActual() - cantidad);
                DetalleVenta detalle = new DetalleVenta();
                detalle.setVenta(venta); detalle.setTipo("PRODUCTO"); detalle.setProducto(producto);
                detalle.setDescripcion(producto.getNombre()); detalle.setCantidad(cantidad);
                detalle.setPrecioUnitario(producto.getPrecioVenta());
                detalle.setSubtotal(producto.getPrecioVenta().multiply(BigDecimal.valueOf(cantidad)));
                em.persist(detalle); total = total.add(detalle.getSubtotal());
                MovimientoInventario movimiento = new MovimientoInventario();
                movimiento.setProducto(producto); movimiento.setUsuario(usuario); movimiento.setTipo("SALIDA");
                movimiento.setCantidad(cantidad); movimiento.setMotivo("Venta " + venta.getNumeroTicket());
                em.persist(movimiento);
            }
            if (total.signum() <= 0 || total.compareTo(new BigDecimal("99999999.99")) > 0)
                throw new IllegalArgumentException("El total debe ser positivo y caber en DECIMAL(10,2)");
            venta.setTotal(total);
            em.flush();
            return venta.getId();
        });
    }
}

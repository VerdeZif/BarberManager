package com.barbermanager.service.ventas;

import com.barbermanager.model.DetalleVenta;
import com.barbermanager.model.Pago;
import com.barbermanager.model.Venta;
import com.barbermanager.repository.ventas.DetalleVentaRepository;
import com.barbermanager.repository.ventas.PagoRepository;
import com.barbermanager.service.Transacciones;
import jakarta.persistence.EntityManagerFactory;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class TicketService {
    public record Item(String descripcion, int cantidad, BigDecimal precio, BigDecimal subtotal) { }
    public record PagoSimulado(String metodo, BigDecimal monto) { }
    public record Ticket(String numero, LocalDateTime fecha, BigDecimal total, List<Item> items,
                         List<PagoSimulado> pagos) { }
    private final Transacciones transacciones;
    public TicketService(EntityManagerFactory factory) { transacciones = new Transacciones(factory); }

    /** clienteId obligatorio cuando el acceso corresponde a un cliente. */
    public Ticket obtener(Long ventaId, Long clienteId, boolean accesoInterno) {
        return transacciones.ejecutar(em -> {
            Venta venta = em.find(Venta.class, ventaId);
            if (venta == null || !"PAGADA".equals(venta.getEstadoPago()))
                throw new IllegalArgumentException("Ticket no disponible");
            if (!accesoInterno && (clienteId == null || venta.getCliente() == null
                    || !clienteId.equals(venta.getCliente().getId()))) throw new SecurityException("Acceso denegado");
            List<Item> items = new DetalleVentaRepository(em).listarPorVenta(ventaId).stream()
                    .map(d -> new Item(d.getDescripcion(), d.getCantidad(), d.getPrecioUnitario(), d.getSubtotal())).toList();
            List<PagoSimulado> pagos = new PagoRepository(em).listarPorVenta(ventaId).stream()
                    .map(p -> new PagoSimulado(p.getMetodo(), p.getMonto())).toList();
            return new Ticket(venta.getNumeroTicket(), venta.getFecha(), venta.getTotal(), items, pagos);
        });
    }
}

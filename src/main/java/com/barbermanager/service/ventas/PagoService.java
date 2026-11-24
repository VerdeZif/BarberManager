package com.barbermanager.service.ventas;

import com.barbermanager.model.*;
import com.barbermanager.repository.ventas.VentaRepository;
import com.barbermanager.service.Transacciones;
import jakarta.persistence.EntityManagerFactory;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Set;

public class PagoService {
    private static final Set<String> METODOS = Set.of("EFECTIVO", "YAPE", "PLIN", "TARJETA");
    private final Transacciones transacciones;
    public PagoService(EntityManagerFactory factory) { transacciones = new Transacciones(factory); }

    public void registrarPago(Long ventaId, String metodo, BigDecimal monto) {
        if (metodo == null || !METODOS.contains(metodo) || monto == null || monto.signum() <= 0)
            throw new IllegalArgumentException("Método o monto inválido");
        try { monto = monto.setScale(2, RoundingMode.UNNECESSARY); }
        catch (ArithmeticException e) { throw new IllegalArgumentException("El monto admite hasta dos decimales"); }
        final BigDecimal importe = monto;
        transacciones.ejecutar(em -> {
            Venta venta = new VentaRepository(em).buscarPorIdConBloqueo(ventaId)
                    .orElseThrow(() -> new IllegalArgumentException("Venta no encontrada"));
            if (!"PENDIENTE".equals(venta.getEstadoPago()) && !"PARCIAL".equals(venta.getEstadoPago()))
                throw new IllegalStateException("La venta no admite pagos");
            BigDecimal pagado = em.createQuery("select sum(p.monto) from Pago p where p.venta.id = :id", BigDecimal.class)
                    .setParameter("id", ventaId).getSingleResult();
            if (pagado == null) pagado = BigDecimal.ZERO;
            BigDecimal nuevoTotal = pagado.add(importe);
            if (nuevoTotal.compareTo(venta.getTotal()) > 0) throw new IllegalArgumentException("El pago supera el saldo");
            Pago pago = new Pago();
            pago.setVenta(venta);
            pago.setMetodo(metodo);
            pago.setMonto(importe);
            pago.setSimulado(true);
            em.persist(pago);
            boolean pagada = nuevoTotal.compareTo(venta.getTotal()) == 0;
            venta.setEstadoPago(pagada ? "PAGADA" : "PARCIAL");
            if (pagada && venta.getAtencion() != null) {
                Atencion atencion = venta.getAtencion();
                if (!"FINALIZADA".equals(atencion.getCita().getEstado())) throw new IllegalStateException("Atención no finalizada");
                DetalleVenta detalle = em.createQuery("select d from DetalleVenta d where d.venta.id = :id and d.tipo = 'SERVICIO'", DetalleVenta.class)
                        .setParameter("id", ventaId).getSingleResult();
                Barbero barbero = atencion.getCita().getBarbero();
                Comision comision = new Comision();
                comision.setAtencion(atencion);
                comision.setBarbero(barbero);
                comision.setBaseServicio(detalle.getSubtotal());
                comision.setPorcentaje(barbero.getPorcentajeComision());
                comision.setMonto(detalle.getSubtotal().multiply(barbero.getPorcentajeComision())
                        .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP));
                comision.setEstado("PENDIENTE");
                em.persist(comision);
            }
            return null;
        });
    }
}

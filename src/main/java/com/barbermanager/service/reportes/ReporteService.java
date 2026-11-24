package com.barbermanager.service.reportes;

import com.barbermanager.service.Transacciones;
import jakarta.persistence.EntityManagerFactory;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class ReporteService {
    public record ConteoEstado(String estado, long cantidad) { }
    public record ProductoBajo(Long id, String nombre, int stock, int minimo) { }
    private final Transacciones transacciones;
    public ReporteService(EntityManagerFactory factory) { transacciones = new Transacciones(factory); }

    public List<ConteoEstado> citasPorEstado(LocalDateTime desde, LocalDateTime hasta) {
        validarPeriodo(desde, hasta);
        return transacciones.ejecutar(em -> em.createQuery(
                "select c.estado, count(c) from Cita c where c.inicio >= :desde and c.inicio < :hasta group by c.estado", Object[].class)
                .setParameter("desde", desde).setParameter("hasta", hasta).getResultList().stream()
                .map(fila -> new ConteoEstado((String) fila[0], (Long) fila[1])).toList());
    }
    public BigDecimal ventasSimuladas(LocalDateTime desde, LocalDateTime hasta) {
        validarPeriodo(desde, hasta);
        return transacciones.ejecutar(em -> {
            BigDecimal total = em.createQuery("select sum(v.total) from Venta v where v.estadoPago = 'PAGADA' and v.fecha >= :desde and v.fecha < :hasta", BigDecimal.class)
                    .setParameter("desde", desde).setParameter("hasta", hasta).getSingleResult();
            return total == null ? BigDecimal.ZERO : total;
        });
    }
    public List<ProductoBajo> stockBajo() {
        return transacciones.ejecutar(em -> em.createQuery(
                "select p from Producto p where p.activo = true and p.stockActual <= p.stockMinimo order by p.nombre",
                com.barbermanager.model.Producto.class).getResultList().stream()
                .map(p -> new ProductoBajo(p.getId(), p.getNombre(), p.getStockActual(), p.getStockMinimo())).toList());
    }
    private void validarPeriodo(LocalDateTime desde, LocalDateTime hasta) {
        if (desde == null || hasta == null || !desde.isBefore(hasta)) throw new IllegalArgumentException("Periodo inválido");
    }
}


package com.barbermanager.controller.inventario;

import com.barbermanager.controller.ControllerBase;
import com.barbermanager.model.MovimientoInventario;
import com.barbermanager.model.Producto;
import com.barbermanager.service.inventario.InventarioService;
import com.barbermanager.service.inventario.ProductoService;
import com.barbermanager.util.Jpa;

import jakarta.persistence.EntityManager;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.math.BigDecimal;

@WebServlet("/recepcion/inventario")
public class InventarioServlet extends ControllerBase {

    private static final String VISTA =
            "/WEB-INF/views/panel/inventario.jsp";

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {
        mostrar(request, response);
    }

    private void mostrar(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        EntityManager em = null;

        try {
            em = Jpa.factory().createEntityManager();

            request.setAttribute("productos", em.createQuery(
                    "SELECT p FROM Producto p ORDER BY p.id DESC",
                    Producto.class
            ).setMaxResults(200).getResultList());

            request.setAttribute("movimientos", em.createQuery(
                    "SELECT m FROM MovimientoInventario m " +
                            "JOIN FETCH m.producto " +
                            "JOIN FETCH m.usuario " +
                            "ORDER BY m.id DESC",
                    MovimientoInventario.class
            ).setMaxResults(30).getResultList());

            String editar = request.getParameter("editar");

            if (editar != null && !editar.isBlank()) {
                Long productoId = id(request, "editar", true);
                Producto producto = em.find(Producto.class, productoId);

                if (producto == null) {
                    throw new IllegalArgumentException(
                            "Producto no encontrado."
                    );
                }

                request.setAttribute("seleccionado", producto);
            }

        } catch (IllegalArgumentException e) {

            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            request.setAttribute("mensajeError", e.getMessage());

        } catch (RuntimeException e) {

            throw new ServletException(
                    "No se pudo cargar el inventario.", e
            );

        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }

        request.getRequestDispatcher(VISTA)
                .forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        try {

            String accion = request.getParameter("accion");

            if ("guardarProducto".equals(accion)) {

                new ProductoService(Jpa.factory()).guardar(
                        id(request, "id", false),
                        request.getParameter("nombre"),
                        request.getParameter("categoria"),
                        decimal(request, "precio"),
                        entero(request, "stockMinimo"),
                        "true".equals(request.getParameter("activo"))
                );

            } else if ("registrarMovimiento".equals(accion)) {

                HttpSession session = request.getSession(false);

                if (session == null ||
                        !(session.getAttribute("usuarioId") instanceof Long)) {
                    response.sendError(
                            HttpServletResponse.SC_UNAUTHORIZED
                    );
                    return;
                }

                Long usuarioId =
                        (Long) session.getAttribute("usuarioId");

                new InventarioService(Jpa.factory())
                        .registrarMovimiento(
                                id(request, "productoId", true),
                                usuarioId,
                                request.getParameter("tipo"),
                                entero(request, "cantidad"),
                                request.getParameter("motivo")
                        );

            } else {
                throw new IllegalArgumentException(
                        "Operación no permitida."
                );
            }

            request.getSession().setAttribute(
                    "flashExito",
                    "Inventario actualizado correctamente."
            );

            response.sendRedirect(
                    request.getContextPath() + "/recepcion/inventario"
            );

        } catch (IllegalArgumentException | IllegalStateException e) {

            response.setStatus(
                    e instanceof IllegalArgumentException ? 400 : 409
            );
            request.setAttribute("mensajeError", e.getMessage());
            mostrar(request, response);

        } catch (RuntimeException e) {

            throw new ServletException(
                    "Error al procesar el inventario.", e
            );
        }
    }

    private int entero(HttpServletRequest request, String campo) {
        try {
            return Integer.parseInt(request.getParameter(campo));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(
                    "El campo " + campo + " debe ser un número entero.", e
            );
        }
    }

    private BigDecimal decimal(HttpServletRequest request, String campo) {

        String valor = request.getParameter(campo);

        if (valor == null || valor.isBlank()) {
            throw new IllegalArgumentException(
                    "Falta el campo " + campo + "."
            );
        }

        try {
            return new BigDecimal(valor);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(
                    "El campo " + campo + " no es un importe válido.", e
            );
        }
    }
}

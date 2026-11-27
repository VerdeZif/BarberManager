
package com.barbermanager.controller.clientes;

import com.barbermanager.controller.ControllerBase;
import com.barbermanager.model.Cliente;
import com.barbermanager.service.clientes.ClienteService;
import com.barbermanager.util.Jpa;

import jakarta.persistence.EntityManager;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@WebServlet("/recepcion/clientes")
public class ClienteServlet extends ControllerBase {

    private static final String VISTA =
            "/WEB-INF/views/panel/clientes.jsp";

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

            List<Cliente> clientes = em.createQuery(
                    "SELECT c FROM Cliente c ORDER BY c.id DESC",
                    Cliente.class
            ).setMaxResults(200).getResultList();

            request.setAttribute("clientes", clientes);

            String editar = request.getParameter("editar");

            if (editar != null && !editar.isBlank()) {
                Long clienteId = id(request, "editar", true);
                Cliente seleccionado = em.find(Cliente.class, clienteId);

                if (seleccionado == null) {
                    throw new IllegalArgumentException(
                            "Cliente no encontrado."
                    );
                }

                request.setAttribute("seleccionado", seleccionado);
            }

        } catch (IllegalArgumentException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            request.setAttribute("mensajeError", e.getMessage());

        } catch (RuntimeException e) {
            throw new ServletException(
                    "No se pudo cargar el módulo Clientes.", e
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
            ClienteService servicio =
                    new ClienteService(Jpa.factory());

            String accion = request.getParameter("accion");

            if ("crear".equals(accion)) {

                servicio.crear(
                        request.getParameter("nombres"),
                        request.getParameter("apellidos"),
                        request.getParameter("telefono")
                );

            } else if ("editar".equals(accion)) {

                servicio.actualizar(
                        id(request, "id", true),
                        request.getParameter("nombres"),
                        request.getParameter("apellidos"),
                        request.getParameter("telefono")
                );

            } else if ("estado".equals(accion)) {

                String activo = request.getParameter("activo");

                if (!"true".equals(activo) &&
                        !"false".equals(activo)) {
                    throw new IllegalArgumentException(
                            "Estado de cliente inválido."
                    );
                }

                servicio.cambiarEstado(
                        id(request, "id", true),
                        Boolean.parseBoolean(activo)
                );

            } else {
                throw new IllegalArgumentException(
                        "Operación no permitida."
                );
            }

            request.getSession().setAttribute(
                    "flashExito",
                    "Operación realizada correctamente."
            );

            response.sendRedirect(
                    request.getContextPath() + "/recepcion/clientes"
            );

        } catch (IllegalArgumentException | IllegalStateException e) {

            response.setStatus(
                    e instanceof IllegalArgumentException ? 400 : 409
            );
            request.setAttribute("mensajeError", e.getMessage());
            mostrar(request, response);

        } catch (RuntimeException e) {
            throw new ServletException(
                    "Error al guardar los datos del cliente.", e
            );
        }
    }
}

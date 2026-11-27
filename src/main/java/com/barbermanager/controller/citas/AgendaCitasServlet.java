
package com.barbermanager.controller.citas;

import com.barbermanager.model.*;
import com.barbermanager.service.citas.CitaService;
import com.barbermanager.service.atenciones.AtencionService;
import com.barbermanager.util.Jpa;

import jakarta.persistence.EntityManager;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;

@WebServlet("/recepcion/citas")
public class AgendaCitasServlet extends HttpServlet {

    private static final String VISTA =
            "/WEB-INF/views/panel/agenda.jsp";

    private static final ZoneId ZONA =
            ZoneId.of("America/Lima");

    private static final DateTimeFormatter FECHA_HORA =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        cargarPagina(request, response);
    }

    private void cargarPagina(HttpServletRequest request,
                              HttpServletResponse response)
            throws ServletException, IOException {

        LocalDate fecha;

        try {
            String valor = request.getParameter("fecha");

            fecha = valor == null || valor.isBlank()
                    ? LocalDate.now(ZONA)
                    : LocalDate.parse(valor);

        } catch (DateTimeException e) {
            fecha = LocalDate.now(ZONA);
            request.setAttribute(
                    "mensajeError",
                    "La fecha seleccionada no es válida."
            );
        }

        EntityManager em = null;

        try {
            em = Jpa.factory().createEntityManager();

            request.setAttribute("fechaFiltro", fecha.toString());

            request.setAttribute("clientes", em.createQuery(
                    "SELECT c FROM Cliente c " +
                            "WHERE c.activo = true ORDER BY c.nombres, c.apellidos",
                    Cliente.class
            ).getResultList());

            request.setAttribute("barberos", em.createQuery(
                    "SELECT b FROM Barbero b " +
                            "WHERE b.activo = true ORDER BY b.nombres, b.apellidos",
                    Barbero.class
            ).getResultList());

            request.setAttribute("servicios", em.createQuery(
                    "SELECT s FROM Servicio s " +
                            "WHERE s.activo = true ORDER BY s.nombre",
                    Servicio.class
            ).getResultList());

            List<Cita> resultados = em.createQuery(
                            "SELECT c FROM Cita c " +
                                    "JOIN FETCH c.cliente " +
                                    "JOIN FETCH c.barbero " +
                                    "JOIN FETCH c.servicio " +
                                    "WHERE c.inicio >= :desde AND c.inicio < :hasta " +
                                    "ORDER BY c.inicio",
                            Cita.class
                    )
                    .setParameter("desde", fecha.atStartOfDay())
                    .setParameter("hasta", fecha.plusDays(1).atStartOfDay())
                    .getResultList();

            List<Map<String, Object>> filas = new ArrayList<>();

            for (Cita c : resultados) {
                Map<String, Object> fila = new HashMap<>();

                fila.put("id", c.getId());
                fila.put("cliente", c.getCliente().getNombres()
                        + " " + c.getCliente().getApellidos());
                fila.put("barbero", c.getBarbero().getNombres()
                        + " " + c.getBarbero().getApellidos());
                fila.put("servicio", c.getServicio().getNombre());
                fila.put("inicio", c.getInicio().format(FECHA_HORA));
                fila.put("fin", c.getFin().format(FECHA_HORA));
                fila.put("estado", c.getEstado());
                fila.put("precio", c.getPrecioReservado());

                filas.add(fila);
            }

            request.setAttribute("citas", filas);

        } catch (RuntimeException e) {
            throw new ServletException(
                    "No se pudo cargar la agenda de citas.", e
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

        String accion = request.getParameter("accion");

        String fechaRedireccion =
                LocalDate.now(ZONA).toString();

        try {
            String filtro = request.getParameter("fecha");

            if (filtro != null && !filtro.isBlank()) {
                fechaRedireccion = LocalDate.parse(filtro).toString();
            }

            CitaService citas = new CitaService(Jpa.factory());

            if ("reservar".equals(accion)) {

                LocalDateTime inicio = obtenerFechaHora(request);

                citas.reservar(
                        obtenerId(request, "clienteId"),
                        obtenerId(request, "barberoId"),
                        obtenerId(request, "servicioId"),
                        inicio
                );

                fechaRedireccion = inicio.toLocalDate().toString();

            } else if ("confirmar".equals(accion)) {

                citas.cambiarEstado(
                        obtenerId(request, "citaId"),
                        "CONFIRMADA"
                );

            } else if ("cancelar".equals(accion)) {

                citas.cambiarEstado(
                        obtenerId(request, "citaId"),
                        "CANCELADA"
                );

            } else if ("no_asistio".equals(accion)) {

                citas.cambiarEstado(
                        obtenerId(request, "citaId"),
                        "NO_ASISTIO"
                );

            } else if ("reprogramar".equals(accion)) {

                LocalDateTime inicio = obtenerFechaHora(request);

                citas.reprogramar(
                        obtenerId(request, "citaId"),
                        inicio
                );

                fechaRedireccion = inicio.toLocalDate().toString();

            } else if ("iniciar".equals(accion)) {

                new AtencionService(Jpa.factory()).iniciar(
                        obtenerId(request, "citaId")
                );

            } else if ("finalizar".equals(accion)) {

                new AtencionService(Jpa.factory()).finalizar(
                        obtenerId(request, "citaId"),
                        request.getParameter("observaciones")
                );

            } else {
                throw new IllegalArgumentException(
                        "Acción no permitida."
                );
            }

            request.getSession().setAttribute(
                    "flashExito",
                    "Operación realizada correctamente."
            );

            response.sendRedirect(
                    request.getContextPath()
                            + "/recepcion/citas?fecha="
                            + fechaRedireccion
            );

        } catch (IllegalArgumentException |
                 IllegalStateException |
                 DateTimeException e) {

            response.setStatus(
                    e instanceof IllegalStateException ? 409 : 400
            );

            request.setAttribute(
                    "mensajeError",
                    e.getMessage()
            );

            cargarPagina(request, response);
        }
    }

    private Long obtenerId(HttpServletRequest request, String campo) {

        String valor = request.getParameter(campo);

        try {
            long id = Long.parseLong(valor);

            if (id <= 0) {
                throw new NumberFormatException();
            }

            return id;

        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(
                    "Selecciona un valor válido para " + campo + "."
            );
        }
    }

    private LocalDateTime obtenerFechaHora(HttpServletRequest request) {

        String valor = request.getParameter("inicio");

        if (valor == null || valor.isBlank()) {
            throw new IllegalArgumentException(
                    "Debes seleccionar una fecha y hora."
            );
        }

        try {
            return LocalDateTime.parse(valor);
        } catch (DateTimeException e) {
            throw new IllegalArgumentException(
                    "Fecha y hora incorrectas."
            );
        }
    }
}


<%@ page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<%@ include file="/WEB-INF/views/fragments/taglibs.jspf" %>

<c:set var="tituloPagina" value="Reservar cita"/>
<c:set var="paginaActiva" value="reservar"/>

<%@ include file="/WEB-INF/views/fragments/head.jspf" %>
<%@ include file="/WEB-INF/views/fragments/header-publico.jspf" %>

<main id="contenido" class="bm-contenedor py-5">

    <span class="bm-ruta">Inicio / Reservar cita</span>

    <h1 class="mt-2">Reserva tu cita</h1>

    <p class="bm-muted mb-4">
        Elige el servicio, tu barbero y un horario
        dentro de su jornada de trabajo.
    </p>

    <%@ include file="/WEB-INF/views/fragments/alertas.jspf" %>

    <section class="bm-card p-3 p-md-4">

        <h2 class="mb-4">Nueva reserva</h2>

        <c:if test="${puedeReservar}">

            <form method="post"
                  action="${ctx}/reservas/nueva">

                <input type="hidden"
                       name="csrfToken"
                       value="<c:out value='${sessionScope.csrfToken}'/>">

                <div class="row g-3">

                    <div class="col-md-6">

                        <label class="form-label">
                            Servicio
                        </label>

                        <select name="servicioId"
                                class="form-select"
                                required>

                            <option value="">
                                Selecciona un servicio
                            </option>

                            <c:forEach var="s" items="${servicios}">

                                <option value="${s.id}"
                                    ${s.id == servicioPreseleccionado ? 'selected' : ''}>

                                    <c:out value="${s.nombre}"/>
                                    — S/
                                    <fmt:formatNumber
                                        value="${s.precio}"
                                        minFractionDigits="2"
                                        maxFractionDigits="2"/>
                                    — ${s.duracionMinutos} min

                                </option>

                            </c:forEach>

                        </select>

                    </div>

                    <div class="col-md-6">

                        <label class="form-label">
                            Barbero
                        </label>

                        <select name="barberoId"
                                class="form-select"
                                required>

                            <option value="">
                                Selecciona un barbero
                            </option>

                            <c:forEach var="b" items="${barberos}">


                                <option value="${b.id}"
                                    ${b.id == barberoPreseleccionado ? 'selected="selected"' : ''}>
                                    <c:out value="${b.nombres} ${b.apellidos}"/>
                                    — ${b.horaInicio} a ${b.horaFin}
                                </option>

                            </c:forEach>

                        </select>

                    </div>

                    <div class="col-md-6">

                        <label class="form-label">
                            Fecha y hora
                        </label>

                        <input type="datetime-local"
                               name="inicio"
                               class="form-control"
                               step="60"
                               required>

                        <div class="form-text">
                            El sistema comprobará la disponibilidad
                            del barbero antes de guardar.
                        </div>

                    </div>

                    <div class="col-12 d-flex flex-wrap gap-2">

                        <button type="submit"
                                class="btn btn-bm">
                            Confirmar solicitud
                        </button>

                        <a href="${ctx}/mis-citas"
                           class="btn btn-bm-borde">
                            Ver mis citas
                        </a>

                    </div>

                </div>

            </form>

        </c:if>

    </section>

    <p class="small bm-muted mt-3">
        La reserva queda pendiente hasta que Recepción
        la confirme. El pago se realiza en el local.
    </p>

</main>

<%@ include file="/WEB-INF/views/fragments/footer.jspf" %>

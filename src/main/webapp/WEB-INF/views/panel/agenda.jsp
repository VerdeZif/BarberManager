
<%@ page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<%@ include file="/WEB-INF/views/fragments/taglibs.jspf" %>

<c:set var="tituloPagina" value="Agenda de citas"/>
<c:set var="menuActivo" value="citas"/>

<%@ include file="/WEB-INF/views/fragments/head.jspf" %>
<%@ include file="/WEB-INF/views/fragments/panel-inicio.jspf" %>

<span class="bm-ruta">Recepción / Agenda de citas</span>

<h1 class="mt-2">Agenda de citas</h1>

<p class="bm-muted mb-4">
    Organiza las reservas, horarios y atenciones de la barbería.
</p>

<%@ include file="/WEB-INF/views/fragments/alertas.jspf" %>

<section class="bm-card p-3 p-md-4 mb-4">

    <h2 class="mb-3">Consultar agenda</h2>

    <form method="get" action="${ctx}/recepcion/citas"
          class="row g-3 align-items-end">

        <div class="col-md-5">
            <label class="form-label" for="fechaFiltro">
                Fecha
            </label>

            <input type="date"
                   id="fechaFiltro"
                   name="fecha"
                   class="form-control"
                   value="${fechaFiltro}"
                   required>
        </div>

        <div class="col-md-3">
            <button type="submit" class="btn btn-bm">
                Buscar citas
            </button>
        </div>

    </form>

</section>

<section class="bm-card p-3 p-md-4 mb-4">

    <h2 class="mb-3">Registrar nueva cita</h2>

    <form method="post" action="${ctx}/recepcion/citas">

        <input type="hidden" name="csrfToken"
               value="<c:out value='${sessionScope.csrfToken}'/>">

        <input type="hidden" name="accion" value="reservar">

        <input type="hidden" name="fecha" value="${fechaFiltro}">

        <div class="row g-3">

            <div class="col-md-6">
                <label class="form-label">Cliente</label>

                <select class="form-select" name="clienteId" required>
                    <option value="">Selecciona un cliente</option>

                    <c:forEach var="cliente" items="${clientes}">
                        <option value="${cliente.id}">
                            <c:out value="${cliente.nombres} ${cliente.apellidos}"/>
                        </option>
                    </c:forEach>
                </select>
            </div>

            <div class="col-md-6">
                <label class="form-label">Barbero</label>

                <select class="form-select" name="barberoId" required>
                    <option value="">Selecciona un barbero</option>

                    <c:forEach var="barbero" items="${barberos}">
                        <option value="${barbero.id}">
                            <c:out value="${barbero.nombres} ${barbero.apellidos}"/>
                        </option>
                    </c:forEach>
                </select>
            </div>

            <div class="col-md-6">
                <label class="form-label">Servicio</label>

                <select class="form-select" name="servicioId" required>
                    <option value="">Selecciona un servicio</option>

                    <c:forEach var="servicio" items="${servicios}">
                        <option value="${servicio.id}">
                            <c:out value="${servicio.nombre}"/>
                            — S/
                            <fmt:formatNumber
                                value="${servicio.precio}"
                                minFractionDigits="2"
                                maxFractionDigits="2"/>
                            (${servicio.duracionMinutos} min)
                        </option>
                    </c:forEach>
                </select>
            </div>

            <div class="col-md-6">
                <label class="form-label">Fecha y hora</label>

                <input type="datetime-local"
                       class="form-control"
                       name="inicio"
                       step="60"
                       required>
            </div>

            <div class="col-12">
                <button type="submit" class="btn btn-bm">
                    Registrar cita
                </button>
            </div>

        </div>

    </form>

</section>

<section class="bm-card p-3 p-md-4">

    <h2 class="mb-3">Citas del día</h2>

    <c:choose>

        <c:when test="${empty citas}">
            <div class="bm-vacio">
                <b>No hay citas registradas para esta fecha</b>
                <span>
                    Puedes crear una nueva cita desde el formulario superior.
                </span>
            </div>
        </c:when>

        <c:otherwise>

            <div class="bm-tabla-wrap bm-tabla-wrap--suave">

                <table class="table bm-tabla bm-tabla-responsive">

                    <thead>
                        <tr>
                            <th>Horario</th>
                            <th>Cliente</th>
                            <th>Barbero</th>
                            <th>Servicio</th>
                            <th>Precio</th>
                            <th>Estado</th>
                            <th>Acciones</th>
                        </tr>
                    </thead>

                    <tbody>

                        <c:forEach var="cita" items="${citas}">

                            <tr>

                                <td data-label="Horario">
                                    <strong>
                                        <c:out value="${cita.inicio}"/>
                                    </strong>
                                    <div class="small bm-muted">
                                        Hasta <c:out value="${cita.fin}"/>
                                    </div>
                                </td>

                                <td data-label="Cliente">
                                    <c:out value="${cita.cliente}"/>
                                </td>

                                <td data-label="Barbero">
                                    <c:out value="${cita.barbero}"/>
                                </td>

                                <td data-label="Servicio">
                                    <c:out value="${cita.servicio}"/>
                                </td>

                                <td data-label="Precio">
                                    S/
                                    <fmt:formatNumber
                                        value="${cita.precio}"
                                        minFractionDigits="2"
                                        maxFractionDigits="2"/>
                                </td>

                                <td data-label="Estado">
                                    <span class="bm-chip">
                                        <c:out value="${cita.estado}"/>
                                    </span>
                                </td>

                                <td data-label="Acciones">

                                    <form method="post"
                                          action="${ctx}/recepcion/citas"
                                          class="d-flex flex-wrap gap-2 mb-2">

                                        <input type="hidden"
                                               name="csrfToken"
                                               value="<c:out value='${sessionScope.csrfToken}'/>">

                                        <input type="hidden"
                                               name="citaId"
                                               value="${cita.id}">

                                        <input type="hidden"
                                               name="fecha"
                                               value="${fechaFiltro}">

                                        <c:if test="${cita.estado == 'PENDIENTE'}">

                                            <button name="accion"
                                                    value="confirmar"
                                                    class="btn btn-bm btn-bm-sm">
                                                Confirmar
                                            </button>

                                        </c:if>

                                        <c:if test="${cita.estado == 'PENDIENTE'
                                                    or cita.estado == 'CONFIRMADA'}">

                                            <button name="accion"
                                                    value="cancelar"
                                                    class="btn btn-bm-borde btn-bm-sm">
                                                Cancelar
                                            </button>

                                        </c:if>

                                        <c:if test="${cita.estado == 'CONFIRMADA'}">

                                            <button name="accion"
                                                    value="iniciar"
                                                    class="btn btn-bm btn-bm-sm">
                                                Iniciar atención
                                            </button>

                                            <button name="accion"
                                                    value="no_asistio"
                                                    class="btn btn-bm-borde btn-bm-sm">
                                                No asistió
                                            </button>

                                        </c:if>

                                        <c:if test="${cita.estado == 'EN_ATENCION'}">

                                            <input class="form-control form-control-sm"
                                                   name="observaciones"
                                                   maxlength="500"
                                                   placeholder="Observaciones (opcional)">

                                            <button name="accion"
                                                    value="finalizar"
                                                    class="btn btn-bm btn-bm-sm">
                                                Finalizar atención
                                            </button>

                                        </c:if>

                                    </form>

                                    <c:if test="${cita.estado == 'PENDIENTE'
                                                or cita.estado == 'CONFIRMADA'}">

                                        <form method="post"
                                              action="${ctx}/recepcion/citas"
                                              class="d-flex flex-wrap gap-2">

                                            <input type="hidden"
                                                   name="csrfToken"
                                                   value="<c:out value='${sessionScope.csrfToken}'/>">

                                            <input type="hidden"
                                                   name="accion"
                                                   value="reprogramar">

                                            <input type="hidden"
                                                   name="fecha"
                                                   value="${fechaFiltro}">

                                            <input type="hidden"
                                                   name="citaId"
                                                   value="${cita.id}">

                                            <input type="datetime-local"
                                                   name="inicio"
                                                   class="form-control form-control-sm"
                                                   aria-label="Nueva fecha y hora"
                                                   required>

                                            <button type="submit"
                                                    class="btn btn-bm-borde btn-bm-sm">
                                                Reprogramar
                                            </button>

                                        </form>

                                    </c:if>

                                </td>

                            </tr>

                        </c:forEach>

                    </tbody>

                </table>

            </div>

        </c:otherwise>

    </c:choose>

</section>

<%@ include file="/WEB-INF/views/fragments/panel-fin.jspf" %>

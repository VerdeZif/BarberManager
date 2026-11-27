
<%@ page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<%@ include file="/WEB-INF/views/fragments/taglibs.jspf" %>

<c:set var="tituloPagina" value="Mis atenciones"/>
<c:set var="menuActivo" value="b-atenciones"/>

<%@ include file="/WEB-INF/views/fragments/head.jspf" %>
<%@ include file="/WEB-INF/views/fragments/panel-inicio.jspf" %>

<span class="bm-ruta">
    Barbero / Atenciones
</span>

<h1 class="mt-2">Mis atenciones</h1>

<p class="bm-muted mb-4">
    Gestiona los servicios asignados a tu cuenta.
    Solo puedes iniciar y finalizar tus propias atenciones.
</p>

<%@ include file="/WEB-INF/views/fragments/alertas.jspf" %>

<c:if test="${not empty nombreBarbero}">
    <p class="mb-4">
        Profesional:
        <strong>
            <c:out value="${nombreBarbero}"/>
        </strong>
    </p>
</c:if>

<section class="bm-card p-3 p-md-4 mb-4">

    <h2 class="mb-3">Consultar atenciones</h2>

    <form method="get"
          action="${ctx}/barbero/atenciones"
          class="row g-3 align-items-end">

        <div class="col-md-5">
            <label for="fechaFiltro" class="form-label">
                Seleccionar fecha
            </label>

            <input type="date"
                   id="fechaFiltro"
                   name="fecha"
                   class="form-control"
                   value="${fechaFiltro}"
                   required>
        </div>

        <div class="col-md-3">
            <button type="submit"
                    class="btn btn-bm">
                Consultar
            </button>
        </div>

    </form>

</section>

<div class="row g-3 mb-4">

    <div class="col-sm-6 col-xl-3">
        <div class="bm-card p-4 h-100">
            <p class="bm-muted mb-2">
                Citas del día
            </p>
            <h2 class="mb-0">
                <c:out value="${totalCitas + 0}"/>
            </h2>
        </div>
    </div>

    <div class="col-sm-6 col-xl-3">
        <div class="bm-card p-4 h-100">
            <p class="bm-muted mb-2">
                Confirmadas
            </p>
            <h2 class="mb-0">
                <c:out value="${confirmadas + 0}"/>
            </h2>
        </div>
    </div>

    <div class="col-sm-6 col-xl-3">
        <div class="bm-card p-4 h-100">
            <p class="bm-muted mb-2">
                En atención
            </p>
            <h2 class="mb-0">
                <c:out value="${enAtencion + 0}"/>
            </h2>
        </div>
    </div>

    <div class="col-sm-6 col-xl-3">
        <div class="bm-card p-4 h-100">
            <p class="bm-muted mb-2">
                Finalizadas
            </p>
            <h2 class="mb-0">
                <c:out value="${finalizadas + 0}"/>
            </h2>
        </div>
    </div>

</div>

<section class="bm-card p-3 p-md-4">

    <h2 class="mb-3">
        Atenciones del
        <c:out value="${fechaTexto}"/>
    </h2>

    <div class="bm-tabla-wrap bm-tabla-wrap--suave">

        <c:choose>

            <c:when test="${empty atenciones}">

                <div class="bm-vacio">
                    <b>No hay citas asignadas en esta fecha</b>
                    <span>
                        Selecciona otro día para consultar
                        tus atenciones.
                    </span>
                </div>

            </c:when>

            <c:otherwise>

                <table class="table bm-tabla bm-tabla-responsive">

                    <thead>
                        <tr>
                            <th>Horario</th>
                            <th>Cliente</th>
                            <th>Servicio</th>
                            <th>Precio</th>
                            <th>Estado</th>
                            <th>Acciones</th>
                        </tr>
                    </thead>

                    <tbody>

                        <c:forEach var="cita"
                                   items="${atenciones}">

                            <tr>

                                <td data-label="Horario">
                                    <strong>
                                        <c:out value="${cita.inicio}"/>
                                    </strong>
                                    <div class="small bm-muted">
                                        Hasta
                                        <c:out value="${cita.fin}"/>
                                    </div>
                                </td>

                                <td data-label="Cliente">
                                    <c:out value="${cita.cliente}"/>
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

                                    <c:choose>

                                        <c:when test="${cita.estado == 'CONFIRMADA'}">

                                            <form method="post"
                                                  action="${ctx}/barbero/atenciones">

                                                <input type="hidden"
                                                       name="csrfToken"
                                                       value="<c:out value='${sessionScope.csrfToken}'/>">

                                                <input type="hidden"
                                                       name="fecha"
                                                       value="${fechaFiltro}">

                                                <input type="hidden"
                                                       name="citaId"
                                                       value="${cita.id}">

                                                <input type="hidden"
                                                       name="accion"
                                                       value="iniciar">

                                                <button type="submit"
                                                        class="btn btn-bm btn-bm-sm">
                                                    Iniciar atención
                                                </button>

                                            </form>

                                        </c:when>

                                        <c:when test="${cita.estado == 'EN_ATENCION'}">

                                            <form method="post"
                                                  action="${ctx}/barbero/atenciones"
                                                  class="d-flex flex-column gap-2">

                                                <input type="hidden"
                                                       name="csrfToken"
                                                       value="<c:out value='${sessionScope.csrfToken}'/>">

                                                <input type="hidden"
                                                       name="fecha"
                                                       value="${fechaFiltro}">

                                                <input type="hidden"
                                                       name="citaId"
                                                       value="${cita.id}">

                                                <input type="hidden"
                                                       name="accion"
                                                       value="finalizar">

                                                <textarea
                                                    class="form-control form-control-sm"
                                                    name="observaciones"
                                                    rows="2"
                                                    maxlength="500"
                                                    placeholder="Observaciones del servicio (opcional)"></textarea>

                                                <button type="submit"
                                                        class="btn btn-bm btn-bm-sm">
                                                    Finalizar atención
                                                </button>

                                            </form>

                                        </c:when>

                                        <c:when test="${cita.estado == 'FINALIZADA'}">

                                            <span class="bm-chip bm-chip-OK">
                                                Completada
                                            </span>

                                        </c:when>

                                        <c:when test="${cita.estado == 'PENDIENTE'}">

                                            <span class="small bm-muted">
                                                Pendiente de confirmación
                                            </span>

                                        </c:when>

                                        <c:otherwise>

                                            <span class="small bm-muted">
                                                Sin acciones disponibles
                                            </span>

                                        </c:otherwise>

                                    </c:choose>

                                </td>

                            </tr>

                        </c:forEach>

                    </tbody>

                </table>

            </c:otherwise>

        </c:choose>

    </div>

    <p class="small bm-muted mt-3 mb-0">
        Las citas se confirman desde Recepción.
        Las atenciones finalizadas podrán ser
        cobradas desde Ventas y cobros.
    </p>

</section>

<%@ include file="/WEB-INF/views/fragments/panel-fin.jspf" %>

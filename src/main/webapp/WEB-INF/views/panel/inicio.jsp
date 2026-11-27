
<%@ page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<%@ include file="/WEB-INF/views/fragments/taglibs.jspf" %>

<c:set var="tituloPagina" value="${tituloPanel}" />
<c:set var="menuActivo" value="${menuActivoPanel}" />

<%@ include file="/WEB-INF/views/fragments/head.jspf" %>
<%@ include file="/WEB-INF/views/fragments/panel-inicio.jspf" %>

<span class="bm-ruta">
    BarberManager / Panel interno
</span>

<h1 class="mt-2 mb-2">
    <c:out value="${tituloPanel}" />
</h1>

<p class="bm-muted mb-4">
    Bienvenido,
    <strong>
        <c:out value="${usuarioSesion.username}" />
    </strong>.
    Consulta la información de tu área.
</p>

<%@ include file="/WEB-INF/views/fragments/alertas.jspf" %>

<c:if test="${esAdmin}">

    <div class="row g-3 mb-4">

        <div class="col-sm-6 col-xl-3">
            <div class="bm-card p-4 h-100">
                <p class="bm-muted mb-2">Usuarios</p>
                <h2 class="mb-0">
                    <c:out value="${totalUsuarios}" />
                </h2>
            </div>
        </div>

        <div class="col-sm-6 col-xl-3">
            <div class="bm-card p-4 h-100">
                <p class="bm-muted mb-2">Barberos activos</p>
                <h2 class="mb-0">
                    <c:out value="${totalBarberos}" />
                </h2>
            </div>
        </div>

        <div class="col-sm-6 col-xl-3">
            <div class="bm-card p-4 h-100">
                <p class="bm-muted mb-2">Servicios activos</p>
                <h2 class="mb-0">
                    <c:out value="${totalServicios}" />
                </h2>
            </div>
        </div>

        <div class="col-sm-6 col-xl-3">
            <div class="bm-card p-4 h-100">
                <p class="bm-muted mb-2">Citas registradas</p>
                <h2 class="mb-0">
                    <c:out value="${totalCitas}" />
                </h2>
            </div>
        </div>

    </div>

</c:if>

<div class="d-flex flex-wrap justify-content-between
            align-items-center gap-2 mb-3">

    <h2 class="mb-0">
        <c:choose>
            <c:when test="${esAdmin}">
                Últimas citas registradas
            </c:when>
            <c:otherwise>
                Listado de citas
            </c:otherwise>
        </c:choose>
    </h2>

</div>

<div class="bm-tabla-wrap">

    <c:choose>

        <c:when test="${empty citas}">

            <div class="bm-vacio">
                <b>No hay citas para mostrar</b>
                <span>
                    Cuando existan citas aparecerán aquí.
                </span>
            </div>

        </c:when>

        <c:otherwise>

            <table class="table bm-tabla bm-tabla-responsive">

                <thead>
                    <tr>
                        <th scope="col">Cliente</th>
                        <th scope="col">Barbero</th>
                        <th scope="col">Servicio</th>
                        <th scope="col">Fecha y hora</th>
                        <th scope="col">Estado</th>
                    </tr>
                </thead>

                <tbody>

                    <c:forEach var="cita" items="${citas}">

                        <tr>
                            <td class="bm-td-principal"
                                data-label="Cliente">
                                <c:out value="${cita.cliente}" />
                            </td>

                            <td data-label="Barbero">
                                <c:out value="${cita.barbero}" />
                            </td>

                            <td data-label="Servicio">
                                <c:out value="${cita.servicio}" />
                            </td>

                            <td data-label="Fecha y hora">
                                <c:out value="${cita.fecha}" />
                            </td>

                            <td data-label="Estado">
                                <span class="bm-chip">
                                    <c:out value="${cita.estado}" />
                                </span>
                            </td>
                        </tr>

                    </c:forEach>

                </tbody>

            </table>

        </c:otherwise>

    </c:choose>

</div>

<p class="small bm-muted mt-3">
    Se muestran hasta 50 citas, ordenadas desde la más reciente.
</p>

<%@ include file="/WEB-INF/views/fragments/panel-fin.jspf" %>

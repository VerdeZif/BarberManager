
<%@ page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<%@ include file="/WEB-INF/views/fragments/taglibs.jspf" %>

<c:set var="tituloPagina"
       value="${esAdmin ? 'Gestión de comisiones' : 'Mis comisiones'}"/>

<c:set var="menuActivo"
       value="${esAdmin ? 'comisiones' : 'b-comisiones'}"/>

<%@ include file="/WEB-INF/views/fragments/head.jspf" %>
<%@ include file="/WEB-INF/views/fragments/panel-inicio.jspf" %>

<span class="bm-ruta">
    <c:choose>
        <c:when test="${esAdmin}">
            Administración / Comisiones
        </c:when>
        <c:otherwise>
            Barbero / Mis comisiones
        </c:otherwise>
    </c:choose>
</span>

<h1 class="mt-2">
    <c:out value="${tituloPagina}"/>
</h1>

<p class="bm-muted mb-4">
    Consulta los importes generados por servicios
    realizados y cobrados.
</p>

<%@ include file="/WEB-INF/views/fragments/alertas.jspf" %>

<div class="row g-3 mb-4">

    <div class="col-md-4">
        <div class="bm-card p-4 h-100">

            <p class="bm-muted mb-2">
                Comisiones pendientes
            </p>

            <h2 class="mb-0">
                S/
                <fmt:formatNumber
                    value="${totalPendiente}"
                    minFractionDigits="2"
                    maxFractionDigits="2"/>
            </h2>

            <span class="small bm-muted">
                Pendientes de liquidación
            </span>

        </div>
    </div>

    <div class="col-md-4">
        <div class="bm-card p-4 h-100">

            <p class="bm-muted mb-2">
                Comisiones pagadas
            </p>

            <h2 class="mb-0">
                S/
                <fmt:formatNumber
                    value="${totalPagado}"
                    minFractionDigits="2"
                    maxFractionDigits="2"/>
            </h2>

            <span class="small bm-muted">
                Liquidadas internamente
            </span>

        </div>
    </div>

    <div class="col-md-4">
        <div class="bm-card p-4 h-100">

            <p class="bm-muted mb-2">
                Registros consultados
            </p>

            <h2 class="mb-0">
                <c:out value="${cantidad}"/>
            </h2>

            <span class="small bm-muted">
                Hasta 200 comisiones recientes
            </span>

        </div>
    </div>

</div>

<section class="bm-card p-3 p-md-4">

    <div class="d-flex flex-wrap justify-content-between
                align-items-center gap-2 mb-3">

        <div>
            <h2 class="mb-1">
                Historial de comisiones
            </h2>

            <p class="bm-muted mb-0">
                Los importes se calculan sobre el valor
                del servicio, no sobre los productos vendidos.
            </p>
        </div>

    </div>

    <div class="bm-tabla-wrap bm-tabla-wrap--suave">

        <c:choose>

            <c:when test="${empty comisiones}">

                <div class="bm-vacio">
                    <b>No hay comisiones para mostrar</b>

                    <span>
                        Las comisiones aparecen cuando una
                        atención finalizada se cobra completamente.
                    </span>
                </div>

            </c:when>

            <c:otherwise>

                <table class="table bm-tabla bm-tabla-responsive">

                    <thead>
                        <tr>
                            <th>ID</th>

                            <c:if test="${esAdmin}">
                                <th>Barbero</th>
                            </c:if>

                            <th>Servicio</th>
                            <th>Atención</th>
                            <th>Base</th>
                            <th>%</th>
                            <th>Comisión</th>
                            <th>Generada</th>
                            <th>Estado</th>

                            <c:if test="${esAdmin}">
                                <th>Acción</th>
                            </c:if>
                        </tr>
                    </thead>

                    <tbody>

                        <c:forEach var="c" items="${comisiones}">

                            <tr>

                                <td data-label="ID">
                                    #<c:out value="${c.id}"/>
                                </td>

                                <c:if test="${esAdmin}">
                                    <td data-label="Barbero">
                                        <strong>
                                            <c:out value="${c.barbero}"/>
                                        </strong>
                                    </td>
                                </c:if>

                                <td data-label="Servicio">
                                    <c:out value="${c.servicio}"/>
                                </td>

                                <td data-label="Atención">
                                    #<c:out value="${c.atencionId}"/>
                                </td>

                                <td data-label="Base">
                                    S/
                                    <fmt:formatNumber
                                        value="${c.base}"
                                        minFractionDigits="2"
                                        maxFractionDigits="2"/>
                                </td>

                                <td data-label="Porcentaje">
                                    <fmt:formatNumber
                                        value="${c.porcentaje}"
                                        minFractionDigits="2"
                                        maxFractionDigits="2"/> %
                                </td>

                                <td class="bm-td-principal"
                                    data-label="Comisión">
                                    <strong>
                                        S/
                                        <fmt:formatNumber
                                            value="${c.monto}"
                                            minFractionDigits="2"
                                            maxFractionDigits="2"/>
                                    </strong>
                                </td>

                                <td data-label="Generada">
                                    <c:out value="${c.fechaGeneracion}"/>
                                </td>

                                <td data-label="Estado">

                                    <span class="bm-chip bm-chip-${c.estado}">
                                        <c:out value="${c.estado}"/>
                                    </span>

                                    <c:if test="${c.estado == 'PAGADA'}">
                                        <div class="small bm-muted mt-1">
                                            <c:out value="${c.fechaPago}"/>
                                        </div>
                                    </c:if>

                                </td>

                                <c:if test="${esAdmin}">

                                    <td data-label="Acción">

                                        <c:choose>

                                            <c:when test="${c.estado == 'PENDIENTE'}">

                                                <form method="post"
                                                      action="${ctx}/admin/comisiones">

                                                    <input type="hidden"
                                                           name="csrfToken"
                                                           value="<c:out value='${sessionScope.csrfToken}'/>">

                                                    <input type="hidden"
                                                           name="accion"
                                                           value="marcarPagada">

                                                    <input type="hidden"
                                                           name="id"
                                                           value="${c.id}">

                                                    <button type="submit"
                                                            class="btn btn-bm btn-bm-sm">
                                                        Marcar pagada
                                                    </button>

                                                </form>

                                            </c:when>

                                            <c:otherwise>
                                                <span class="small bm-muted">
                                                    Liquidada
                                                </span>
                                            </c:otherwise>

                                        </c:choose>

                                    </td>

                                </c:if>

                            </tr>

                        </c:forEach>

                    </tbody>

                </table>

            </c:otherwise>

        </c:choose>

    </div>

    <p class="small bm-muted mt-3 mb-0">
        Los totales corresponden a los registros mostrados.
        La liquidación es interna y no realiza
        transferencias de dinero.
    </p>

</section>

<%@ include file="/WEB-INF/views/fragments/panel-fin.jspf" %>

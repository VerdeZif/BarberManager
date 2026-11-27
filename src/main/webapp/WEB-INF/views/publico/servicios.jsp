<%@ page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<%@ include file="/WEB-INF/views/fragments/taglibs.jspf" %>
<%--
  GET /servicios — Catálogo público
  Request: servicios (List<Servicio> activos: id, nombre, descripcion, precio [BigDecimal], duracionMinutos)
--%>
<c:set var="tituloPagina" value="Servicios y precios"/>
<c:set var="paginaActiva" value="servicios"/>
<%@ include file="/WEB-INF/views/fragments/head.jspf" %>
<%@ include file="/WEB-INF/views/fragments/header-publico.jspf" %>

<main id="contenido" class="bm-contenedor pt-4 pb-5" style="max-width:1180px">
    <%@ include file="/WEB-INF/views/fragments/alertas.jspf" %>
    <div class="d-flex flex-wrap justify-content-between align-items-end gap-3 mb-4">
        <div>
            <span class="bm-ruta">Catálogo</span>
            <h1 class="m-0 mt-1" style="font-size:clamp(40px,6vw,56px)">Servicios y precios</h1>
            <p class="m-0 bm-muted">Cada cita incluye un servicio. Precios en soles; pagas en el local.</p>
        </div>
        <a class="btn btn-bm-borde" href="${ctx}/barberos">Conoce a los barberos</a>
    </div>

    <div class="bm-tabla-wrap">
        <c:choose>
            <c:when test="${empty servicios}">
                <div class="bm-vacio">
                    <b>Aún no hay servicios publicados</b>
                    <span>Vuelve pronto o comunícate con la barbería.</span>
                </div>
            </c:when>
            <c:otherwise>
                <table class="table bm-tabla bm-tabla-responsive">
                    <caption class="visually-hidden">Lista de servicios con duración y precio</caption>
                    <thead>
                    <tr><th scope="col">Servicio</th><th scope="col">Duración</th><th scope="col" class="text-end">Precio</th><th scope="col"><span class="visually-hidden">Acción</span></th></tr>
                    </thead>
                    <tbody>
                    <c:forEach var="s" items="${servicios}">
                        <tr>
                            <td class="bm-td-principal">
                                <b class="fs-6" style="color:var(--bm-vino)"><c:out value="${s.nombre}"/></b>
                                <c:if test="${not empty s.descripcion}"><div class="small bm-muted mt-1"><c:out value="${s.descripcion}"/></div></c:if>
                            </td>
                            <td data-label="Duración" class="bm-muted">${s.duracionMinutos} min</td>
                            <td data-label="Precio" class="text-end"><span class="bm-precio">S/ <fmt:formatNumber value="${s.precio}" minFractionDigits="2" maxFractionDigits="2"/></span></td>
                            <td class="text-end bm-td-accion">
                                <a class="btn btn-bm btn-bm-sm" href="${ctx}/reservas/nueva?servicioId=${s.id}"
                                   aria-label="Reservar <c:out value='${s.nombre}'/>">Reservar</a>
                            </td>
                        </tr>
                    </c:forEach>
                    </tbody>
                </table>
            </c:otherwise>
        </c:choose>
    </div>
    <p class="small bm-muted mt-3 mb-0">Los precios pueden cambiar; tu cita conserva el precio con el que reservaste.</p>
</main>

<%@ include file="/WEB-INF/views/fragments/footer.jspf" %>

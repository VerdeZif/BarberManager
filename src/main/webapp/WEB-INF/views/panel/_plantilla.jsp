<%@ page pageEncoding="UTF-8" %>
<%@ include file="/WEB-INF/views/fragments/taglibs.jspf" %>
<%--
  PLANTILLA para las vistas del panel interno (E2–E4). Copiar este archivo y cambiar:
    tituloPagina, menuActivo y el contenido de <main>.
  El Servlet debe validar sesión y rol ANTES de hacer forward aquí.
--%>
<c:set var="tituloPagina" value="Panel"/>
<c:set var="menuActivo" value="dashboard"/>
<%@ include file="/WEB-INF/views/fragments/head.jspf" %>
<%@ include file="/WEB-INF/views/fragments/panel-inicio.jspf" %>

        <span class="bm-ruta">/ruta/de/la/vista</span>
        <h1>Título de la vista</h1>
        <div class="mt-3"><%@ include file="/WEB-INF/views/fragments/alertas.jspf" %></div>

        <div class="bm-tabla-wrap bm-tabla-wrap--suave mt-3">
            <c:choose>
                <c:when test="${empty lista}">
                    <div class="bm-vacio"><b>No hay registros</b><span>Cuando se agreguen datos aparecerán aquí.</span></div>
                </c:when>
                <c:otherwise>
                    <table class="table bm-tabla bm-tabla-responsive">
                        <thead><tr><th scope="col">Nombre</th><th scope="col">Estado</th></tr></thead>
                        <tbody>
                        <c:forEach var="x" items="${lista}">
                            <tr><td class="bm-td-principal"><c:out value="${x.nombre}"/></td>
                                <td data-label="Estado"><span class="bm-chip bm-chip-${x.estado}"><c:out value="${x.estado}"/></span></td></tr>
                        </c:forEach>
                        </tbody>
                    </table>
                </c:otherwise>
            </c:choose>
        </div>

<%@ include file="/WEB-INF/views/fragments/panel-fin.jspf" %>

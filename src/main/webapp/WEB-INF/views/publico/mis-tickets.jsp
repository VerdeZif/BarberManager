
<%@ page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<%@ include file="/WEB-INF/views/fragments/taglibs.jspf" %>

<c:set var="tituloPagina" value="Mis tickets"/>
<c:set var="paginaActiva" value="mis-tickets"/>

<%@ include file="/WEB-INF/views/fragments/head.jspf" %>
<%@ include file="/WEB-INF/views/fragments/header-publico.jspf" %>

<main id="contenido" class="bm-contenedor py-5">

    <span class="bm-ruta">
        Mi cuenta / Mis tickets
    </span>

    <div class="d-flex flex-wrap justify-content-between
                align-items-center gap-3 mb-4">

        <div>
            <h1 class="mb-2">Mis tickets</h1>

            <p class="bm-muted mb-0">
                Consulta tus compras y servicios pagados.
            </p>
        </div>

        <a href="${ctx}/mis-citas"
           class="btn btn-bm-borde">
            Volver a mis citas
        </a>

    </div>

    <%@ include file="/WEB-INF/views/fragments/alertas.jspf" %>

    <section class="bm-card p-3 p-md-4">

        <h2 class="mb-3">
            Historial de tickets
        </h2>

        <c:choose>

            <c:when test="${empty tickets}">

                <div class="bm-vacio">

                    <b>
                        Todavía no tienes tickets disponibles
                    </b>

                    <span>
                        Los tickets aparecerán cuando una
                        venta asociada a tu cuenta esté
                        completamente pagada.
                    </span>

                </div>

            </c:when>

            <c:otherwise>

                <div class="bm-tabla-wrap bm-tabla-wrap--suave">

                    <table class="table bm-tabla bm-tabla-responsive">

                        <thead>
                            <tr>
                                <th>Ticket</th>
                                <th>Fecha</th>
                                <th>Total</th>
                                <th>Estado</th>
                                <th>Acción</th>
                            </tr>
                        </thead>

                        <tbody>

                            <c:forEach var="ticket"
                                       items="${tickets}">

                                <tr>

                                    <td class="bm-td-principal"
                                        data-label="Ticket">

                                        <strong>
                                            <c:out value="${ticket.numero}"/>
                                        </strong>

                                    </td>

                                    <td data-label="Fecha">

                                        <c:out value="${ticket.fecha}"/>

                                    </td>

                                    <td data-label="Total">

                                        S/
                                        <fmt:formatNumber
                                            value="${ticket.total}"
                                            minFractionDigits="2"
                                            maxFractionDigits="2"/>

                                    </td>

                                    <td data-label="Estado">

                                        <span class="bm-chip bm-chip-OK">
                                            Pagada
                                        </span>

                                    </td>

                                    <td data-label="Acción">

                                        <a class="btn btn-bm btn-bm-sm"
                                           href="${ctx}/mis-tickets/ver?id=${ticket.id}"
                                           target="_blank"
                                           rel="noopener">

                                            Ver / imprimir

                                        </a>

                                    </td>

                                </tr>

                            </c:forEach>

                        </tbody>

                    </table>

                </div>

            </c:otherwise>

        </c:choose>

        <p class="small bm-muted mt-3 mb-0">
            Solo se muestran ventas completamente pagadas
            asociadas a tu cuenta. Los tickets son comprobantes
            internos de un proyecto académico, no comprobantes
            tributarios.
        </p>

    </section>

</main>

<%@ include file="/WEB-INF/views/fragments/footer.jspf" %>

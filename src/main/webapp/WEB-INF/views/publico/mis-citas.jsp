
<%@ page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<%@ include file="/WEB-INF/views/fragments/taglibs.jspf" %>

<c:set var="tituloPagina" value="Mis citas"/>
<c:set var="paginaActiva" value="mis-citas"/>

<%@ include file="/WEB-INF/views/fragments/head.jspf" %>
<%@ include file="/WEB-INF/views/fragments/header-publico.jspf" %>

<main id="contenido" class="bm-contenedor py-5">

    <span class="bm-ruta">Mi cuenta / Mis citas</span>

    <div class="d-flex flex-wrap justify-content-between
                align-items-center gap-3 mb-4">

        <div>
            <h1 class="mb-2">Mis citas</h1>

            <p class="bm-muted mb-0">
                Consulta tus reservas y su estado.
            </p>
        </div>

        <a href="${ctx}/reservas/nueva"
           class="btn btn-bm">
            Nueva reserva
        </a>

    </div>

    <%@ include file="/WEB-INF/views/fragments/alertas.jspf" %>

    <section class="bm-card p-3 p-md-4">

        <h2 class="mb-3">Historial de reservas</h2>

        <c:choose>

            <c:when test="${empty citas}">

                <div class="bm-vacio">
                    <b>Todavía no tienes citas registradas</b>

                    <span>
                        Puedes reservar tu primera cita
                        seleccionando un servicio y barbero.
                    </span>
                </div>

            </c:when>

            <c:otherwise>

                <div class="bm-tabla-wrap bm-tabla-wrap--suave">

                    <table class="table bm-tabla bm-tabla-responsive">

                        <thead>
                            <tr>
                                <th>N.º</th>
                                <th>Servicio</th>
                                <th>Barbero</th>
                                <th>Fecha y hora</th>
                                <th>Precio</th>
                                <th>Estado</th>
                                <th>Acción</th>
                            </tr>
                        </thead>

                        <tbody>

                            <c:forEach var="cita" items="${citas}">

                                <tr>

                                    <td data-label="N.º">
                                        #<c:out value="${cita.id}"/>
                                    </td>

                                    <td class="bm-td-principal"
                                        data-label="Servicio">
                                        <strong>
                                            <c:out value="${cita.servicio}"/>
                                        </strong>
                                    </td>

                                    <td data-label="Barbero">
                                        <c:out value="${cita.barbero}"/>
                                    </td>

                                    <td data-label="Fecha">
                                        <c:out value="${cita.fecha}"/>
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

                                    <td data-label="Acción">

                                        <c:choose>

                                            <c:when test="${cita.cancelable}">

                                                <form method="post"
                                                      action="${ctx}/mis-citas">

                                                    <input type="hidden"
                                                           name="csrfToken"
                                                           value="<c:out value='${sessionScope.csrfToken}'/>">

                                                    <input type="hidden"
                                                           name="accion"
                                                           value="cancelar">

                                                    <input type="hidden"
                                                           name="citaId"
                                                           value="${cita.id}">

                                                    <button type="submit"
                                                            class="btn btn-bm-borde btn-bm-sm">
                                                        Cancelar cita
                                                    </button>

                                                </form>

                                            </c:when>

                                            <c:otherwise>

                                                <span class="small bm-muted">
                                                    Sin acciones
                                                </span>

                                            </c:otherwise>

                                        </c:choose>

                                    </td>

                                </tr>

                            </c:forEach>

                        </tbody>

                    </table>

                </div>

            </c:otherwise>

        </c:choose>

        <p class="small bm-muted mt-3 mb-0">
            Puedes cancelar citas pendientes o confirmadas
            mientras no haya comenzado la atención.
        </p>

    </section>

</main>

<%@ include file="/WEB-INF/views/fragments/footer.jspf" %>

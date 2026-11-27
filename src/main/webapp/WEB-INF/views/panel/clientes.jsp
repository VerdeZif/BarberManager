
<%@ page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<%@ include file="/WEB-INF/views/fragments/taglibs.jspf" %>

<c:set var="tituloPagina" value="Gestión de clientes"/>
<c:set var="menuActivo" value="clientes"/>

<%@ include file="/WEB-INF/views/fragments/head.jspf" %>
<%@ include file="/WEB-INF/views/fragments/panel-inicio.jspf" %>

<span class="bm-ruta">Recepción / Clientes</span>

<h1 class="mt-2">Gestión de clientes</h1>

<p class="bm-muted mb-4">
    Registra, consulta y actualiza los clientes de BarberManager.
</p>

<%@ include file="/WEB-INF/views/fragments/alertas.jspf" %>

<section class="bm-card mb-4">

    <h2>
        ${empty seleccionado ? 'Registrar cliente' : 'Editar cliente'}
    </h2>

    <form method="post" action="${ctx}/recepcion/clientes">

        <input type="hidden" name="csrfToken"
               value="<c:out value='${sessionScope.csrfToken}'/>">

        <input type="hidden" name="accion"
               value="${empty seleccionado ? 'crear' : 'editar'}">

        <c:if test="${not empty seleccionado}">
            <input type="hidden" name="id"
                   value="${seleccionado.id}">
        </c:if>

        <div class="row g-3 mt-1">

            <div class="col-md-4">
                <label class="form-label">Nombres</label>

                <input type="text"
                       class="form-control"
                       name="nombres"
                       maxlength="100"
                       required
                       value="<c:out value='${seleccionado.nombres}'/>">
            </div>

            <div class="col-md-4">
                <label class="form-label">Apellidos</label>

                <input type="text"
                       class="form-control"
                       name="apellidos"
                       maxlength="100"
                       required
                       value="<c:out value='${seleccionado.apellidos}'/>">
            </div>

            <div class="col-md-4">
                <label class="form-label">Teléfono</label>

                <input type="tel"
                       class="form-control"
                       name="telefono"
                       maxlength="20"
                       value="<c:out value='${seleccionado.telefono}'/>">
            </div>

            <div class="col-12 d-flex gap-2">

                <button type="submit" class="btn btn-bm">
                    ${empty seleccionado ? 'Registrar cliente' : 'Guardar cambios'}
                </button>

                <c:if test="${not empty seleccionado}">
                    <a href="${ctx}/recepcion/clientes"
                       class="btn btn-bm-borde">
                        Cancelar
                    </a>
                </c:if>

            </div>

        </div>

    </form>

</section>

<section class="bm-card">

    <h2 class="mb-3">Clientes registrados</h2>

    <div class="bm-tabla-wrap bm-tabla-wrap--suave">

        <c:choose>

            <c:when test="${empty clientes}">
                <div class="bm-vacio">
                    <b>No hay clientes registrados</b>
                </div>
            </c:when>

            <c:otherwise>

                <table class="table bm-tabla bm-tabla-responsive">

                    <thead>
                        <tr>
                            <th>ID</th>
                            <th>Cliente</th>
                            <th>Teléfono</th>
                            <th>Estado</th>
                            <th>Acciones</th>
                        </tr>
                    </thead>

                    <tbody>

                        <c:forEach var="cliente" items="${clientes}">

                            <tr>

                                <td data-label="ID">
                                    <c:out value="${cliente.id}"/>
                                </td>

                                <td class="bm-td-principal">
                                    <strong>
                                        <c:out value="${cliente.nombres} ${cliente.apellidos}"/>
                                    </strong>
                                </td>

                                <td data-label="Teléfono">
                                    <c:out value="${cliente.telefono}"/>
                                </td>

                                <td data-label="Estado">
                                    <span class="bm-chip ${cliente.activo ? 'bm-chip-OK' : 'bm-chip-INACTIVO'}">
                                        ${cliente.activo ? 'Activo' : 'Inactivo'}
                                    </span>
                                </td>

                                <td data-label="Acciones">

                                    <div class="d-flex flex-wrap gap-2">

                                        <a class="btn btn-bm-borde btn-bm-sm"
                                           href="${ctx}/recepcion/clientes?editar=${cliente.id}">
                                            Editar
                                        </a>

                                        <form method="post"
                                              action="${ctx}/recepcion/clientes">

                                            <input type="hidden"
                                                   name="csrfToken"
                                                   value="<c:out value='${sessionScope.csrfToken}'/>">

                                            <input type="hidden"
                                                   name="accion"
                                                   value="estado">

                                            <input type="hidden"
                                                   name="id"
                                                   value="${cliente.id}">

                                            <input type="hidden"
                                                   name="activo"
                                                   value="${cliente.activo ? 'false' : 'true'}">

                                            <button type="submit"
                                                    class="btn btn-bm-borde btn-bm-sm">
                                                ${cliente.activo ? 'Desactivar' : 'Activar'}
                                            </button>

                                        </form>

                                    </div>

                                </td>

                            </tr>

                        </c:forEach>

                    </tbody>

                </table>

            </c:otherwise>

        </c:choose>

    </div>

    <p class="small bm-muted mt-3 mb-0">
        Se muestran hasta 200 clientes.
    </p>

</section>

<%@ include file="/WEB-INF/views/fragments/panel-fin.jspf" %>

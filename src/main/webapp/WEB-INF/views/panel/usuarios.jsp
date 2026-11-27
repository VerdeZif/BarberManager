
<%@ page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<%@ include file="/WEB-INF/views/fragments/taglibs.jspf" %>

<c:set var="tituloPagina" value="Gestión de usuarios"/>
<c:set var="menuActivo" value="usuarios"/>

<%@ include file="/WEB-INF/views/fragments/head.jspf" %>
<%@ include file="/WEB-INF/views/fragments/panel-inicio.jspf" %>

<span class="bm-ruta">
    Administración / Usuarios
</span>

<div class="mt-2 mb-4">
    <h1>Gestión de usuarios</h1>

    <p class="bm-muted">
        Consulta los usuarios registrados, sus roles
        y el estado de sus cuentas.
    </p>
</div>

<%@ include file="/WEB-INF/views/fragments/alertas.jspf" %>

<div class="bm-card p-3 p-md-4">

    <div class="d-flex flex-wrap justify-content-between
                align-items-center gap-2 mb-3">

        <div>
            <h2 class="mb-1">Usuarios registrados</h2>
            <p class="bm-muted mb-0">
                Se muestran hasta 100 registros.
            </p>
        </div>

        <span class="bm-chip bm-chip-OK">
            <c:out value="${fn:length(usuarios)}"/> usuarios
        </span>
    </div>

    <div class="bm-tabla-wrap bm-tabla-wrap--suave">

        <c:choose>

            <c:when test="${empty usuarios}">

                <div class="bm-vacio">
                    <b>No hay usuarios registrados</b>
                    <span>
                        Los usuarios aparecerán aquí
                        cuando se registren en el sistema.
                    </span>
                </div>

            </c:when>

            <c:otherwise>

                <table class="table bm-tabla bm-tabla-responsive">

                    <thead>
                        <tr>
                            <th scope="col">ID</th>
                            <th scope="col">Usuario</th>
                            <th scope="col">Correo electrónico</th>
                            <th scope="col">Rol</th>
                            <th scope="col">Estado</th>
                        </tr>
                    </thead>

                    <tbody>

                        <c:forEach var="usuario" items="${usuarios}">

                            <tr>
                                <td data-label="ID">
                                    <c:out value="${usuario.id}"/>
                                </td>

                                <td class="bm-td-principal"
                                    data-label="Usuario">
                                    <strong>
                                        <c:out value="${usuario.username}"/>
                                    </strong>
                                </td>

                                <td data-label="Correo">
                                    <c:out value="${usuario.email}"/>
                                </td>

                                <td data-label="Rol">
                                    <span class="bm-chip">
                                        <c:out value="${usuario.rol}"/>
                                    </span>
                                </td>

                                <td data-label="Estado">

                                    <c:choose>

                                        <c:when test="${usuario.activo}">
                                            <span class="bm-chip bm-chip-OK">
                                                Activo
                                            </span>
                                        </c:when>

                                        <c:otherwise>
                                            <span class="bm-chip bm-chip-INACTIVO">
                                                Inactivo
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

</div>

<%@ include file="/WEB-INF/views/fragments/panel-fin.jspf" %>

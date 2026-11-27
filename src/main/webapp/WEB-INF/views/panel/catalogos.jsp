
<%@ page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<%@ include file="/WEB-INF/views/fragments/taglibs.jspf" %>

<c:set var="tituloPagina"
       value="${moduloBarberos ? 'Barberos y horarios' : 'Servicios'}"/>

<c:set var="menuActivo"
       value="${moduloBarberos ? 'barberos' : 'servicios'}"/>

<c:set var="rutaModulo"
       value="${moduloBarberos ? '/admin/barberos' : '/admin/servicios'}"/>

<%@ include file="/WEB-INF/views/fragments/head.jspf" %>
<%@ include file="/WEB-INF/views/fragments/panel-inicio.jspf" %>

<span class="bm-ruta">Administración / ${tituloPagina}</span>

<h1 class="mt-2">${tituloPagina}</h1>

<p class="bm-muted mb-4">
    Administra los registros de BarberManager.
</p>

<%@ include file="/WEB-INF/views/fragments/alertas.jspf" %>

<section class="bm-card p-3 p-md-4 mb-4">

    <h2 class="mb-3">
        ${empty seleccionado ? 'Nuevo registro' : 'Editar registro'}
    </h2>

    <form method="post" action="${ctx}${rutaModulo}">

        <input type="hidden" name="csrfToken"
               value="<c:out value='${sessionScope.csrfToken}'/>">

        <c:if test="${not empty seleccionado}">
            <input type="hidden" name="id"
                   value="${seleccionado.id}">
        </c:if>

        <div class="row g-3">

            <c:choose>
                <c:when test="${moduloBarberos}">

                    <div class="col-md-6">
                        <label class="form-label">Nombres</label>
                        <input class="form-control" name="nombres"
                               maxlength="100" required
                               value="<c:out value='${seleccionado.nombres}'/>">
                    </div>

                    <div class="col-md-6">
                        <label class="form-label">Apellidos</label>
                        <input class="form-control" name="apellidos"
                               maxlength="100" required
                               value="<c:out value='${seleccionado.apellidos}'/>">
                    </div>

                    <div class="col-md-4">
                        <label class="form-label">Días laborales</label>
                        <input class="form-control"
                               name="diasLaborables"
                               maxlength="20"
                               placeholder="1,2,3,4,5,6"
                               required
                               value="<c:out value='${seleccionado.diasLaborables}'/>">
                        <div class="form-text">
                            1 = lunes; 7 = domingo. Separar con comas.
                        </div>
                    </div>

                    <div class="col-md-4">
                        <label class="form-label">Hora de inicio</label>
                        <input type="time" class="form-control"
                               name="horaInicio"
                               value="${seleccionado.horaInicio}"
                               required>
                    </div>

                    <div class="col-md-4">
                        <label class="form-label">Hora de fin</label>
                        <input type="time" class="form-control"
                               name="horaFin"
                               value="${seleccionado.horaFin}"
                               required>
                    </div>

                    <div class="col-md-4">
                        <label class="form-label">Comisión (%)</label>
                        <input type="number" class="form-control"
                               name="porcentajeComision"
                               min="0" max="100" step="0.01"
                               value="${seleccionado.porcentajeComision}"
                               required>
                    </div>

                </c:when>

                <c:otherwise>

                    <div class="col-md-6">
                        <label class="form-label">Nombre del servicio</label>
                        <input class="form-control" name="nombre"
                               maxlength="100" required
                               value="<c:out value='${seleccionado.nombre}'/>">
                    </div>

                    <div class="col-md-3">
                        <label class="form-label">Precio (S/)</label>
                        <input type="number" class="form-control"
                               name="precio" min="0" step="0.01"
                               required
                               value="${seleccionado.precio}">
                    </div>

                    <div class="col-md-3">
                        <label class="form-label">Duración (minutos)</label>
                        <input type="number" class="form-control"
                               name="duracionMinutos" min="1"
                               required
                               value="${seleccionado.duracionMinutos}">
                    </div>

                    <div class="col-12">
                        <label class="form-label">Descripción</label>
                        <textarea class="form-control"
                                  name="descripcion" rows="3"><c:out value="${seleccionado.descripcion}"/></textarea>
                    </div>

                </c:otherwise>
            </c:choose>

            <div class="col-12">
                <label class="d-flex align-items-center gap-2">
                    <input type="checkbox"
                           name="activo"
                           value="true"
                           ${empty seleccionado or seleccionado.activo ? 'checked' : ''}>
                    Registro activo
                </label>
            </div>

            <div class="col-12 d-flex flex-wrap gap-2">
                <button class="btn btn-bm" type="submit">
                    ${empty seleccionado ? 'Registrar' : 'Guardar cambios'}
                </button>

                <c:if test="${not empty seleccionado}">
                    <a class="btn btn-bm-borde"
                       href="${ctx}${rutaModulo}">
                        Cancelar edición
                    </a>
                </c:if>
            </div>

        </div>
    </form>
</section>

<section class="bm-card p-3 p-md-4">

    <h2 class="mb-3">Registros existentes</h2>

    <div class="bm-tabla-wrap bm-tabla-wrap--suave">

        <c:choose>
            <c:when test="${empty registros}">
                <div class="bm-vacio">
                    <b>No hay registros disponibles</b>
                </div>
            </c:when>

            <c:otherwise>

                <table class="table bm-tabla bm-tabla-responsive">

                    <thead>
                        <c:choose>
                            <c:when test="${moduloBarberos}">
                                <tr>
                                    <th>Barbero</th>
                                    <th>Días</th>
                                    <th>Horario</th>
                                    <th>Comisión</th>
                                    <th>Estado</th>
                                    <th>Acción</th>
                                </tr>
                            </c:when>
                            <c:otherwise>
                                <tr>
                                    <th>Servicio</th>
                                    <th>Precio</th>
                                    <th>Duración</th>
                                    <th>Estado</th>
                                    <th>Acción</th>
                                </tr>
                            </c:otherwise>
                        </c:choose>
                    </thead>

                    <tbody>
                        <c:forEach var="r" items="${registros}">
                            <tr>

                                <c:choose>
                                    <c:when test="${moduloBarberos}">

                                        <td class="bm-td-principal">
                                            <strong>
                                                <c:out value="${r.nombres} ${r.apellidos}"/>
                                            </strong>
                                        </td>

                                        <td data-label="Días">
                                            <c:out value="${r.diasLaborables}"/>
                                        </td>

                                        <td data-label="Horario">
                                            <c:out value="${r.horaInicio}"/> –
                                            <c:out value="${r.horaFin}"/>
                                        </td>

                                        <td data-label="Comisión">
                                            <fmt:formatNumber
                                                value="${r.porcentajeComision}"
                                                minFractionDigits="2"
                                                maxFractionDigits="2"/> %
                                        </td>

                                    </c:when>

                                    <c:otherwise>

                                        <td class="bm-td-principal">
                                            <strong>
                                                <c:out value="${r.nombre}"/>
                                            </strong>
                                        </td>

                                        <td data-label="Precio">
                                            S/ <fmt:formatNumber
                                                value="${r.precio}"
                                                minFractionDigits="2"
                                                maxFractionDigits="2"/>
                                        </td>

                                        <td data-label="Duración">
                                            ${r.duracionMinutos} min
                                        </td>

                                    </c:otherwise>
                                </c:choose>

                                <td data-label="Estado">
                                    <c:choose>
                                        <c:when test="${r.activo}">
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

                                <td data-label="Acción">
                                    <a class="btn btn-bm-borde btn-bm-sm"
                                       href="${ctx}${rutaModulo}?editar=${r.id}">
                                        Editar
                                    </a>
                                </td>

                            </tr>
                        </c:forEach>
                    </tbody>

                </table>

            </c:otherwise>
        </c:choose>

    </div>
</section>

<%@ include file="/WEB-INF/views/fragments/panel-fin.jspf" %>


<%@ page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<%@ include file="/WEB-INF/views/fragments/taglibs.jspf" %>

<c:set var="tituloPagina" value="Inventario"/>
<c:set var="menuActivo" value="inventario"/>

<%@ include file="/WEB-INF/views/fragments/head.jspf" %>
<%@ include file="/WEB-INF/views/fragments/panel-inicio.jspf" %>

<span class="bm-ruta">Recepción / Inventario</span>

<h1 class="mt-2">Gestión de inventario</h1>

<p class="bm-muted mb-4">
    Administra productos, existencias y movimientos.
</p>

<%@ include file="/WEB-INF/views/fragments/alertas.jspf" %>

<section class="bm-card mb-4">

    <h2 class="mb-3">
        ${empty seleccionado ? 'Registrar producto' : 'Editar producto'}
    </h2>

    <form method="post" action="${ctx}/recepcion/inventario">

        <input type="hidden" name="csrfToken"
               value="<c:out value='${sessionScope.csrfToken}'/>">

        <input type="hidden" name="accion"
               value="guardarProducto">

        <c:if test="${not empty seleccionado}">
            <input type="hidden" name="id"
                   value="${seleccionado.id}">
        </c:if>

        <div class="row g-3">

            <div class="col-md-6">
                <label class="form-label">Producto</label>
                <input class="form-control"
                       name="nombre"
                       maxlength="100"
                       required
                       value="<c:out value='${seleccionado.nombre}'/>">
            </div>

            <div class="col-md-6">
                <label class="form-label">Categoría</label>
                <input class="form-control"
                       name="categoria"
                       maxlength="50"
                       required
                       value="<c:out value='${seleccionado.categoria}'/>">
            </div>

            <div class="col-md-4">
                <label class="form-label">Precio de venta (S/)</label>
                <input type="number"
                       class="form-control"
                       name="precio"
                       min="0"
                       step="0.01"
                       required
                       value="${seleccionado.precioVenta}">
            </div>

            <div class="col-md-4">
                <label class="form-label">Stock mínimo</label>
                <input type="number"
                       class="form-control"
                       name="stockMinimo"
                       min="0"
                       step="1"
                       required
                       value="${empty seleccionado ? 0 : seleccionado.stockMinimo}">
            </div>

            <div class="col-md-4">
                <label class="form-label d-block">Estado</label>
                <label class="d-flex align-items-center gap-2">
                    <input type="checkbox"
                           name="activo"
                           value="true"
                           ${empty seleccionado or seleccionado.activo ? 'checked' : ''}>
                    Producto activo
                </label>
            </div>

            <div class="col-12">
                <button class="btn btn-bm" type="submit">
                    ${empty seleccionado ? 'Registrar producto' : 'Guardar cambios'}
                </button>

                <c:if test="${not empty seleccionado}">
                    <a href="${ctx}/recepcion/inventario"
                       class="btn btn-bm-borde">
                        Cancelar edición
                    </a>
                </c:if>
            </div>

        </div>
    </form>
</section>

<section class="bm-card mb-4">

    <h2 class="mb-3">Registrar movimiento de stock</h2>

    <form method="post" action="${ctx}/recepcion/inventario">

        <input type="hidden" name="csrfToken"
               value="<c:out value='${sessionScope.csrfToken}'/>">

        <input type="hidden" name="accion"
               value="registrarMovimiento">

        <div class="row g-3">

            <div class="col-md-5">
                <label class="form-label">Producto</label>

                <select class="form-select"
                        name="productoId" required>

                    <option value="">Selecciona un producto</option>

                    <c:forEach var="p" items="${productos}">
                        <c:if test="${p.activo}">
                            <option value="${p.id}">
                                <c:out value="${p.nombre}"/>
                                — Stock: ${p.stockActual}
                            </option>
                        </c:if>
                    </c:forEach>

                </select>
            </div>

            <div class="col-md-4">
                <label class="form-label">Tipo de movimiento</label>

                <select class="form-select" name="tipo" required>
                    <option value="ENTRADA">Entrada</option>
                    <option value="SALIDA">Salida</option>
                    <option value="AJUSTE_ENTRADA">Ajuste de entrada</option>
                    <option value="AJUSTE_SALIDA">Ajuste de salida</option>
                </select>
            </div>

            <div class="col-md-3">
                <label class="form-label">Cantidad</label>
                <input type="number"
                       class="form-control"
                       name="cantidad"
                       min="1"
                       step="1"
                       required>
            </div>

            <div class="col-12">
                <label class="form-label">Motivo</label>
                <input class="form-control"
                       name="motivo"
                       maxlength="255"
                       placeholder="Ejemplo: reposición de productos">
            </div>

            <div class="col-12">
                <button type="submit" class="btn btn-bm">
                    Registrar movimiento
                </button>
            </div>

        </div>

    </form>
</section>

<section class="bm-card mb-4">

    <h2 class="mb-3">Productos registrados</h2>

    <div class="bm-tabla-wrap bm-tabla-wrap--suave">

        <c:choose>

            <c:when test="${empty productos}">
                <div class="bm-vacio">
                    <b>No hay productos registrados</b>
                </div>
            </c:when>

            <c:otherwise>

                <table class="table bm-tabla bm-tabla-responsive">

                    <thead>
                        <tr>
                            <th>Producto</th>
                            <th>Categoría</th>
                            <th>Precio</th>
                            <th>Stock</th>
                            <th>Mínimo</th>
                            <th>Estado</th>
                            <th>Acción</th>
                        </tr>
                    </thead>

                    <tbody>
                        <c:forEach var="p" items="${productos}">

                            <tr>

                                <td class="bm-td-principal">
                                    <strong><c:out value="${p.nombre}"/></strong>
                                </td>

                                <td data-label="Categoría">
                                    <c:out value="${p.categoria}"/>
                                </td>

                                <td data-label="Precio">
                                    S/
                                    <fmt:formatNumber
                                        value="${p.precioVenta}"
                                        minFractionDigits="2"
                                        maxFractionDigits="2"/>
                                </td>

                                <td data-label="Stock">
                                    <span class="bm-chip ${p.stockActual <= p.stockMinimo ? 'bm-chip-STOCK_BAJO' : 'bm-chip-OK'}">
                                        ${p.stockActual}
                                    </span>
                                </td>

                                <td data-label="Mínimo">
                                    ${p.stockMinimo}
                                </td>

                                <td data-label="Estado">
                                    <span class="bm-chip ${p.activo ? 'bm-chip-OK' : 'bm-chip-INACTIVO'}">
                                        ${p.activo ? 'Activo' : 'Inactivo'}
                                    </span>
                                </td>

                                <td data-label="Acción">
                                    <a class="btn btn-bm-borde btn-bm-sm"
                                       href="${ctx}/recepcion/inventario?editar=${p.id}">
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

<section class="bm-card">

    <h2 class="mb-3">Últimos movimientos</h2>

    <div class="bm-tabla-wrap bm-tabla-wrap--suave">

        <c:choose>

            <c:when test="${empty movimientos}">
                <div class="bm-vacio">
                    <b>No hay movimientos registrados</b>
                </div>
            </c:when>

            <c:otherwise>

                <table class="table bm-tabla bm-tabla-responsive">

                    <thead>
                        <tr>
                            <th>Producto</th>
                            <th>Tipo</th>
                            <th>Cantidad</th>
                            <th>Responsable</th>
                            <th>Motivo</th>
                        </tr>
                    </thead>

                    <tbody>

                        <c:forEach var="m" items="${movimientos}">

                            <tr>

                                <td class="bm-td-principal">
                                    <c:out value="${m.producto.nombre}"/>
                                </td>

                                <td data-label="Tipo">
                                    <c:out value="${m.tipo}"/>
                                </td>

                                <td data-label="Cantidad">
                                    ${m.cantidad}
                                </td>

                                <td data-label="Responsable">
                                    <c:out value="${m.usuario.username}"/>
                                </td>

                                <td data-label="Motivo">
                                    <c:out value="${m.motivo}"/>
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

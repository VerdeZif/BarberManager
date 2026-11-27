
<%@ page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<%@ include file="/WEB-INF/views/fragments/taglibs.jspf" %>

<c:set var="tituloPagina" value="Ventas y cobros"/>
<c:set var="menuActivo" value="ventas"/>

<%@ include file="/WEB-INF/views/fragments/head.jspf" %>
<%@ include file="/WEB-INF/views/fragments/panel-inicio.jspf" %>

<span class="bm-ruta">Recepción / Ventas y cobros</span>

<h1 class="mt-2">Ventas y cobros</h1>

<p class="bm-muted mb-4">
    Registra ventas de servicios y productos,
    pagos simulados y tickets internos.
</p>

<%@ include file="/WEB-INF/views/fragments/alertas.jspf" %>

<section class="bm-card p-3 p-md-4 mb-4">

    <h2 class="mb-3">Registrar nueva venta</h2>

    <form method="post" action="${ctx}/recepcion/ventas">

        <input type="hidden" name="csrfToken"
               value="<c:out value='${sessionScope.csrfToken}'/>">

        <div class="row g-3">

            <div class="col-md-6">
                <label class="form-label">Cliente (opcional)</label>

                <select class="form-select" name="clienteId">
                    <option value="">Público general / cliente de la atención</option>

                    <c:forEach var="cliente" items="${clientes}">
                        <option value="${cliente.id}">
                            <c:out value="${cliente.nombres} ${cliente.apellidos}"/>
                        </option>
                    </c:forEach>
                </select>
            </div>

            <div class="col-md-6">
                <label class="form-label">
                    Atención finalizada (opcional)
                </label>

                <select class="form-select" name="atencionId">
                    <option value="">Solo venta de productos</option>

                    <c:forEach var="a" items="${atenciones}">
                        <option value="${a.id}">
                            <c:out value="${a.etiqueta}"/>
                        </option>
                    </c:forEach>
                </select>

                <div class="form-text">
                    Si seleccionas una atención, deja el cliente
                    sin seleccionar. El sistema usará el cliente
                    de esa cita.
                </div>
            </div>

            <div class="col-md-8">
                <label class="form-label">
                    Producto adicional (opcional)
                </label>

                <select class="form-select" name="productoId">
                    <option value="">Sin producto</option>

                    <c:forEach var="p" items="${productos}">
                        <option value="${p.id}">
                            <c:out value="${p.nombre}"/>
                            — S/
                            <fmt:formatNumber
                                value="${p.precioVenta}"
                                minFractionDigits="2"
                                maxFractionDigits="2"/>
                            — Stock: ${p.stockActual}
                        </option>
                    </c:forEach>
                </select>
            </div>

            <div class="col-md-4">
                <label class="form-label">Cantidad del producto</label>

                <input type="number"
                       class="form-control"
                       name="cantidad"
                       min="1"
                       step="1"
                       placeholder="Ejemplo: 2">
            </div>

            <div class="col-12">
                <p class="small bm-muted">
                    Selecciona una atención finalizada, un producto
                    o ambos. En esta primera versión se admite un
                    tipo de producto por venta.
                </p>

                <button type="submit" class="btn btn-bm">
                    Registrar venta
                </button>
            </div>

        </div>

    </form>

</section>

<section class="bm-card p-3 p-md-4">

    <h2 class="mb-3">Últimas ventas registradas</h2>

    <c:choose>

        <c:when test="${empty ventas}">
            <div class="bm-vacio">
                <b>Todavía no hay ventas registradas</b>
                <span>
                    Crea una venta desde el formulario superior.
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
                            <th>Cliente</th>
                            <th>Total</th>
                            <th>Pagado</th>
                            <th>Saldo</th>
                            <th>Estado</th>
                            <th>Cobrar / Ticket</th>
                        </tr>
                    </thead>

                    <tbody>

                        <c:forEach var="venta" items="${ventas}">

                            <tr>

                                <td data-label="Ticket">
                                    <strong>
                                        <c:out value="${venta.numero}"/>
                                    </strong>
                                </td>

                                <td data-label="Fecha">
                                    <c:out value="${venta.fecha}"/>
                                </td>

                                <td data-label="Cliente">
                                    <c:out value="${venta.cliente}"/>
                                </td>

                                <td data-label="Total">
                                    S/
                                    <fmt:formatNumber
                                        value="${venta.total}"
                                        minFractionDigits="2"
                                        maxFractionDigits="2"/>
                                </td>

                                <td data-label="Pagado">
                                    S/
                                    <fmt:formatNumber
                                        value="${venta.pagado}"
                                        minFractionDigits="2"
                                        maxFractionDigits="2"/>
                                </td>

                                <td data-label="Saldo">
                                    S/
                                    <fmt:formatNumber
                                        value="${venta.saldo}"
                                        minFractionDigits="2"
                                        maxFractionDigits="2"/>
                                </td>

                                <td data-label="Estado">
                                    <span class="bm-chip">
                                        <c:out value="${venta.estado}"/>
                                    </span>
                                </td>

                                <td data-label="Acciones">

                                    <c:if test="${venta.pendiente}">

                                        <form method="post"
                                              action="${ctx}/recepcion/ventas/pago"
                                              class="d-flex flex-column gap-2">

                                            <input type="hidden"
                                                   name="csrfToken"
                                                   value="<c:out value='${sessionScope.csrfToken}'/>">

                                            <input type="hidden"
                                                   name="ventaId"
                                                   value="${venta.id}">

                                            <select name="metodo"
                                                    class="form-select form-select-sm"
                                                    aria-label="Método de pago"
                                                    required>
                                                <option value="EFECTIVO">Efectivo</option>
                                                <option value="YAPE">Yape (simulado)</option>
                                                <option value="PLIN">Plin (simulado)</option>
                                                <option value="TARJETA">Tarjeta (simulado)</option>
                                            </select>

                                            <input type="number"
                                                   name="monto"
                                                   class="form-control form-control-sm"
                                                   aria-label="Monto del pago"
                                                   min="0.01"
                                                   max="${venta.saldo}"
                                                   step="0.01"
                                                   value="${venta.saldo}"
                                                   required>

                                            <button class="btn btn-bm btn-bm-sm"
                                                    type="submit">
                                                Registrar pago
                                            </button>

                                        </form>

                                    </c:if>

                                    <c:if test="${venta.pagada}">
                                        <a class="btn btn-bm-borde btn-bm-sm"
                                           href="${ctx}/recepcion/ventas/ticket?id=${venta.id}"
                                           target="_blank"
                                           rel="noopener">
                                            Ver / imprimir ticket
                                        </a>
                                    </c:if>

                                </td>

                            </tr>

                        </c:forEach>

                    </tbody>

                </table>

            </div>

            <p class="small bm-muted mt-3 mb-0">
                Se muestran las últimas 100 ventas.
                Todos los pagos registrados son simulados.
            </p>

        </c:otherwise>

    </c:choose>

</section>

<%@ include file="/WEB-INF/views/fragments/panel-fin.jspf" %>


<%@ page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<%@ include file="/WEB-INF/views/fragments/taglibs.jspf" %>

<c:set var="tituloPagina" value="Reportes"/>
<c:set var="menuActivo" value="reportes"/>

<%@ include file="/WEB-INF/views/fragments/head.jspf" %>
<%@ include file="/WEB-INF/views/fragments/panel-inicio.jspf" %>

<span class="bm-ruta">Administración / Reportes</span>

<h1 class="mt-2">Reportes generales</h1>

<p class="bm-muted mb-4">
    Consulta las estadísticas de BarberManager
    utilizando los registros reales del sistema.
</p>

<%@ include file="/WEB-INF/views/fragments/alertas.jspf" %>

<section class="bm-card p-3 p-md-4 mb-4">

    <h2 class="mb-3">Filtrar por período</h2>

    <form method="get"
          action="${ctx}/admin/reportes"
          class="row g-3 align-items-end">

        <div class="col-md-4">
            <label class="form-label" for="desde">
                Fecha inicial
            </label>

            <input type="date"
                   id="desde"
                   name="desde"
                   class="form-control"
                   value="${desde}"
                   required>
        </div>

        <div class="col-md-4">
            <label class="form-label" for="hasta">
                Fecha final
            </label>

            <input type="date"
                   id="hasta"
                   name="hasta"
                   class="form-control"
                   value="${hasta}"
                   required>
        </div>

        <div class="col-md-4">
            <button class="btn btn-bm" type="submit">
                Generar reporte
            </button>
        </div>

    </form>

    <p class="small bm-muted mt-3 mb-0">
        Período seleccionado:
        <strong><c:out value="${desde}"/></strong>
        al
        <strong><c:out value="${hasta}"/></strong>.
    </p>

</section>


<section class="bm-card p-3 p-md-4 mb-4">

    <h2 class="mb-3">Exportar reportes</h2>

    <p class="bm-muted">
        Descarga los datos del sistema en formato CSV
        para abrirlos en Microsoft Excel.
    </p>

    <form method="get"
          action="${ctx}/admin/reportes/exportar"
          class="row g-3 align-items-end">

        <input type="hidden"
               name="desde"
               value="<c:out value='${desde}'/>">

        <input type="hidden"
               name="hasta"
               value="<c:out value='${hasta}'/>">

        <div class="col-md-8">

            <label class="form-label"
                   for="tipoReporte">
                Seleccionar reporte
            </label>

            <select id="tipoReporte"
                    name="tipo"
                    class="form-select"
                    required>

                <option value="ventas">
                    Ventas pagadas
                </option>

                <option value="cobros">
                    Cobros por método de pago
                </option>

                <option value="citas">
                    Historial de citas
                </option>

                <option value="comisiones">
                    Comisiones generadas
                </option>

                <option value="servicios">
                    Top 5 servicios solicitados
                </option>

                <option value="stock">
                    Productos con stock bajo
                </option>

            </select>

        </div>

        <div class="col-md-4">

            <button type="submit"
                    class="btn btn-bm w-100">
                Descargar CSV
            </button>

        </div>

    </form>

    <p class="small bm-muted mt-3 mb-0">
        La descarga utilizará el período seleccionado:
        <strong><c:out value="${desde}"/></strong>
        al
        <strong><c:out value="${hasta}"/></strong>.
        El reporte de stock bajo utiliza las
        existencias actuales.
    </p>

    <hr class="my-4">

    <div class="d-flex flex-wrap justify-content-between
                align-items-center gap-3">

        <div>
            <h3 class="mb-1">Reporte Excel completo</h3>

            <p class="bm-muted mb-0">
                Descarga todos los reportes en un archivo
                Excel con seis hojas independientes.
            </p>
        </div>

        <form method="get"
              action="${ctx}/admin/reportes/excel">

            <input type="hidden"
                   name="desde"
                   value="<c:out value='${desde}'/>">

            <input type="hidden"
                   name="hasta"
                   value="<c:out value='${hasta}'/>">

            <button type="submit" class="btn btn-bm">
                Descargar Excel completo
            </button>

        </form>

    </div>


</section>


<div class="row g-3 mb-4">

    <div class="col-sm-6 col-xl-3">
        <div class="bm-card p-4 h-100">

            <p class="bm-muted mb-2">
                Ventas pagadas
            </p>

            <h2 class="mb-0">
                S/
                <fmt:formatNumber
                    value="${totalVentas}"
                    minFractionDigits="2"
                    maxFractionDigits="2"/>
            </h2>

            <span class="small bm-muted">
                Ventas creadas en el período
                y actualmente pagadas
            </span>

        </div>
    </div>

    <div class="col-sm-6 col-xl-3">
        <div class="bm-card p-4 h-100">

            <p class="bm-muted mb-2">
                Cobros simulados
            </p>

            <h2 class="mb-0">
                S/
                <fmt:formatNumber
                    value="${totalCobros}"
                    minFractionDigits="2"
                    maxFractionDigits="2"/>
            </h2>

            <span class="small bm-muted">
                Pagos registrados en el período
            </span>

        </div>
    </div>

    <div class="col-sm-6 col-xl-3">
        <div class="bm-card p-4 h-100">

            <p class="bm-muted mb-2">
                Comisiones generadas
            </p>

            <h2 class="mb-0">
                S/
                <fmt:formatNumber
                    value="${totalComisiones}"
                    minFractionDigits="2"
                    maxFractionDigits="2"/>
            </h2>

            <span class="small bm-muted">
                Generadas durante el período
            </span>

        </div>
    </div>

    <div class="col-sm-6 col-xl-3">
        <div class="bm-card p-4 h-100">

            <p class="bm-muted mb-2">
                Productos con stock bajo
            </p>

            <h2 class="mb-0">
                <c:out value="${cantidadStockBajo}"/>
            </h2>

            <span class="small bm-muted">
                Situación actual del inventario
            </span>

        </div>
    </div>

</div>

<div class="row g-3 mb-4">

    <div class="col-lg-6">

        <section class="bm-card p-3 p-md-4 h-100">

            <h2 class="mb-3">Citas por estado</h2>

            <div class="bm-tabla-wrap">

                <table class="table bm-tabla">
                    <thead>
                        <tr>
                            <th>Estado</th>
                            <th>Cantidad</th>
                        </tr>
                    </thead>

                    <tbody>
                        <c:forEach var="c" items="${estadosCitas}">
                            <tr>
                                <td>
                                    <span class="bm-chip">
                                        <c:out value="${c.estado}"/>
                                    </span>
                                </td>
                                <td>
                                    <c:out value="${c.cantidad}"/>
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>

                <c:if test="${empty estadosCitas}">
                    <p class="bm-muted">
                        No hay citas en este período.
                    </p>
                </c:if>

            </div>

        </section>

    </div>

    <div class="col-lg-6">

        <section class="bm-card p-3 p-md-4 h-100">

            <h2 class="mb-3">Cobros por método</h2>

            <div class="bm-tabla-wrap">

                <table class="table bm-tabla">
                    <thead>
                        <tr>
                            <th>Método</th>
                            <th>Pagos</th>
                            <th>Total</th>
                        </tr>
                    </thead>

                    <tbody>
                        <c:forEach var="p" items="${metodosPago}">
                            <tr>
                                <td>
                                    <c:out value="${p.metodo}"/>
                                </td>
                                <td>
                                    <c:out value="${p.cantidad}"/>
                                </td>
                                <td>
                                    S/
                                    <fmt:formatNumber
                                        value="${p.monto}"
                                        minFractionDigits="2"
                                        maxFractionDigits="2"/>
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>

                <c:if test="${empty metodosPago}">
                    <p class="bm-muted">
                        No hay pagos registrados.
                    </p>
                </c:if>

            </div>

        </section>

    </div>

</div>

<div class="row g-3 mb-4">

    <div class="col-lg-6">

        <section class="bm-card p-3 p-md-4 h-100">

            <h2 class="mb-3">
                Top 5 servicios solicitados
            </h2>

            <div class="bm-tabla-wrap">

                <table class="table bm-tabla">
                    <thead>
                        <tr>
                            <th>Servicio</th>
                            <th>Reservas</th>
                        </tr>
                    </thead>

                    <tbody>
                        <c:forEach var="s"
                                   items="${topServicios}">
                            <tr>
                                <td>
                                    <c:out value="${s.nombre}"/>
                                </td>
                                <td>
                                    <c:out value="${s.cantidad}"/>
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>

                <c:if test="${empty topServicios}">
                    <p class="bm-muted">
                        No hay servicios solicitados
                        en el período.
                    </p>
                </c:if>

            </div>

        </section>

    </div>

    <div class="col-lg-6">

        <section class="bm-card p-3 p-md-4 h-100">

            <h2 class="mb-3">
                Productos con stock bajo
            </h2>

            <div class="bm-tabla-wrap">

                <table class="table bm-tabla">
                    <thead>
                        <tr>
                            <th>Producto</th>
                            <th>Actual</th>
                            <th>Mínimo</th>
                        </tr>
                    </thead>

                    <tbody>
                        <c:forEach var="p" items="${stockBajo}">
                            <tr>
                                <td>
                                    <c:out value="${p.nombre}"/>
                                </td>
                                <td>
                                    <c:out value="${p.stock}"/>
                                </td>
                                <td>
                                    <c:out value="${p.minimo}"/>
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>

                <c:if test="${empty stockBajo}">
                    <p class="bm-muted">
                        No hay productos activos
                        con stock bajo.
                    </p>
                </c:if>

            </div>

        </section>

    </div>

</div>

<p class="small bm-muted">
    Los datos financieros son simulados y tienen
    fines académicos. Las ventas se agrupan según
    su fecha de creación; los cobros, según su fecha
    de pago. Por ello, ambos totales pueden diferir.
</p>

<%@ include file="/WEB-INF/views/fragments/panel-fin.jspf" %>

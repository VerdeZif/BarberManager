<%@ page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<%@ include file="/WEB-INF/views/fragments/taglibs.jspf" %>
<%--
  GET /  — Inicio público
  Request: servicios  (List<Servicio> activos: id, nombre, descripcion, precio, duracionMinutos)
  Se muestran los 3 primeros en los bloques de precios. Si no llega la lista, se ve un estado vacío.
--%>
<c:set var="tituloPagina" value="Inicio"/>
<c:set var="paginaActiva" value="inicio"/>
<%@ include file="/WEB-INF/views/fragments/head.jspf" %>
<%@ include file="/WEB-INF/views/fragments/header-publico.jspf" %>

<main id="contenido" class="overflow-hidden">
    <div class="bm-contenedor"><%@ include file="/WEB-INF/views/fragments/alertas.jspf" %></div>

    <%-- HERO --%>
    <section class="bm-contenedor bm-hero">
        <h1>Fresh <span class="bm-hueco">cut</span><br>tu hora, tu estilo</h1>
        <div class="bm-sticker" aria-hidden="true">Reserva<br>en 1<br>minuto</div>

        <div class="row g-4 align-items-end mt-2">
            <div class="col-lg-4 d-flex flex-column gap-3">
                <p class="bm-hero-texto m-0">Cortes, barba y afeitado en el barrio. Elige barbero y horario libre desde el celular y llega directo a tu silla.</p>
                <div class="d-flex flex-wrap gap-2">
                    <a class="btn btn-bm" href="${ctx}/reservas/nueva">Reservar cita</a>
                    <a class="btn btn-bm-dorado" href="${ctx}/servicios">Ver precios</a>
                </div>
            </div>
            <div class="col-md-7 col-lg-5">
                <div class="bm-foto">
                    <img src="${ctx}/assets/img/barberia-local.jpg" alt="Sillón de barbero de cuero frente a un espejo en el local"
                         width="1100" height="1650" loading="eager">
                    <span class="bm-foto-tag">Tu silla te espera</span>
                </div>
            </div>
            <div class="col-md-5 col-lg-3">
                <div class="bm-card">
                    <h2 class="m-0" style="font-size:32px">Horario</h2>
                    <p class="m-0 mt-1" style="line-height:1.7">Lun – Sáb · 9:00 a 18:00<br>Pago en el local: efectivo, Yape, Plin o tarjeta</p>
                </div>
            </div>
        </div>
    </section>

    <%-- FRANJA INCLINADA con los nombres de los servicios --%>
    <div class="bm-franja-wrap" aria-hidden="true">
        <div class="bm-franja">
            <div class="bm-franja-track">
                <%-- Se repite un número par de veces para que la animación (-50%) sea continua --%>
                <c:forEach begin="1" end="4">
                    <c:choose>
                        <c:when test="${not empty servicios}">
                            <c:forEach var="s" items="${servicios}" begin="0" end="5">
                                <span><c:out value="${fn:toUpperCase(s.nombre)}"/></span><span class="bm-estrella">✦</span>
                            </c:forEach>
                        </c:when>
                        <c:otherwise>
                            <span>CORTE</span><span class="bm-estrella">✦</span><span>BARBA</span><span class="bm-estrella">✦</span><span>AFEITADO</span><span class="bm-estrella">✦</span>
                        </c:otherwise>
                    </c:choose>
                </c:forEach>
            </div>
        </div>
    </div>

    <%-- BENTO DE PRECIOS --%>
    <section class="bm-contenedor pt-4 pb-3" aria-labelledby="tituloPrecios">
        <h2 id="tituloPrecios" class="visually-hidden">Servicios destacados</h2>
        <div class="bm-bento">
            <c:forEach var="s" items="${servicios}" begin="0" end="2" varStatus="st">
                <c:set var="claseBloque" value="${st.index == 0 ? 'bm-b-grande' : (st.index == 1 ? 'bm-b-dorado' : 'bm-b-blanco')}"/>
                <a class="${claseBloque}" href="${ctx}/reservas/nueva?servicioId=${s.id}">
                    <c:if test="${st.index == 0}"><span class="bm-etiqueta">DESTACADO</span></c:if>
                    <b class="bm-b-nombre"><c:out value="${s.nombre}"/></b>
                    <span class="bm-b-desc">
                        <c:if test="${not empty s.descripcion}"><c:out value="${s.descripcion}"/> · </c:if>${s.duracionMinutos} min
                    </span>
                    <b class="bm-b-precio">S/<fmt:formatNumber value="${s.precio}" maxFractionDigits="2" minFractionDigits="0"/></b>
                </a>
            </c:forEach>
            <c:if test="${empty servicios}">
                <div class="bm-b-blanco bm-vacio" style="grid-column: 1 / -1">
                    <b>Pronto publicaremos nuestros precios</b>
                    <span>Mientras tanto, puedes reservar tu cita y pagar en el local.</span>
                </div>
            </c:if>
            <div class="bm-b-arena">
                <div>
                    <b class="d-block" style="font-family:var(--bm-titulo);font-size:50px;color:var(--bm-vino);line-height:.95">¿Primera vez?</b>
                    <span>Crea tu cuenta y revisa tus citas y tickets cuando quieras.</span>
                </div>
                <c:choose>
                    <c:when test="${empty usuarioSesion}"><a class="btn btn-bm" href="${ctx}/registro">Crear cuenta</a></c:when>
                    <c:otherwise><a class="btn btn-bm" href="${ctx}/servicios">Ver todos los servicios</a></c:otherwise>
                </c:choose>
            </div>
        </div>
    </section>

    <%-- PASOS --%>
    <section class="bm-contenedor py-5" aria-labelledby="tituloPasos">
        <h2 id="tituloPasos" style="font-size:64px;line-height:.9">Reserva en 3 pasos</h2>
        <div class="row g-3 mt-1">
            <div class="col-md-4"><div class="bm-card h-100"><span class="bm-paso-num">01</span><b class="d-block fs-5 mt-1">Elige servicio y barbero</b><p class="m-0 mt-1 bm-muted">Un servicio por cita, con su precio y duración.</p></div></div>
            <div class="col-md-4"><div class="bm-card h-100"><span class="bm-paso-num">02</span><b class="d-block fs-5 mt-1">Escoge un horario libre</b><p class="m-0 mt-1 bm-muted">Solo ves las horas disponibles de tu barbero.</p></div></div>
            <div class="col-md-4"><div class="bm-card h-100"><span class="bm-paso-num">03</span><b class="d-block fs-5 mt-1">Llega y paga en el local</b><p class="m-0 mt-1 bm-muted">Revisa o cancela tu cita en “Mis citas”.</p></div></div>
        </div>
    </section>
</main>

<%@ include file="/WEB-INF/views/fragments/footer.jspf" %>

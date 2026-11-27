<%@ page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<%@ include file="/WEB-INF/views/fragments/taglibs.jspf" %>
<%--
  GET /barberos — Lista pública de barberos
  Request: barberos (List<Barbero> activos: id, nombres, apellidos,
           diasLaborables ["1,2,3,4,5,6" — 1 = lunes ... 7 = domingo], horaInicio, horaFin [LocalTime])
  Solo se muestran datos públicos (no se muestra comisión ni datos de usuario).
--%>
<c:set var="tituloPagina" value="Nuestros barberos"/>
<c:set var="paginaActiva" value="barberos"/>
<%@ include file="/WEB-INF/views/fragments/head.jspf" %>
<%@ include file="/WEB-INF/views/fragments/header-publico.jspf" %>

<main id="contenido" class="bm-contenedor pt-4 pb-5" style="max-width:1180px">
    <%@ include file="/WEB-INF/views/fragments/alertas.jspf" %>
    <div class="mb-4">
        <span class="bm-ruta">El equipo</span>
        <h1 class="m-0 mt-1" style="font-size:clamp(40px,6vw,56px)">Nuestros barberos</h1>
        <p class="m-0 bm-muted">Elige con quién atenderte al reservar. Los días marcados son los que trabaja cada uno.</p>
    </div>

    <c:choose>
        <c:when test="${empty barberos}">
            <div class="bm-card bm-vacio">
                <b>Aún no hay barberos disponibles</b>
                <span>Vuelve pronto para reservar tu cita.</span>
            </div>
        </c:when>
        <c:otherwise>
            <div class="row g-3">
                <c:forEach var="b" items="${barberos}">
                    <c:set var="dias" value=",${fn:replace(b.diasLaborables, ' ', '')},"/>
                    <div class="col-sm-6 col-lg-4">
                        <article class="bm-barbero">
                            <div class="bm-barbero-foto">
                                <span class="bm-iniciales" aria-hidden="true"><c:out value="${fn:substring(b.nombres,0,1)}${fn:substring(b.apellidos,0,1)}"/></span>
                            </div>
                            <div class="bm-barbero-cuerpo">
                                <h2 class="bm-barbero-nombre m-0"><c:out value="${b.nombres} ${b.apellidos}"/></h2>
                                <div class="bm-dias" aria-label="Días de atención">
                                    <c:forTokens var="d" items="1,2,3,4,5,6,7" delims="," varStatus="st">
                                        <c:set var="letra" value="${fn:substring('LMMJVSD', st.index, st.index + 1)}"/>
                                        <c:set var="nombreDia" value="${fn:split('lunes,martes,miércoles,jueves,viernes,sábado,domingo', ',')[st.index]}"/>
                                        <c:set var="trabaja" value="${fn:contains(dias, ','.concat(d).concat(','))}"/>
                                        <span class="bm-dia ${trabaja ? 'on' : ''}" title="${nombreDia}">${letra}<span class="visually-hidden"> ${nombreDia}: ${trabaja ? 'atiende' : 'descanso'}</span></span>
                                    </c:forTokens>
                                </div>
                                <span class="small bm-muted">Horario: <c:out value="${b.horaInicio}"/> – <c:out value="${b.horaFin}"/></span>
                                <a class="btn btn-bm-borde btn-bm-sm align-self-start mt-auto" href="${ctx}/reservas/nueva?barberoId=${b.id}">Reservar con <c:out value="${b.nombres}"/> →</a>
                            </div>
                        </article>
                    </div>
                </c:forEach>
            </div>
        </c:otherwise>
    </c:choose>
</main>

<%@ include file="/WEB-INF/views/fragments/footer.jspf" %>

<%@ page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<%@ include file="/WEB-INF/views/fragments/taglibs.jspf" %>
<%@ page isErrorPage="true" %>
<%-- Página de error 403 (registrada en web.xml) --%>
<c:set var="tituloPagina" value="Error 403"/>
<%@ include file="/WEB-INF/views/fragments/head.jspf" %>
<%@ include file="/WEB-INF/views/fragments/header-publico.jspf" %>

<main id="contenido" class="bm-contenedor py-5 text-center d-flex flex-column align-items-center gap-3">
    <div class="bm-error-codigo" aria-hidden="true">403</div>
    <h1 class="m-0" style="font-size:clamp(40px,6vw,60px)">No tienes permiso</h1>
    <p class="bm-muted m-0" style="max-width:520px">Esta sección es solo para otro tipo de usuario. Si crees que es un error, ingresa con la cuenta correcta o habla con el administrador.</p>
    <div class="d-flex flex-wrap justify-content-center gap-2 mt-2">
        <a class="btn btn-bm" href="${ctx}/">Volver al inicio</a>
        <c:if test="${empty usuarioSesion}"><a class="btn btn-bm-borde" href="${ctx}/login">Ingresar</a></c:if>
    </div>
</main>

<%@ include file="/WEB-INF/views/fragments/footer.jspf" %>

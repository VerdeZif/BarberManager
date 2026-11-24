<%@ include file="/WEB-INF/views/fragments/taglibs.jspf" %>
<%--
  GET/POST /login
  POST params: usuario (username o correo), password, csrfToken, siguiente (opcional)
  Request: mensajeError  (ej. "Usuario o contraseña incorrectos." / "Tu cuenta está inactiva.")
  Query:   ?logout=1  → sesión cerrada   ·  ?registro=ok → cuenta creada   ·  ?expirada=1 → sesión vencida
           ?siguiente=/reservas/nueva → a dónde volver después de ingresar (el backend debe validar que sea ruta interna)
--%>
<c:set var="tituloPagina" value="Ingresar"/>
<c:set var="paginaActiva" value="login"/>
<c:if test="${param.logout == '1'}"><c:set var="mensajeInfo" value="Cerraste sesión correctamente."/></c:if>
<c:if test="${param.expirada == '1'}"><c:set var="mensajeInfo" value="Tu sesión venció. Vuelve a ingresar."/></c:if>
<c:if test="${param.registro == 'ok'}"><c:set var="mensajeExito" value="¡Cuenta creada! Ya puedes ingresar con tu usuario."/></c:if>
<%@ include file="/WEB-INF/views/fragments/head.jspf" %>
<%@ include file="/WEB-INF/views/fragments/header-publico.jspf" %>

<main id="contenido" class="bm-contenedor pt-3 pb-5">
    <div class="bm-acceso">
        <%@ include file="/WEB-INF/views/fragments/acceso-lado.jspf" %>

        <section class="bm-card p-4 p-md-5 d-flex flex-column justify-content-center" aria-labelledby="tituloLogin">
            <nav class="bm-pestanas mb-4 align-self-start" aria-label="Acceso">
                <a class="active" aria-current="page" href="${ctx}/login">Ingresar</a>
                <a href="${ctx}/registro">Crear cuenta</a>
            </nav>
            <h1 id="tituloLogin" class="m-0" style="font-size:52px;line-height:.95">Bienvenido de vuelta</h1>
            <p class="bm-muted mt-1 mb-4">Ingresa para reservar y revisar tus citas.</p>

            <%@ include file="/WEB-INF/views/fragments/alertas.jspf" %>

            <form method="post" action="${ctx}/login" data-validar autocomplete="on">
                <input type="hidden" name="csrfToken" value="<c:out value='${sessionScope.csrfToken}'/>">
                <c:if test="${not empty param.siguiente}"><input type="hidden" name="siguiente" value="<c:out value='${param.siguiente}'/>"></c:if>

                <bm:campo nombre="usuario" etiqueta="Usuario o correo" valor="${param.usuario}" requerido="true"
                          autocomplete="username" maxlength="120" placeholder="ej. jperez o jperez@correo.com"/>
                <bm:campo nombre="password" etiqueta="Contraseña" tipo="password" requerido="true"
                          autocomplete="current-password" maxlength="72"/>

                <button type="submit" class="btn btn-bm w-100 mt-2" data-enviando="Ingresando…">Ingresar</button>
            </form>

            <p class="mt-4 mb-0 text-center">¿No tienes cuenta? <a href="${ctx}/registro" class="fw-bold">Créala gratis</a></p>
        </section>
    </div>
</main>

<%@ include file="/WEB-INF/views/fragments/footer.jspf" %>

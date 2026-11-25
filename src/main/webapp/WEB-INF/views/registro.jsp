<%@ page pageEncoding="UTF-8" %>
<%@ include file="/WEB-INF/views/fragments/taglibs.jspf" %>
<%--
  GET/POST /registro  → RegistroServlet (Alexander). El rol CLIENTE lo pone el backend.
  POST params: nombres, apellidos, telefono (opcional), email, username, password, csrfToken
              (confirmarPassword solo se valida en el navegador)
  Request si hay error: error (mensaje general, ej. "Usuario o correo ya registrado")
                        errores (Map campo → mensaje, opcional)
  Si todo sale bien: redirect a /login?registrado=1
--%>
<c:set var="tituloPagina" value="Crear cuenta"/>
<c:set var="paginaActiva" value="registro"/>
<%@ include file="/WEB-INF/views/fragments/head.jspf" %>
<%@ include file="/WEB-INF/views/fragments/header-publico.jspf" %>

<main id="contenido" class="bm-contenedor pt-3 pb-5">
    <div class="bm-acceso">
        <%@ include file="/WEB-INF/views/fragments/acceso-lado.jspf" %>

        <section class="bm-card p-4 p-md-5" aria-labelledby="tituloRegistro">
            <nav class="bm-pestanas mb-4" aria-label="Acceso">
                <a href="${ctx}/login">Ingresar</a>
                <a class="active" aria-current="page" href="${ctx}/registro">Crear cuenta</a>
            </nav>
            <h1 id="tituloRegistro" class="m-0" style="font-size:52px;line-height:.95">Crea tu cuenta</h1>
            <p class="bm-muted mt-1 mb-4">Es gratis. Los campos con <span style="color:var(--bm-link)">*</span> son obligatorios.</p>

            <c:if test="${not empty errores and empty mensajeError}">
                <c:set var="mensajeError" value="Revisa los campos marcados en rojo."/>
            </c:if>
            <%@ include file="/WEB-INF/views/fragments/alertas.jspf" %>

            <form method="post" action="${ctx}/registro" data-validar>
                <input type="hidden" name="csrfToken" value="<c:out value='${sessionScope.csrfToken}'/>">
                <div class="row g-0 gx-3">
                    <div class="col-sm-6"><bm:campo nombre="nombres" etiqueta="Nombres" valor="${param.nombres}" requerido="true" regla="nombre" autocomplete="given-name" maxlength="100"/></div>
                    <div class="col-sm-6"><bm:campo nombre="apellidos" etiqueta="Apellidos" valor="${param.apellidos}" requerido="true" regla="nombre" autocomplete="family-name" maxlength="100"/></div>
                    <div class="col-sm-6"><bm:campo nombre="telefono" etiqueta="Celular" tipo="tel" valor="${param.telefono}" regla="telefono" autocomplete="tel-national" maxlength="9" inputmode="numeric" placeholder="9XXXXXXXX" ayuda="Opcional. Para avisarte de tu cita."/></div>
                    <div class="col-sm-6"><bm:campo nombre="email" etiqueta="Correo" tipo="email" valor="${param.email}" requerido="true" autocomplete="email" maxlength="120"/></div>
                    <div class="col-12"><bm:campo nombre="username" etiqueta="Usuario" valor="${param.username}" requerido="true" regla="username" autocomplete="username" maxlength="50" ayuda="Con este nombre vas a ingresar."/></div>
                    <div class="col-sm-6"><bm:campo nombre="password" etiqueta="Contraseña" tipo="password" requerido="true" regla="password" autocomplete="new-password" maxlength="128" ayuda="Mínimo 12 caracteres, con letras y números."/></div>
                    <div class="col-sm-6"><bm:campo nombre="confirmarPassword" etiqueta="Repite la contraseña" tipo="password" requerido="true" igualA="password" autocomplete="new-password" maxlength="128"/></div>
                </div>
                <button type="submit" class="btn btn-bm w-100 mt-2" data-enviando="Creando cuenta…">Crear cuenta</button>
            </form>
            <p class="mt-4 mb-0 text-center">¿Ya tienes cuenta? <a href="${ctx}/login" class="fw-bold">Ingresa aquí</a></p>
        </section>
    </div>
</main>

<%@ include file="/WEB-INF/views/fragments/footer.jspf" %>

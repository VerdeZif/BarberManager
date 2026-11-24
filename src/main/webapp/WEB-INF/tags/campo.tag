<%@ tag pageEncoding="UTF-8" description="Campo de formulario con etiqueta, ayuda y mensaje de error del servidor" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ attribute name="nombre" required="true" description="name e id del campo (igual al parámetro que lee el Servlet)" %>
<%@ attribute name="etiqueta" required="true" %>
<%@ attribute name="tipo" description="text, email, password, tel... (por defecto text)" %>
<%@ attribute name="valor" description="valor a repintar; nunca se repinta una contraseña" %>
<%@ attribute name="requerido" type="java.lang.Boolean" %>
<%@ attribute name="autocomplete" %>
<%@ attribute name="placeholder" %>
<%@ attribute name="ayuda" description="texto de ayuda debajo del campo" %>
<%@ attribute name="regla" description="regla JS extra: telefono | username | nombre | password" %>
<%@ attribute name="igualA" description="id del campo que debe coincidir (confirmar contraseña)" %>
<%@ attribute name="maxlength" %>
<%@ attribute name="inputmode" %>
<%--
  Uso:  <bm:campo nombre="email" etiqueta="Correo" tipo="email" valor="${param.email}" requerido="true"/>
  El error del servidor se lee de requestScope.errores[nombre] (Map<String,String>).
--%>
<c:set var="t" value="${empty tipo ? 'text' : tipo}"/>
<c:set var="err" value="${requestScope.errores[nombre]}"/>
<div class="mb-3">
    <label class="form-label" for="${nombre}"><c:out value="${etiqueta}"/><c:if test="${requerido}"> <span aria-hidden="true" style="color:var(--bm-link)">*</span></c:if></label>
    <c:if test="${t == 'password'}"><div class="bm-input-pass"></c:if>
    <input class="form-control ${not empty err ? 'is-invalid' : ''}" type="${t}" id="${nombre}" name="${nombre}"
           <c:if test="${t != 'password' and not empty valor}">value="<c:out value='${valor}'/>"</c:if>
           <c:if test="${requerido}">required aria-required="true"</c:if>
           <c:if test="${not empty autocomplete}">autocomplete="${autocomplete}"</c:if>
           <c:if test="${not empty placeholder}">placeholder="<c:out value='${placeholder}'/>"</c:if>
           <c:if test="${not empty regla}">data-regla="${regla}"</c:if>
           <c:if test="${not empty igualA}">data-igual-a="${igualA}"</c:if>
           <c:if test="${not empty maxlength}">maxlength="${maxlength}"</c:if>
           <c:if test="${not empty inputmode}">inputmode="${inputmode}"</c:if>
           <c:if test="${not empty err}">aria-invalid="true"</c:if>
           aria-describedby="${not empty ayuda ? nombre.concat('-ayuda ') : ''}${nombre}-error">
    <c:if test="${t == 'password'}">
        <button type="button" class="bm-ver-pass" data-ver-pass="${nombre}" aria-pressed="false" aria-label="Mostrar u ocultar contraseña">Ver</button>
    </div></c:if>
    <div class="invalid-feedback" id="${nombre}-error" ${t == 'password' and not empty err ? 'style="display:block"' : ''}><c:out value="${err}"/></div>
    <c:if test="${not empty ayuda}"><div class="form-text" id="${nombre}-ayuda"><c:out value="${ayuda}"/></div></c:if>
</div>

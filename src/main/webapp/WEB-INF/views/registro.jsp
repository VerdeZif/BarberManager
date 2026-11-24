<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!doctype html>
<html lang="es">
<head><meta charset="UTF-8"><title>Crear cuenta</title></head>
<body>
<h1>Crear cuenta</h1>
<c:if test="${not empty error}"><p role="alert"><c:out value="${error}"/></p></c:if>
<form method="post" action="<c:url value='/registro'/>">
    <input type="hidden" name="csrfToken" value="<c:out value='${sessionScope.csrfToken}'/>">
    <label>Usuario <input name="username" maxlength="50" required autocomplete="username"></label>
    <label>Correo <input type="email" name="email" maxlength="120" required autocomplete="email"></label>
    <label>Contraseña <input type="password" name="password" minlength="12" maxlength="128" required autocomplete="new-password"></label>
    <label>Nombres <input name="nombres" maxlength="100" required autocomplete="given-name"></label>
    <label>Apellidos <input name="apellidos" maxlength="100" required autocomplete="family-name"></label>
    <label>Teléfono <input name="telefono" maxlength="20" autocomplete="tel"></label>
    <button type="submit">Registrarse</button>
</form>
<a href="<c:url value='/login'/>">Iniciar sesión</a>
</body>
</html>

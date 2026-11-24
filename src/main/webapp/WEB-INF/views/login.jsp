<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!doctype html>
<html lang="es">
<head><meta charset="UTF-8"><title>Iniciar sesión</title></head>
<body>
<h1>Iniciar sesión</h1>
<c:if test="${not empty error}"><p role="alert"><c:out value="${error}"/></p></c:if>
<form method="post" action="<c:url value='/login'/>">
    <input type="hidden" name="csrfToken" value="<c:out value='${sessionScope.csrfToken}'/>">
    <label>Usuario <input name="username" maxlength="50" required autocomplete="username"></label>
    <label>Contraseña <input type="password" name="password" required autocomplete="current-password"></label>
    <button type="submit">Ingresar</button>
</form>
<a href="<c:url value='/registro'/>">Crear cuenta</a>
</body>
</html>

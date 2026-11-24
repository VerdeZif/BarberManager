<%--
  Página de bienvenida temporal.
  Cuando exista el Servlet de inicio (mapeado a "" → GET /), ese Servlet carga "servicios"
  y hace forward a /WEB-INF/views/publico/inicio.jsp; este archivo deja de usarse.
--%>
<jsp:forward page="/WEB-INF/views/publico/inicio.jsp"/>

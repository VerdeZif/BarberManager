# BarberManager — README de Gerald (Frontend)

**Rama:** `frontend-gerald`  
**Objetivo del rol:** construir las vistas JSP del portal público y panel interno, con diseño responsivo e integración con los Servlets de Alexander.

## Referencias

- `../especificacion-funcional-tecnica.md`: RF-01 a RF-18 y permisos.
- `../flujos-aplicacion.md`: flujos F01–F07; especialmente F02 y F05.
- `../rutas-pantallas.md`: rutas GET/POST acordadas.
- `../modelo-entidad-relacion.md`: nombres de campos; **no cambiar BD desde frontend**.

## Carpetas principales

`src/main/webapp/index.jsp`, `src/main/webapp/WEB-INF/views/`, `src/main/webapp/assets/css/`, `assets/js/`, `assets/img/`.

## Actividades por entregas

### E0 — Diseño común
- [ ] Crear propuesta de diseño para portal público y panel interno (paleta, navbar, sidebar, formularios, mensajes).
- [ ] Definir componentes JSP reutilizables: encabezado, menú, pie, alertas, tabla y modal (según necesidad).
- [ ] Acordar con Alexander rutas, parámetros, atributos de request/session y nombres de formularios antes de conectarlos.
- [ ] Preparar páginas de error 403/404 y estados vacíos de tablas.

### E1 — Portal y autenticación (M01)
- [ ] Implementar inicio público `/` con presentación y enlaces a servicios y barberos.
- [ ] Crear catálogo `/servicios` con nombre, descripción, precio S/ y duración.
- [ ] Crear listado público `/barberos` con datos permitidos.
- [ ] Diseñar JSP `/registro`, `/login` y las validaciones JS de datos obligatorios.
- [ ] Mostrar mensajes del servidor para correo/username duplicado, credenciales inválidas y cierre de sesión.
- [ ] Ocultar opciones de navegación por rol **sin reemplazar** la autorización del backend.

### E2 — Reservas y atenciones (M02–M06)
- [ ] Formulario `/reservas/nueva`: **un servicio**, un barbero, fecha y horario.
- [ ] Mostrar horarios disponibles desde el backend; avisar cuando ya estén ocupados.
- [ ] Página `/mis-citas` para historial, próximas citas y cancelación válida.
- [ ] Vistas de recepción: clientes, agenda y acciones de confirmar/reprogramar/cancelar.
- [ ] Vistas de barbero: mis citas e inicio/finalización de atenciones propias.
- [ ] Vistas de administrador: usuarios, barberos, horarios básicos y catálogo de servicios.

### E3 — Inventario, ventas, simulación y ticket (M07–M09)
- [ ] Crear vistas de productos, stock y movimientos de inventario.
- [ ] Construir formulario de venta: servicio de una atención terminada o venta solo de productos; añadir productos opcionales.
- [ ] Mostrar resumen de venta, precios históricos, cantidades y saldo pendiente.
- [ ] Crear formulario **Simular pago** con `EFECTIVO`, `YAPE`, `PLIN`, `TARJETA`; nunca solicitar datos bancarios reales.
- [ ] Mostrar estados `PENDIENTE`, `PARCIAL`, `PAGADA` y bloquear la opción ticket antes de `PAGADA`.
- [ ] Implementar vista JSP de ticket imprimible y CSS `@media print`, con número único, fecha, items, total en soles, métodos de pago y **PAGO SIMULADO — SIN VALIDEZ TRIBUTARIA**.
- [ ] Permitir consulta de tickets propios al cliente y de comisiones propias al barbero.

### E4 — Dashboard, integración y calidad (M10)
- [ ] Implementar dashboard de administrador: citas por estado, ventas **simuladas**, stock bajo y comisiones.
- [ ] Integrar vistas y formularios con Servlets reales (no dejar datos ficticios como fuente final).
- [ ] Validar diseño responsivo en escritorio y móvil; tablas legibles, mensajes de error y navegación coherente.
- [ ] Verificar que las vistas JSP privadas se sirvan desde `WEB-INF/views` mediante Servlet.
- [ ] Probar la navegación F01–F07 con cuentas de cada rol y documentar capturas de prueba.

## Entregables

1. JSP y archivos CSS/JS ubicados en las carpetas oficiales.
2. Componentes visuales consistentes para portal y panel.
3. Reservas, ventas, simulación de pago y ticket integrados con el backend.
4. Capturas de portal, agenda, venta, pago parcial y ticket impreso.
5. Pull Request hacia `develop` con pasos para probar las pantallas.

## Criterios de aceptación

- Todas las rutas pactadas muestran pantallas adecuadas al rol.
- Formularios muestran errores y no permiten operaciones inválidas de forma superficial; el backend vuelve a validar.
- El ticket muestra expresamente que es **simulado**, se imprime sin botones ni elementos de navegación y no se muestra en ventas incompletas.
- No se introducen cambios directos de SQL ni lógica de cobro real.

## Colaboración sin Issues

Trabaja en `frontend-gerald`. Actualiza los checkboxes en este README y envía Pull Requests a `develop` con capturas, rutas afectadas y pruebas realizadas. Coordina contratos con Alexander antes de cambiar nombres de campos.

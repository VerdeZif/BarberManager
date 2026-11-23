# BarberManager — README de Alexander (Backend)

**Rama:** `backend-alexander`  
**Objetivo del rol:** implementar reglas de negocio y seguridad de BarberManager con Jakarta Servlets, Service, DAO y JPA/Hibernate.

## Referencias y alcance

Consultar `../especificacion-funcional-tecnica.md`, `../flujos-aplicacion.md`, `../modelo-entidad-relacion.md` y `../rutas-pantallas.md`. No ampliar las 12 tablas sin aprobación. **Los pagos son simulaciones y el ticket es interno, no fiscal.**

## Carpetas principales

`src/main/java/com/barbermanager/{controller,service,dao,model,filter,util}` y `src/main/resources/META-INF/persistence.xml`.

## Actividades por entregas

### E0 — Contratos y configuración
- [ ] Verificar JDK 17, Maven WAR, Servlet 6.0, Tomcat 10.1, Hibernate y driver BD.
- [ ] Coordinar con Jybran motor MySQL/MariaDB, DDL, tipos SQL, nombres y relaciones definitivas.
- [ ] Acordar con Gerald métodos/rutas HTTP, parámetros, JSP de destino, errores y atributos de sesión.
- [ ] Preparar `persistence.xml` sin contraseñas en Git; documentar configuración local segura.
- [ ] Definir constantes/enumeraciones para roles, estados de citas, pagos simulados y comisiones.

### E1 — Seguridad, usuarios y clientes (M01–M02)
- [ ] Crear entidades/DAO/Service/Servlet de `usuarios` y `clientes` según el esquema aprobado.
- [ ] Registro público con rol fijo `CLIENTE`, unicidad de username/correo y creación transaccional usuario-cliente.
- [ ] Login/logout con contraseña hasheada, sesión y manejo seguro de credenciales.
- [ ] Filtro de autorización por rol en cada ruta interna y comprobación de propiedad de datos.
- [ ] CRUD/inactivación de clientes y administración de usuarios según permisos.
- [ ] Manejar validaciones servidor y CSRF para acciones que cambian datos.

### E2 — Barberos, servicios, citas y atenciones (M03–M06)
- [ ] CRUD/inactivación de barberos; jornadas simples y porcentaje de comisión.
- [ ] CRUD/inactivación de servicios con precio `BigDecimal` y duración positiva.
- [ ] Implementar búsqueda de disponibilidad por barbero: calendario laboral, duración y citas bloqueantes.
- [ ] Validar solapamiento: `inicioExistente < finNuevo && finExistente > inicioNuevo`.
- [ ] Revalidar y guardar reservas bajo transacción/bloqueo de la fila del barbero o estrategia equivalente para impedir duplicados concurrentes.
- [ ] Crear, confirmar, reprogramar y cancelar citas respetando estados y permisos.
- [ ] Inicio/fin de atención: una atención por cita y transiciones `CONFIRMADA → EN_ATENCION → FINALIZADA`.

### E3 — Inventario, ventas, pagos simulados, ticket y comisiones (M07–M09)
- [ ] CRUD de productos; stock, precio, categoría y mínimo válidos.
- [ ] Entradas/salidas/ajustes de inventario con movimiento y actualización transaccional.
- [ ] Venta de una atención finalizada (un único detalle de servicio) con productos opcionales, o venta solo de productos.
- [ ] Al confirmar una venta, bloquear/verificar stock, descontar cantidades y guardar movimientos sin inconsistencias.
- [ ] Calcular importes usando `BigDecimal`; conservar precios y descripción de venta históricos.
- [ ] Implementar `pagos` simulados, métodos `EFECTIVO/YAPE/PLIN/TARJETA`, validación del saldo y estados `PENDIENTE/PARCIAL/PAGADA`.
- [ ] Generar número de ticket único en venta; proporcionar datos al JSP de ticket **solo si PAGADA**. Reimpresiones conservan el número.
- [ ] Crear comisión **una sola vez** por atención finalizada con venta pagada, basada en importe del servicio (no productos), con porcentaje y monto históricos.
- [ ] Permitir al administrador marcar comisión como `PAGADA`; sin tabla de liquidaciones.

### E4 — Reportes y pruebas (M10)
- [ ] Implementar consultas/DTOs del dashboard: citas por estado, ventas simuladas por período, stock bajo y comisiones.
- [ ] Probar permisos contra acceso directo a rutas, datos ajenos y tickets de otros clientes.
- [ ] Probar reserva concurrente, stock insuficiente, pago mayor que saldo y doble comisión.
- [ ] Probar transacciones ante error para evitar venta/pago/movimiento parcial.
- [ ] Ejecutar `mvn clean package`; comprobar WAR en Tomcat y documentar datos necesarios para pruebas.

## Entregables

1. Estructura de clases por capas y configuración JPA segura.
2. Servlets/controladores y servicios de los 10 módulos.
3. Contrato de rutas y parámetros documentado con Gerald.
4. Pruebas verificables de seguridad, reservas y transacciones.
5. Pull Requests hacia `develop` y evidencia de ejecución en Tomcat.

## Criterios de aceptación

- Ninguna operación sensible depende solo de JS o de ocultar botones.
- No hay dos citas activas superpuestas de un mismo barbero ni stock negativo.
- No se registra dinero real, credenciales de tarjeta ni conexiones financieras.
- Se emite ticket interno solo para venta `PAGADA`, sin contenido tributario.
- Una atención no origina comisiones duplicadas.

## Colaboración sin Issues

Trabaja en `backend-alexander`. Actualiza este README con `[x]` y presenta avances mediante Pull Requests hacia `develop`. Los cambios de BD deben acordarse primero con Jybran y el líder.

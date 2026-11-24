# Avance de Alexander — Backend BarberManager

**Fecha de revisión:** 24 de noviembre de 2026  
**Rama de trabajo prevista:** `backend-alexander`  
**Estado:** implementación inicial; pendiente de integración y pruebas.

Este documento describe lo que existe en el código, no certifica que los flujos estén terminados o funcionando en Tomcat y MySQL.

## Alcance y organización

El backend utiliza Java 17, Jakarta Servlets, JPA/Hibernate y MySQL 8 como referencia. Se organiza en `controller`, `service`, `repository`, `model`, `filter` y `util`. Se acordó utilizar **Repository** para acceso a datos; algunos documentos anteriores todavía mencionan DAO.

El sistema define cuatro roles: `ADMINISTRADOR`, `RECEPCIONISTA`, `BARBERO` y `CLIENTE`. El registro público asigna `CLIENTE`. Los pagos son simulados y el ticket es interno, sin validez tributaria.

## Trabajo realizado

### Modelos y persistencia

- Se crearon las 12 entidades JPA: usuario, cliente, barbero, servicio, cita, atención, producto, movimiento de inventario, venta, detalle de venta, pago y comisión. También se definió el enum `Rol`.
- Las entidades están registradas en `src/main/resources/META-INF/persistence.xml`. La configuración usa `hibernate.hbm2ddl.auto=validate` y no genera el esquema.
- La conexión se obtiene mediante `Jpa` y las variables de entorno `BARBER_DB_URL`, `BARBER_DB_USER` y `BARBER_DB_PASSWORD`. No hay credenciales configuradas en el repositorio.
- El esquema que compartió Jybran se guardó como [referencia para backend](docs/esquema-mysql-referencia-backend.sql). No sustituye el script oficial `database/schema.sql` ni confirma que esté desplegado o aprobado.

### Repositorios

Existe un repositorio para cada una de las 12 entidades y una clase base con operaciones de inserción, búsqueda por ID y listado. Hay consultas específicas iniciales, entre ellas búsqueda de citas solapadas y búsquedas con bloqueo de barbero, producto y venta. Las consultas y los bloqueos aún deben probarse con el motor real.

### Servicios

Se implementaron primeras versiones de los siguientes servicios:

| Área | Funcionalidad implementada |
|---|---|
| Autenticación | Registro transaccional de usuario y cliente, validaciones básicas y verificación de contraseña con PBKDF2. |
| Usuarios y clientes | Cambio de estado y rol de usuario; creación, edición y cambio de estado de cliente. |
| Barberos y servicios | Guardado de datos, horario simple, porcentaje, precio y duración. |
| Citas | Reserva con validación de jornada y solapamiento, cambios de estado básicos y reprogramación conservando barbero y duración. |
| Atenciones | Inicio y finalización vinculados al estado de la cita. |
| Inventario | Guardado básico de productos y movimientos con actualización de stock dentro de una transacción. |
| Ventas y pagos | Creación de venta con detalle de atención o productos, descuento de stock y movimientos; registro de pagos simulados y generación de comisión al completar el pago. |
| Tickets y comisiones | Consulta de datos de ticket pagado con comprobación de cliente cuando corresponde; marcado de comisión como pagada. |
| Reportes | Consultas iniciales de citas por estado, ventas pagadas por fecha de venta y productos con stock bajo. |

La clase `Transacciones` centraliza la apertura, confirmación o reversión y cierre de cada operación. Los servicios no reemplazan las comprobaciones de permisos y propiedad que deben aplicarse al exponer cada acción.

### Controladores y seguridad

Se crearon controladores para registro, login y logout. También existen acciones POST iniciales para cambiar el estado de un usuario, crear clientes, iniciar o finalizar atenciones, registrar pagos y marcar comisiones como pagadas.

Hay un filtro CSRF para solicitudes que modifican datos y un filtro de autorización que consulta el rol y estado actuales en base de datos para rutas protegidas. Las vistas JSP de login y registro son formularios básicos para integración inicial.

Las rutas implementadas, sus parámetros y roles están en el [contrato para Gerald](docs/equipo/CONTRATO-BACKEND-FRONTEND.md). No todas las rutas propuestas en la documentación funcional están implementadas.

## Trabajo pendiente

### Completar funcionalidades

- Completar los servicios y validaciones pendientes de usuarios, clientes, catálogos y reportes; revisar su comportamiento con la especificación.
- Resolver cómo tratar ventas con total cero: el esquema admite precios cero y la implementación actual rechaza ventas de total cero.
- Revisar el reporte de ventas: actualmente filtra por fecha de creación de la venta, no por fecha en que se completó el pago. Acordar la definición del período.
- Completar consultas necesarias para disponibilidad, historial, comisiones, servicios más solicitados y demás indicadores.
- Crear controladores para barberos, servicios, citas, inventario, creación de ventas, tickets y reportes; completar las operaciones faltantes de los controladores existentes.
- Integrar los controladores con las vistas definitivas de Gerald y definir mensajes, parámetros y respuestas acordados.
- Aplicar y probar autorización por acción y propiedad: por ejemplo, un cliente solo puede operar sobre sus propios datos y tickets; un barbero, sobre sus propias citas y atenciones.
- Completar el ciclo de sesión y seguridad, y revisar que todas las acciones sensibles comprueben permisos en el servidor.

### Coordinación de base de datos

- Confirmar con Jybran el esquema definitivo, su aplicación en MySQL 8 y la correspondencia de columnas, tipos y restricciones con las entidades.
- Conciliar los comentarios de estados: en citas se menciona `ATENDIDA`, mientras la especificación usa `EN_ATENCION` y `FINALIZADA`; en ventas se menciona `ANULADA` y se omite `PARCIAL`. Son comentarios del SQL compartido, no restricciones de esos campos.
- Revisar restricciones adicionales, semántica de ajustes de inventario, control de concurrencia y tratamiento de importes cero. Preparar para Jybran un documento de observaciones cuando se termine la revisión.

### Verificación e integración

- Instalar o configurar Maven y ejecutar `mvn clean package`; comprobar el WAR en Tomcat 10.1.
- Aplicar el esquema acordado en una base de pruebas y verificar el arranque de Hibernate y todos los mapeos.
- Probar registro, login, logout, CSRF, acceso directo por rol, cuentas inactivas y acceso a datos ajenos.
- Probar reservas simultáneas, reprogramación, stock insuficiente, ventas concurrentes, pagos parciales y excesivos, ticket de venta no pagada y comisiones duplicadas.
- Comprobar que un error revierta la venta, pago o movimiento completo y que las cifras monetarias y los tickets se presenten como simulados.
- Realizar la integración con Gerald y documentar resultados y evidencia en el Pull Request hacia `develop`.

## Validación realizada y límites

Se hizo una compilación intermedia con `javac` y las dependencias locales, antes de los últimos controladores. Los intentos posteriores no proporcionaron una confirmación verificable de compilación completa. Maven no estaba disponible en el entorno durante el desarrollo. No se han comprobado estos cambios ejecutando Tomcat ni una base MySQL.

Las entidades, repositorios, servicios y controladores descritos deben considerarse **trabajo en progreso** hasta completar esas verificaciones.

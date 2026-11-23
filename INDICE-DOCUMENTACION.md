# BarberManager — Documentación técnica del equipo

**Versión:** 1.0 (propuesta para revisión). **Proyecto:** BarberManager — una barbería, 12 tablas, pagos 100 % simulados y tickets internos.

## Documentos

1. [Especificación funcional y técnica](docs/especificacion-funcional-tecnica.md) — objetivos, requisitos, 10 módulos, roles y reglas generales.
2. [Flujos de la aplicación](docs/flujos-aplicacion.md) — diagramas y pasos de registro, reserva, atención, venta, cobro simulado, ticket, comisión e inventario.
3. [Modelo entidad-relación](docs/modelo-entidad-relacion.md) — diagrama Mermaid de 12 tablas, diccionario de datos y restricciones.
4. [Rutas y pantallas](docs/rutas-pantallas.md) — URL propuestas, panel público/interno y permisos.

## Responsables

- **Líder:** revisar requisitos y coordinar cambios.
- **Gerald (frontend):** implementar vistas según `rutas-pantallas.md` y `flujos-aplicacion.md`.
- **Alexander (backend):** implementar validaciones, estados, transacciones y contratos de datos según especificación y flujos.
- **Jybran (BD):** revisar `modelo-entidad-relacion.md`, preparar ER final y scripts SQL.

## Cómo colaborar

Cada integrante desarrolla en su rama (`frontend-gerald`, `backend-alexander`, `database-jybran`) y envía Pull Requests hacia `develop`. **No modificar decisiones funcionales sin revisión del equipo.**

## Próxima aprobación

Antes de construir entidades JPA y `schema.sql`: acordar motor/versión de base de datos, tipos de columnas, restricciones de inventario y política de horarios.

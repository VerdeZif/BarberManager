# Equipo BarberManager — Plan de trabajo por README

No se utilizarán GitHub Issues. Cada responsable consulta su README, marca las casillas completadas y registra las pruebas en la descripción de sus Pull Requests.

| Integrante | Rol | Rama | Plan de trabajo |
|---|---|---|---|
| Israel | Líder | `develop` / revisión de `main` | [README-LIDER.md](README-LIDER.md) |
| Gerald | Frontend | `frontend-gerald` | [README-GERALD-FRONTEND.md](README-GERALD-FRONTEND.md) |
| Alexander | Backend | `backend-alexander` | [README-ALEXANDER-BACKEND.md](README-ALEXANDER-BACKEND.md) |
| Jybran | Base de datos | `database-jybran` | [README-JYBRAN-BASE-DATOS.md](README-JYBRAN-BASE-DATOS.md) |

## Entregas compartidas

- **E0:** validar requisitos, motores y contratos antes de programar.
- **E1:** portal, seguridad, usuarios, clientes y base inicial.
- **E2:** barberos, servicios, agenda, citas y atenciones.
- **E3:** productos, inventario, ventas, pagos simulados, tickets y comisiones.
- **E4:** dashboard, pruebas, documentación e integración.

## Reglas comunes

1. El proyecto contempla **10 módulos y 12 tablas**.
2. Una cita incluye **un servicio y un barbero**.
3. Todo pago es **simulado**; nunca se conecta con bancos.
4. Solo las ventas `PAGADA` muestran ticket interno **sin validez tributaria**.
5. Todas las ramas de cada integrante se integran mediante Pull Request a `develop`.
6. `main` solo recibe una versión estable revisada por el líder.
7. Si hay conflicto entre un README de tareas y la especificación oficial, se corrige el README y se coordina con el líder.

## Documentación oficial

- [Especificación](../especificacion-funcional-tecnica.md)
- [Flujos de aplicación](../flujos-aplicacion.md)
- [Modelo entidad-relación](../modelo-entidad-relacion.md)
- [Rutas y pantallas](../rutas-pantallas.md)

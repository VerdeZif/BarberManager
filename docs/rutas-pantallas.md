# BarberManager — Rutas y pantallas

> **BarberManager · v1.0 — propuesta para revisión del equipo**  
> Pagos simulados; el ticket no tiene validez tributaria. Los cambios se aprueban en GitHub antes de codificar.

## 10. Navegación y rutas propuestas

> Las rutas son **propuestas de contrato** para el equipo; Alexander puede ajustar nombres en una revisión conjunta, no unilateralmente.

### Público / cliente

| Método | Ruta relativa | Vista / acción |
|---|---|---|
| GET | `/` | Inicio |
| GET | `/servicios` | Catálogo |
| GET | `/barberos` | Lista de barberos |
| GET/POST | `/registro` | Alta de cliente |
| GET/POST | `/login` | Login |
| POST | `/logout` | Cerrar sesión |
| GET/POST | `/reservas/nueva` | Crear reserva |
| GET | `/mis-citas` | Historial y próximas citas |
| POST | `/mis-citas/cancelar` | Cancelar cita propia, si procede |
| GET | `/mis-tickets` | Consultar tickets propios pagados |
| GET | `/mis-tickets/ver?id=...` | Visualizar ticket propio |

### Interno

| Ruta base | Funciones |
|---|---|
| `/admin/dashboard` | Indicadores y reportes |
| `/admin/usuarios` | Gestión de usuarios |
| `/admin/barberos` | Barberos y jornadas básicas |
| `/admin/servicios` | Catálogo y precios |
| `/admin/comisiones` | Comisiones, marcar pagada |
| `/recepcion/clientes` | Clientes |
| `/recepcion/citas` | Agenda, confirmación, reprogramación, cancelación |
| `/recepcion/inventario` | Productos y movimientos (admin también) |
| `/recepcion/ventas` | Ventas, simulación de cobros |
| `/recepcion/ventas/ticket?id=...` | Ticket imprimible pagado |
| `/barbero/mis-citas` | Agenda propia |
| `/barbero/atenciones` | Inicio/finalización de atenciones propias |
| `/barbero/comisiones` | Comisiones propias |

Las vistas internas JSP deben estar en `WEB-INF/views` y servirse por Servlet, no exponerse directamente. Validar permisos en backend en cada acción, no solo ocultar botones.
## Reglas de implementación de las pantallas

- Las rutas protegidas deberán validar sesión y rol desde un filtro o Servlet, nunca solo por enlaces ocultos.
- Las vistas JSP internas se ubican bajo `WEB-INF/views` para evitar acceso directo.
- Los formularios tienen validación visual JavaScript y validación obligatoria en el backend.
- El ticket muestra `PAGO SIMULADO — SIN VALIDEZ TRIBUTARIA` y solo está habilitado tras pago completo.

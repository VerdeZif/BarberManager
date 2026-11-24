# BarberManager — Contrato entre vistas JSP y Servlets (E0 + E1)

> Propuesta de Gerald (frontend) para revisar con Alexander (backend). Si cambia un nombre, se cambia aquí y en la JSP.

## 1. Cómo se arma cada vista

```
src/main/webapp/
├── index.jsp                         → temporal: forward a publico/inicio.jsp
├── assets/css/barber.css             → estilo "Urbana moderna" sobre Bootstrap 5.3.3
├── assets/js/barber.js               → validación visual, ver contraseña, evitar doble envío
├── assets/img/favicon.svg
└── WEB-INF/
    ├── tags/campo.tag                → <bm:campo> campo de formulario con error del servidor
    └── views/
        ├── fragments/                → taglibs, head, header-publico, footer, alertas,
        │                               panel-inicio / panel-fin (sidebar por rol), scripts
        ├── publico/                  → inicio, servicios, barberos, login, registro
        ├── panel/_plantilla.jsp      → base para las vistas internas (E2–E4)
        └── error/                    → 403, 404, 500 (registradas en web.xml)
```

El Servlet hace `request.getRequestDispatcher("/WEB-INF/views/publico/servicios.jsp").forward(req, resp);`.

## 2. Atributos de sesión (los lee el header y el menú)

| Atributo | Tipo | Uso |
|---|---|---|
| `usuarioSesion` | objeto con `getId()`, `getUsername()`, `getRol()` | saludo, menú por rol. `rol` ∈ `ADMINISTRADOR`, `RECEPCIONISTA`, `BARBERO`, `CLIENTE` |
| `csrfToken` | `String` | se envía como campo oculto `csrfToken` en todos los POST |
| `flashExito` / `flashError` | `String` | mensaje de una sola vez tras un redirect (la JSP lo borra al mostrarlo) |

Ocultar un enlace NO es seguridad: el filtro de Alexander debe validar el rol en cada ruta.

## 3. Atributos de request comunes

| Atributo | Tipo | Se ve como |
|---|---|---|
| `mensajeExito` | `String` | alerta verde |
| `mensajeError` | `String` | alerta roja |
| `mensajeInfo` | `String` | alerta azul |
| `errores` | `Map<String,String>` campo → mensaje | texto rojo debajo del campo (la clave = `name` del input) |

Los valores del formulario se repintan con `param.*`, así que en caso de error basta hacer **forward** (no redirect) a la misma JSP. Las contraseñas nunca se repintan.

## 4. Pantallas E1

| Ruta | Método | JSP | Atributos que espera | Parámetros que envía |
|---|---|---|---|---|
| `/` | GET | `publico/inicio.jsp` | `servicios` (List<Servicio> activos, los 3 primeros van al bloque de precios) | — |
| `/servicios` | GET | `publico/servicios.jsp` | `servicios` | enlaces a `/reservas/nueva?servicioId={id}` |
| `/barberos` | GET | `publico/barberos.jsp` | `barberos` (List<Barbero> activos) | enlaces a `/reservas/nueva?barberoId={id}` |
| `/login` | GET/POST | `publico/login.jsp` | `mensajeError` si falla | `usuario` (username o correo), `password`, `csrfToken`, `siguiente` (opcional) |
| `/registro` | GET/POST | `publico/registro.jsp` | `errores`, `mensajeError` | `nombres`, `apellidos`, `telefono` (opcional), `email`, `username`, `password`, `confirmarPassword`, `csrfToken` |
| `/logout` | POST | — | — | `csrfToken` → redirect a `/login?logout=1` |

Mensajes por parámetro en `/login`: `?logout=1` (sesión cerrada), `?registro=ok` (cuenta creada), `?expirada=1` (sesión vencida).
Registro correcto → `redirect /login?registro=ok`. El rol `CLIENTE` lo pone el backend, nunca el formulario.
`siguiente` debe validarse en backend (solo rutas internas que empiecen con `/`) para evitar redirecciones abiertas.

### Propiedades que usa la JSP (getters de las entidades)

- **Servicio:** `id`, `nombre`, `descripcion`, `precio` (BigDecimal), `duracionMinutos`
- **Barbero:** `id`, `nombres`, `apellidos`, `diasLaborables` (`"1,2,3,4,5,6"`, 1 = lunes … 7 = domingo), `horaInicio`, `horaFin` (LocalTime)

Coinciden con las columnas de `database/schema.sql` en camelCase. La vista de barberos no muestra comisión ni datos del usuario.

## 5. Errores

`web.xml` envía 403, 404 y 500 a `WEB-INF/views/error/`. Para negar acceso: `resp.sendError(HttpServletResponse.SC_FORBIDDEN);`.

## 6. Vistas internas (E2–E4)

Copiar `panel/_plantilla.jsp`, cambiar `tituloPagina` y `menuActivo`:
`dashboard`, `usuarios`, `barberos`, `servicios`, `comisiones`, `citas`, `clientes`, `inventario`, `ventas`, `b-citas`, `b-atenciones`, `b-comisiones`.
Los estados se pintan con `<span class="bm-chip bm-chip-${estado}">`: `PENDIENTE`, `CONFIRMADA`, `EN_ATENCION`, `ATENDIDA`, `FINALIZADA`, `CANCELADA`, `PARCIAL`, `PAGADA`, `ANULADA`, `STOCK_BAJO`.

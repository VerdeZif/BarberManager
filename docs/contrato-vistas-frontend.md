# BarberManager — Contrato entre vistas JSP y Servlets (E0 + E1)

> Actualizado para usar los nombres que ya programó Alexander en `backend-alexander` (LoginServlet, RegistroServlet, filtros).

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
        ├── login.jsp, registro.jsp   → misma ruta que usan LoginServlet y RegistroServlet (reemplazan las versiones simples)
        ├── publico/                  → inicio, servicios, barberos (las carga PublicoServlet)
        ├── panel/_plantilla.jsp      → base para las vistas internas (E2–E4)
        └── error/                    → 403, 404, 500 (registradas en web.xml)
```

El Servlet hace `request.getRequestDispatcher("/WEB-INF/views/publico/servicios.jsp").forward(req, resp);`.

## 2. Atributos de sesión (los crea LoginServlet)

| Atributo | Tipo | Uso en la vista |
|---|---|---|
| `usuarioId` | `Long` | saber si hay sesión |
| `rol` | `String` | menú por rol: `ADMINISTRADOR`, `RECEPCIONISTA`, `BARBERO`, `CLIENTE` |
| `username` | `String` (opcional) | saludo "Hola, …". **Pedido a Alexander:** agregar `nueva.setAttribute("username", usuario.getUsername());` en LoginServlet. Si no está, se muestra "Hola, tu cuenta". |
| `csrfToken` | `String` | lo crea CsrfFilter; todas las JSP lo envían como campo oculto `csrfToken` |
| `flashExito` / `flashError` | `String` | mensaje de una sola vez tras un redirect |

Ocultar un enlace NO es seguridad: AutorizacionFilter valida el rol en cada ruta.

## 3. Atributos de request comunes

| Atributo | Tipo | Se ve como |
|---|---|---|
| `mensajeExito` | `String` | alerta verde |
| `error` o `mensajeError` | `String` | alerta roja (`error` es el nombre que usan LoginServlet y RegistroServlet) |
| `mensajeInfo` | `String` | alerta azul |
| `errores` | `Map<String,String>` campo → mensaje | texto rojo debajo del campo (la clave = `name` del input) |

Los valores del formulario se repintan con `param.*`, así que en caso de error basta hacer **forward** (no redirect) a la misma JSP. Las contraseñas nunca se repintan.

## 4. Pantallas E1

| Ruta | Método | Servlet | JSP | Atributos | Parámetros que envía la vista |
|---|---|---|---|---|---|
| `/` | GET | PublicoServlet | `publico/inicio.jsp` | `servicios` (activos) | enlaces a `/reservas/nueva?servicioId={id}` |
| `/servicios` | GET | PublicoServlet | `publico/servicios.jsp` | `servicios` | `/reservas/nueva?servicioId={id}` |
| `/barberos` | GET | PublicoServlet | `publico/barberos.jsp` | `barberos` (activos) | `/reservas/nueva?barberoId={id}` |
| `/login` | GET/POST | LoginServlet | `login.jsp` | `error` | `username`, `password`, `csrfToken` |
| `/registro` | GET/POST | RegistroServlet | `registro.jsp` | `error` | `nombres`, `apellidos`, `telefono` (opcional), `email`, `username`, `password`, `csrfToken` |
| `/logout` | POST | LogoutServlet | — | — | `csrfToken` |

Mensajes por parámetro en `/login`: `?registrado=1` (cuenta creada), `?logout=1`, `?expirada=1`.
Contraseña: 12 a 128 caracteres (igual que AutenticacionService). `confirmarPassword` solo se revisa en el navegador.
`PublicoServlet` (en `controller/publico`) es de solo lectura: lista servicios y barberos activos; si la BD falla, la página carga vacía con un aviso.

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

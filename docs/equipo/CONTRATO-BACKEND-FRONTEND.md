# Contrato de integración backend y frontend

Este documento recogerá las rutas implementadas, sus parámetros y las vistas o respuestas correspondientes. Las rutas propuestas en `../rutas-pantallas.md` todavía requieren integración y acuerdo con Gerald.

## Estado actual

Las rutas siguientes tienen una primera implementación; aún requieren pruebas con Tomcat y MySQL. Las demás rutas del proyecto todavía no están implementadas.

| Método | Ruta | Parámetros | Resultado |
|---|---|---|---|
| GET | `/login` | — | Formulario de inicio de sesión |
| POST | `/login` | `username`, `password`, `csrfToken` | Redirige a `/` si es válido; error 401 en caso contrario |
| GET | `/registro` | — | Formulario de registro |
| POST | `/registro` | `username`, `email`, `password`, `nombres`, `apellidos`, `telefono` opcional, `csrfToken` | Crea cuenta CLIENTE y redirige a `/login?registrado=1`; error 400 si la validación falla |
| POST | `/logout` | `csrfToken` | Invalida la sesión y redirige a `/` |

Los formularios POST deben incluir el token de sesión `csrfToken`. Un GET crea el token si falta. Al iniciar sesión se invalida la sesión anterior y se crea otra; el siguiente GET genera un token nuevo. La sesión contiene `usuarioId` y `rol`.

## Acciones internas iniciales

Estas acciones responden 204 al completarse y requieren token CSRF y sesión. Son una primera implementación pendiente de pruebas e integración con las vistas.

| POST | Rol | Parámetros adicionales |
|---|---|---|
| `/admin/usuarios` | ADMINISTRADOR | `id`, `activo` (true o false) |
| `/recepcion/clientes` | ADMINISTRADOR o RECEPCIONISTA | `nombres`, `apellidos`, `telefono` opcional |
| `/recepcion/atenciones` | ADMINISTRADOR o RECEPCIONISTA | `citaId`, `accion` (iniciar o finalizar), `observaciones` opcional |
| `/recepcion/ventas/pago` | ADMINISTRADOR o RECEPCIONISTA | `ventaId`, `metodo`, `monto` |
| `/admin/comisiones` | ADMINISTRADOR | `id` |

El filtro consulta el rol y estado actuales del usuario en cada solicitud protegida. Faltan controladores y comprobaciones de propiedad para los demás flujos.

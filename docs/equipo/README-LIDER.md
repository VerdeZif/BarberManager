# BarberManager — README de Israel (líder)

**Rama de integración:** `develop` · **Rama estable:** `main`  
**Objetivo del rol:** garantizar que los módulos se construyan con el mismo alcance, se integren y se puedan demostrar de principio a fin.

## Documentos obligatorios

Consultar `../especificacion-funcional-tecnica.md`, `../flujos-aplicacion.md`, `../modelo-entidad-relacion.md` y `../rutas-pantallas.md`. Rigen: 10 módulos, 12 tablas, una cita por servicio, pagos simulados y ticket interno sin validez tributaria.

## Actividades por entregas

### E0 — Alineación y coordinación
- [ ] Revisar con el grupo los objetivos, funciones dentro/fuera de alcance y cuatro roles (`ADMINISTRADOR`, `RECEPCIONISTA`, `BARBERO`, `CLIENTE`).
- [ ] Confirmar versión del motor BD de XAMPP y compatibilidad del driver/dialecto con Alexander y Jybran.
- [ ] Aprobar nombres y tipos de columnas, estados, rutas y campos necesarios para formularios **antes** de programar entidades.
- [ ] Confirmar las simplificaciones: una sola barbería, una cita/un servicio, horarios básicos, sin pasarelas reales ni comprobantes SUNAT.
- [ ] Acordar con Jybran el contrato de ajustes de inventario y con el equipo la política de cancelación de citas.

### E1 — Repositorio y base ejecutable
- [ ] Verificar `pom.xml`, Java 17, Maven, WAR y despliegue en Tomcat 10.1.
- [ ] Comprobar carpetas `src/main/java`, `src/main/resources`, `src/main/webapp`, `docs` y `database`.
- [ ] Mantener `README.md` raíz como índice de documentación y los cuatro README de equipo enlazados.
- [ ] Revisar `.gitignore`: `target/`, `.idea/`, credenciales y archivos privados fuera del repositorio.
- [ ] Comprobar que cada integrante trabaje solo en su rama y que las propuestas lleguen por Pull Request a `develop`.

### E2 — Supervisión funcional
- [ ] Revisar el flujo registro/login/roles; validar accesos y privacidad de cliente/barbero.
- [ ] Revisar barberos, servicios, horas de trabajo y reservas sin solapamiento.
- [ ] Revisar estados de citas y ciclo de atención.
- [ ] Verificar que backend y frontend utilicen los mismos nombres de rutas y campos.
- [ ] Revisar la integración entre ventas, existencias, pagos simulados, ticket y comisiones.

### E3 — Pruebas de extremo a extremo
- [ ] Ejecutar demo: registro → reserva → confirmación → atención → venta → pago simulado → ticket → comisión.
- [ ] Probar dos reservas simultáneas para un barbero; la segunda reserva superpuesta debe rechazarse.
- [ ] Probar stock insuficiente; no debe guardarse venta parcial ni quedar stock negativo.
- [ ] Probar pago parcial y pago final; ticket visible solamente cuando `PAGADA`.
- [ ] Probar permisos: cliente no ve tickets ajenos, barbero no modifica citas de otros, recepción no administra usuarios.
- [ ] Confirmar que toda cifra de ingresos en dashboard se identifique como **simulada**.

### E4 — Cierre y presentación
- [ ] Revisar Pull Requests, compilación y navegación tras cada integración.
- [ ] Corregir enlaces y documentación, y mantener una sola definición oficial.
- [ ] Actualizar instrucciones de instalación y configuración local sin compartir contraseñas.
- [ ] Organizar evidencias: capturas de pantallas, ticket de muestra, modelo ER y pruebas realizadas.
- [ ] Fusionar `develop` hacia `main` únicamente cuando la versión sea estable y demostrable.

## Entregables

1. Especificación y contratos aprobados por el equipo.
2. Documentación `docs/` coherente y README raíz actualizado.
3. Pull Requests revisados e integrados.
4. Registro de pruebas en `docs/pruebas-integracion.md` (archivo nuevo, sin GitHub Issues).
5. Versión desplegable y presentación demostrativa.

## Criterio de terminado

El proyecto compila con `mvn clean package`, despliega `BarberManager.war` en Tomcat 10.1, se ejecutan los flujos F01–F07 y no hay incongruencias entre pantallas, reglas y tablas.

## Forma de seguimiento SIN Issues

Cada integrante marca tareas con `[x]` en su README de trabajo y adjunta resultados/capturas en la descripción del Pull Request. El líder actualiza `docs/pruebas-integracion.md` y solicita correcciones por comentarios del PR. No usar GitHub Issues.

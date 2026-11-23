# BarberManager — README de Jybran (Base de datos)

**Rama:** `database-jybran`  
**Objetivo del rol:** definir, implementar y probar el esquema simplificado de **12 tablas**, garantizando las relaciones y reglas de integridad acordadas.

## Referencias

Leer `../modelo-entidad-relacion.md` y `../especificacion-funcional-tecnica.md` antes de escribir SQL. Confirmar con Alexander si el motor real es MySQL 8 o MariaDB de XAMPP; **no asumir que son idénticos**.

## Las únicas 12 tablas de la versión 1

1. `usuarios`
2. `clientes`
3. `barberos`
4. `servicios`
5. `citas`
6. `atenciones`
7. `productos`
8. `movimientos_inventario`
9. `ventas`
10. `detalle_ventas`
11. `pagos`
12. `comisiones`

No agregar tablas de roles, horarios, detalles de citas, proveedores o liquidaciones en v1 sin aprobación del líder.

## Actividades por entregas

### E0 — Diseño y contrato técnico
- [ ] Confirmar motor y versión: MySQL 8 o MariaDB de XAMPP; comunicar resultado al equipo.
- [ ] Revisar diagrama Mermaid de 12 tablas: cardinalidades, obligatoriedad, claves y relaciones opcionales.
- [ ] Completar diccionario de columnas con nombre, tipo SQL, longitud/precisión, nulos, valores permitidos y descripción.
- [ ] Acordar con Alexander nomenclatura `snake_case`, `BIGINT`, identificadores JPA y estrategia de generación.
- [ ] Documentar decisiones pendientes: ajuste de inventario y validaciones de horarios básicos.

### E1 — Seguridad, personas y catálogo
- [ ] Crear `usuarios` con username/email únicos, `password_hash`, rol fijo y estado activo.
- [ ] Crear `clientes` con FK opcional/única a `usuarios`.
- [ ] Crear `barberos` con FK opcional/única a usuarios, jornada simple, días laborales y porcentaje 0–100.
- [ ] Crear `servicios` con precio no negativo, duración positiva y activo.
- [ ] Validar inserts de ejemplo y relaciones uno-a-uno opcionales.

### E2 — Reservas y atenciones
- [ ] Crear `citas` con FKs a cliente, barbero y servicio; inicio, fin, precio histórico y estado.
- [ ] Añadir índice para consultas de disponibilidad por barbero y rango de fechas.
- [ ] Crear `atenciones` con `cita_id` único, fecha real de inicio y finalización opcional.
- [ ] Definir restricciones `fin > inicio`, valores permitidos de estados y reglas de eliminación.
- [ ] Coordinar con Alexander la comprobación transaccional de solapamientos: **no basta una clave UNIQUE**.

### E3 — Productos, ventas, cobros simulados y comisiones
- [ ] Crear `productos` con categoría textual, precio, stock y stock mínimo no negativos.
- [ ] Crear `movimientos_inventario` y definir inequívocamente entrada, salida y ajuste; FK a producto y usuario.
- [ ] Crear `ventas` con cliente opcional, atención opcional/única, usuario que registra, número de ticket único, total y estado.
- [ ] Crear `detalle_ventas` con tipo `SERVICIO`/`PRODUCTO`, descripción y precio histórico; revisar restricción de exactamente un tipo de referencia.
- [ ] Crear `pagos` con métodos simulados, monto positivo, flag `simulado = TRUE` y FK a venta.
- [ ] Crear `comisiones` con FK a barbero y atención única, base, porcentaje histórico, importe y estado.
- [ ] Añadir índices sobre claves foráneas y búsquedas de citas/ventas según el motor elegido.

### E4 — Pruebas, datos iniciales y entrega
- [ ] Crear `database/schema.sql` repetible en una BD de prueba limpia y documentar orden de ejecución.
- [ ] Crear `database/seed.sql` con roles como valores de `usuarios.rol`, servicios, barberos, clientes y productos de prueba, sin credenciales reales.
- [ ] Probar inserciones válidas y rechazos por FK, UNIQUE, cantidades negativas y restricciones compatibles con el motor.
- [ ] Probar consultas JOIN de agenda, historial, ticket, inventario, comisiones y dashboard.
- [ ] Revisar integridad junto con Alexander, corregir diferencias JPA/DDL y documentar el método de respaldo/restauración local.
- [ ] Confirmar que ningún script SQL contenga contraseñas reales ni datos personales reales.

## Entregables

1. `docs/modelo-entidad-relacion.md` actualizado con diagrama aprobado y diccionario completo.
2. `database/schema.sql` con exactamente las 12 tablas definidas.
3. `database/seed.sql` con datos académicos de prueba.
4. Instrucciones para crear/recrear la base local y ejecutar scripts.
5. Evidencia de restricciones, relaciones e integración con las entidades JPA.

## Criterios de aceptación

- Las 12 tablas se crean sin errores en el motor confirmado.
- Las FK/UNIQUE/CHECK disponibles protegen la integridad; las reglas que requieren concurrencia se validan en el backend.
- Los precios usan `DECIMAL(10,2)`, el porcentaje `DECIMAL(5,2)` y las fechas son coherentes con `America/Lima`.
- Los tickets se identifican de manera única, pero **no son documentos tributarios**.
- Existen datos de prueba suficientes para demostrar flujos F01–F07.

## Colaboración sin Issues

Trabaja en `database-jybran`. Marca `[x]` las actividades completadas y abre Pull Requests hacia `develop`. No modifiques modelos Java de Alexander sin coordinarlo previamente.

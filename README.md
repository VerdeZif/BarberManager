# BarberManager

**Sistema Web de Gestión Integral de Barbería**

BarberManager es un sistema web desarrollado con Java y Jakarta EE que permite administrar los procesos de una barbería, incluyendo la gestión de clientes, barberos, servicios, reservas, atenciones, inventario, ventas, pagos, comisiones y reportes.

## Objetivo general

Desarrollar un sistema web que permita automatizar y optimizar los procesos administrativos y operativos de una barbería, mejorando la organización de las reservas, el control de los servicios y la gestión comercial.

## Tecnologías utilizadas

| Tecnología | Uso |
|---|---|
| Java 17 | Lenguaje de programación |
| Jakarta EE / Servlets 6.0 | Desarrollo backend |
| JSP y JSTL | Vistas web |
| HTML, CSS, JavaScript | Interfaz de usuario |
| Bootstrap 5 | Diseño responsivo |
| JPA / Hibernate | Persistencia de datos |
| MySQL 8 / MariaDB compatible | Base de datos |
| Maven | Gestión de dependencias |
| Apache Tomcat 10.1 | Servidor de aplicaciones web |
| IntelliJ IDEA | Entorno de desarrollo |
| Git y GitHub | Control de versiones |

## Integrantes

| Integrante | Rol |
|---|---|
| Israel | Líder del proyecto |
| Gerald | Desarrollador Frontend |
| Alkexander | Desarrollador Backend |
| Jybran | Administrador de Base de Datos |

## Módulos del sistema

1. Autenticación y gestión de usuarios.
2. Gestión de clientes.
3. Gestión de barberos.
4. Gestión de servicios.
5. Gestión de citas y reservas.
6. Gestión de atenciones.
7. Gestión de inventario.
8. Ventas y pagos.
9. Gestión de comisiones.
10. Dashboard y reportes.

## Funcionalidades principales

- Portal público para consultar servicios y realizar reservas.
- Autenticación y autorización por roles.
- Gestión de clientes y barberos.
- Control de horarios y disponibilidad.
- Registro de atenciones.
- Administración de productos e inventario.
- Registro manual de pagos mediante efectivo, Yape, Plin y tarjeta.
- Cálculo de comisiones.
- Reportes administrativos.

## Arquitectura del proyecto

Se utilizará el patrón MVC y una arquitectura organizada por capas.

- `controller`: Servlets y manejo de solicitudes HTTP.
- `model`: entidades JPA.
- `service`: lógica de negocio.
- `dao`: acceso a datos.
- `filter`: autenticación y autorización.
- `util`: utilidades y configuración.
- `webapp`: JSP, CSS, JavaScript e imágenes.

## Requisitos

- JDK 17.
- Maven 3.9 o superior.
- Apache Tomcat 10.1.
- MySQL 8 o MariaDB compatible.
- IntelliJ IDEA.
- Git.

## Instalación

1. Clonar el repositorio:

   `git clone https://github.com/TU_USUARIO/BarberManager.git`

2. Abrir la carpeta `BarberManager` en IntelliJ IDEA.
3. Configurar JDK 17.
4. Cargar las dependencias de Maven.
5. Preparar la base de datos con los scripts de `database` cuando estén disponibles.
6. Configurar la conexión local a la base de datos.
7. Compilar el proyecto:

   `mvn clean package`

8. Desplegar `target/BarberManager.war` en Tomcat 10.1.
9. Abrir `http://localhost:8080/BarberManager/`.

## Ramas de GitHub

| Rama | Descripción |
|---|---|
| `main` | Versión estable |
| `develop` | Integración de funcionalidades |
| `frontend-gerald` | Desarrollo frontend |
| `backend-alkexander` | Desarrollo backend |
| `database-jybran` | Desarrollo de base de datos |

## Reglas de colaboración

1. Cada integrante debe trabajar en su rama asignada.
2. No se permite modificar directamente `main`.
3. Los cambios se integrarán mediante Pull Requests hacia `develop`.
4. El líder revisará los cambios antes de integrarlos.
5. Los commits deben tener mensajes descriptivos.
6. No se deben subir contraseñas ni credenciales.
7. Los cambios que afecten varias áreas deberán coordinarse con los responsables.

## Estado del proyecto

**En desarrollo.**

Proyecto académico desarrollado por un equipo de cuatro integrantes.
-- Esquema compartido por Jybran para referencia del backend (MySQL 8).
-- No sustituye database/schema.sql ni confirma que esté desplegado o aprobado.
-- Los comentarios de estados de citas y ventas deben reconciliarse con la
-- especificación funcional antes de considerar definitivos esos valores.

CREATE DATABASE IF NOT EXISTS barbermanager
CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE barbermanager;

CREATE TABLE usuarios (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(120) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    rol VARCHAR(20) NOT NULL CHECK (rol IN ('ADMINISTRADOR', 'RECEPCIONISTA', 'BARBERO', 'CLIENTE')),
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB;

CREATE TABLE clientes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    usuario_id BIGINT NULL UNIQUE,
    nombres VARCHAR(100) NOT NULL,
    apellidos VARCHAR(100) NOT NULL,
    telefono VARCHAR(20) NULL,
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    CONSTRAINT fk_cliente_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE SET NULL
) ENGINE=InnoDB;

CREATE TABLE barberos (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    usuario_id BIGINT NULL UNIQUE,
    nombres VARCHAR(100) NOT NULL,
    apellidos VARCHAR(100) NOT NULL,
    dias_laborables VARCHAR(20) NOT NULL, -- Ej: '1,2,3,4,5,6'
    hora_inicio TIME NOT NULL,
    hora_fin TIME NOT NULL,
    porcentaje_comision DECIMAL(5,2) NOT NULL CHECK (porcentaje_comision >= 0 AND porcentaje_comision <= 100),
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    CONSTRAINT fk_barbero_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE SET NULL,
    CONSTRAINT chk_horario_valido CHECK (hora_fin > hora_inicio)
) ENGINE=InnoDB;

CREATE TABLE servicios (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    descripcion TEXT NULL,
    precio DECIMAL(10,2) NOT NULL CHECK (precio >= 0),
    duracion_minutos INT NOT NULL CHECK (duracion_minutos > 0),
    activo BOOLEAN NOT NULL DEFAULT TRUE
) ENGINE=InnoDB;

CREATE TABLE citas (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    cliente_id BIGINT NOT NULL,
    barbero_id BIGINT NOT NULL,
    servicio_id BIGINT NOT NULL,
    inicio DATETIME NOT NULL,
    fin DATETIME NOT NULL,
    precio_reservado DECIMAL(10,2) NOT NULL,
    estado VARCHAR(20) NOT NULL, -- Ej: PENDIENTE, CONFIRMADA, ATENDIDA, CANCELADA
    creada_en TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_cita_cliente FOREIGN KEY (cliente_id) REFERENCES clientes(id),
    CONSTRAINT fk_cita_barbero FOREIGN KEY (barbero_id) REFERENCES barberos(id),
    CONSTRAINT fk_cita_servicio FOREIGN KEY (servicio_id) REFERENCES servicios(id),
    CONSTRAINT chk_cita_horario CHECK (fin > inicio),
    INDEX idx_citas_agenda (barbero_id, inicio, fin, estado)
) ENGINE=InnoDB;

CREATE TABLE atenciones (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    cita_id BIGINT NOT NULL UNIQUE,
    inicio_real DATETIME NOT NULL,
    fin_real DATETIME NULL,
    observaciones TEXT NULL,
    CONSTRAINT fk_atencion_cita FOREIGN KEY (cita_id) REFERENCES citas(id),
    CONSTRAINT chk_atencion_horario CHECK (fin_real IS NULL OR fin_real > inicio_real)
) ENGINE=InnoDB;

CREATE TABLE productos (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    categoria VARCHAR(50) NOT NULL,
    precio_venta DECIMAL(10,2) NOT NULL CHECK (precio_venta >= 0),
    stock_actual INT NOT NULL DEFAULT 0 CHECK (stock_actual >= 0),
    stock_minimo INT NOT NULL DEFAULT 0 CHECK (stock_minimo >= 0),
    activo BOOLEAN NOT NULL DEFAULT TRUE
) ENGINE=InnoDB;

CREATE TABLE movimientos_inventario (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    producto_id BIGINT NOT NULL,
    usuario_id BIGINT NOT NULL,
    tipo VARCHAR(20) NOT NULL, -- ENTRADA, SALIDA, AJUSTE_ENTRADA, AJUSTE_SALIDA
    cantidad INT NOT NULL CHECK (cantidad > 0),
    fecha TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    motivo VARCHAR(255) NULL,
    CONSTRAINT fk_mov_producto FOREIGN KEY (producto_id) REFERENCES productos(id),
    CONSTRAINT fk_mov_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios(id)
) ENGINE=InnoDB;

CREATE TABLE ventas (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    cliente_id BIGINT NULL,
    atencion_id BIGINT NULL UNIQUE,
    usuario_id BIGINT NOT NULL,
    numero_ticket VARCHAR(30) NOT NULL UNIQUE,
    total DECIMAL(10,2) NOT NULL CHECK (total >= 0),
    estado_pago VARCHAR(20) NOT NULL, -- PENDIENTE, PAGADA, ANULADA
    fecha TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_venta_cliente FOREIGN KEY (cliente_id) REFERENCES clientes(id),
    CONSTRAINT fk_venta_atencion FOREIGN KEY (atencion_id) REFERENCES atenciones(id),
    CONSTRAINT fk_venta_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios(id)
) ENGINE=InnoDB;

CREATE TABLE detalle_ventas (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    venta_id BIGINT NOT NULL,
    tipo VARCHAR(20) NOT NULL CHECK (tipo IN ('SERVICIO', 'PRODUCTO')),
    servicio_id BIGINT NULL,
    producto_id BIGINT NULL,
    descripcion VARCHAR(150) NOT NULL,
    cantidad INT NOT NULL CHECK (cantidad > 0),
    precio_unitario DECIMAL(10,2) NOT NULL CHECK (precio_unitario >= 0),
    subtotal DECIMAL(10,2) NOT NULL CHECK (subtotal >= 0),
    CONSTRAINT fk_detalle_venta FOREIGN KEY (venta_id) REFERENCES ventas(id) ON DELETE CASCADE,
    CONSTRAINT fk_detalle_servicio FOREIGN KEY (servicio_id) REFERENCES servicios(id),
    CONSTRAINT fk_detalle_producto FOREIGN KEY (producto_id) REFERENCES productos(id),
    CONSTRAINT chk_detalle_tipo CHECK (
        (tipo = 'SERVICIO' AND servicio_id IS NOT NULL AND producto_id IS NULL) OR
        (tipo = 'PRODUCTO' AND producto_id IS NOT NULL AND servicio_id IS NULL)
    )
) ENGINE=InnoDB;

CREATE TABLE pagos (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    venta_id BIGINT NOT NULL,
    metodo VARCHAR(20) NOT NULL CHECK (metodo IN ('EFECTIVO', 'YAPE', 'PLIN', 'TARJETA')),
    monto DECIMAL(10,2) NOT NULL CHECK (monto > 0),
    simulado BOOLEAN NOT NULL DEFAULT TRUE,
    fecha TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_pago_venta FOREIGN KEY (venta_id) REFERENCES ventas(id)
) ENGINE=InnoDB;

CREATE TABLE comisiones (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    barbero_id BIGINT NOT NULL,
    atencion_id BIGINT NOT NULL UNIQUE,
    base_servicio DECIMAL(10,2) NOT NULL,
    porcentaje DECIMAL(5,2) NOT NULL,
    monto DECIMAL(10,2) NOT NULL,
    estado VARCHAR(20) NOT NULL, -- PENDIENTE, PAGADA
    fecha_generacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_pago TIMESTAMP NULL,
    CONSTRAINT fk_comision_barbero FOREIGN KEY (barbero_id) REFERENCES barberos(id),
    CONSTRAINT fk_comision_atencion FOREIGN KEY (atencion_id) REFERENCES atenciones(id)
) ENGINE=InnoDB;

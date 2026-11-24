USE barbermanager;

-- Insertar Usuarios (Contraseñas en texto plano para pruebas: "123456")
-- En producción, Alkexander debe implementar el hash en Java.
INSERT INTO usuarios (username, email, password_hash, rol, activo) VALUES
('admin', 'admin@barber.com', '123456', 'ADMINISTRADOR', TRUE),
('recepcion', 'recepcion@barber.com', '123456', 'RECEPCIONISTA', TRUE),
('barbero1', 'barbero1@barber.com', '123456', 'BARBERO', TRUE),
('cliente1', 'cliente1@correo.com', '123456', 'CLIENTE', TRUE);

-- Insertar Clientes
INSERT INTO clientes (usuario_id, nombres, apellidos, telefono) VALUES
(4, 'Jybran', 'DBA', '999888777'), -- Cliente con cuenta
(NULL, 'Cliente', 'Invitado', '111222333'); -- Cliente sin cuenta

-- Insertar Barberos
INSERT INTO barberos (usuario_id, nombres, apellidos, dias_laborables, hora_inicio, hora_fin, porcentaje_comision) VALUES
(3, 'Carlos', 'Pérez', '1,2,3,4,5,6', '09:00:00', '18:00:00', 30.00);

-- Insertar Servicios
INSERT INTO servicios (nombre, descripcion, precio, duracion_minutos) VALUES
('Corte de Cabello Clásico', 'Corte con tijera y máquina', 25.00, 30),
('Corte + Barba', 'Corte completo y perfilado de barba', 40.00, 45),
('Afeitado Premium', 'Afeitado con toalla caliente', 20.00, 25);

-- Insertar Productos
INSERT INTO productos (nombre, categoria, precio_venta, stock_actual, stock_minimo) VALUES
('Cera para Cabello', 'Styling', 35.00, 20, 5),
('Shampoo Anticaspa', 'Cuidado', 45.00, 15, 3),
('Aceite para Barba', 'Cuidado', 50.00, 10, 2);

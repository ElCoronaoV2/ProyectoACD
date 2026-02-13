-- ==========================================
-- SCRIPT DE CREACIÓN DE USUARIOS DE PRUEBA
-- Contraseña para todos: '123456'
-- Hash Bcrypt: $2b$12$Nq.K8eX/E8WnJ.v7c.e5.u4.w4.z4.y4.x4.w4.v4.u4.t4.s4
-- ==========================================

-- 1. CREAR NUEVOS CEOs (Dueños de restaurantes)
INSERT INTO usuarios (nombre, email, password, enabled, rol, fecha_creacion, telefono)
VALUES 
('Propietario Valencia', 'ceo_valencia@test.com', '$2b$12$GYVGwUual1Aco.AZ2mqjV.2CyoOXRzVUZUaZbvoNS.NCwIwPjKvpC', true, 'CEO', NOW(), '600111222'),
('Propietario Norte', 'ceo_norte@test.com', '$2b$12$GYVGwUual1Aco.AZ2mqjV.2CyoOXRzVUZUaZbvoNS.NCwIwPjKvpC', true, 'CEO', NOW(), '600333444')
ON CONFLICT (email) DO NOTHING;

-- 2. REASIGNAR RESTAURANTES A LOS NUEVOS CEOs
-- La Paella Valenciana -> Propietario Valencia
UPDATE locales 
SET propietario_id = (SELECT id FROM usuarios WHERE email = 'ceo_valencia@test.com')
WHERE nombre = 'La Paella Valenciana';

-- Restaurante Arzak -> Propietario Norte
UPDATE locales 
SET propietario_id = (SELECT id FROM usuarios WHERE email = 'ceo_norte@test.com')
WHERE nombre = 'Restaurante Arzak';

-- (El resto se quedan con el CeoElPuerto original o quien fuera)


-- 3. CREAR EMPLEADOS (Uno por cada restaurante nuevo)

-- Empleado para La Paella Valenciana
INSERT INTO usuarios (nombre, email, password, enabled, rol, fecha_creacion, restaurante_trabajo_id, telefono)
VALUES 
('Camarero Paella', 'emp_paella@test.com', '$2b$12$GYVGwUual1Aco.AZ2mqjV.2CyoOXRzVUZUaZbvoNS.NCwIwPjKvpC', true, 'EMPLEADO', NOW(), 
(SELECT id FROM locales WHERE nombre = 'La Paella Valenciana'), '666000001');

-- Empleado para Asador de Aranda
INSERT INTO usuarios (nombre, email, password, enabled, rol, fecha_creacion, restaurante_trabajo_id, telefono)
VALUES 
('Camarero Asador', 'emp_asador@test.com', '$2b$12$GYVGwUual1Aco.AZ2mqjV.2CyoOXRzVUZUaZbvoNS.NCwIwPjKvpC', true, 'EMPLEADO', NOW(), 
(SELECT id FROM locales WHERE nombre = 'Asador de Aranda'), '666000002');

-- Empleado para Can Culleretes
INSERT INTO usuarios (nombre, email, password, enabled, rol, fecha_creacion, restaurante_trabajo_id, telefono)
VALUES 
('Camarero Can', 'emp_can@test.com', '$2b$12$GYVGwUual1Aco.AZ2mqjV.2CyoOXRzVUZUaZbvoNS.NCwIwPjKvpC', true, 'EMPLEADO', NOW(), 
(SELECT id FROM locales WHERE nombre = 'Can Culleretes'), '666000003');

-- Empleado para El Rinconcillo
INSERT INTO usuarios (nombre, email, password, enabled, rol, fecha_creacion, restaurante_trabajo_id, telefono)
VALUES 
('Camarero Rinconcillo', 'emp_rinconcillo@test.com', '$2b$12$GYVGwUual1Aco.AZ2mqjV.2CyoOXRzVUZUaZbvoNS.NCwIwPjKvpC', true, 'EMPLEADO', NOW(), 
(SELECT id FROM locales WHERE nombre = 'El Rinconcillo'), '666000004');

-- Empleado para Restaurante Arzak
INSERT INTO usuarios (nombre, email, password, enabled, rol, fecha_creacion, restaurante_trabajo_id, telefono)
VALUES 
('Camarero Arzak', 'emp_arzak@test.com', '$2b$12$GYVGwUual1Aco.AZ2mqjV.2CyoOXRzVUZUaZbvoNS.NCwIwPjKvpC', true, 'EMPLEADO', NOW(), 
(SELECT id FROM locales WHERE nombre = 'Restaurante Arzak'), '666000005');

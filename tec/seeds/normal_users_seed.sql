-- ==========================================
-- SCRIPT DE CREACIÓN DE USUARIOS NORMALES (CLIENTES)
-- Contraseña para todos: '123456'
-- Hash Bcrypt: $2b$12$GYVGwUual1Aco.AZ2mqjV.2CyoOXRzVUZUaZbvoNS.NCwIwPjKvpC
-- ==========================================

INSERT INTO usuarios (nombre, email, password, enabled, rol, fecha_creacion, telefono)
VALUES 
('Usuario Uno', 'usuario1@test.com', '$2b$12$GYVGwUual1Aco.AZ2mqjV.2CyoOXRzVUZUaZbvoNS.NCwIwPjKvpC', true, 'USER', NOW(), '600000001'),
('Usuario Dos', 'usuario2@test.com', '$2b$12$GYVGwUual1Aco.AZ2mqjV.2CyoOXRzVUZUaZbvoNS.NCwIwPjKvpC', true, 'USER', NOW(), '600000002'),
('Usuario Tres', 'usuario3@test.com', '$2b$12$GYVGwUual1Aco.AZ2mqjV.2CyoOXRzVUZUaZbvoNS.NCwIwPjKvpC', true, 'USER', NOW(), '600000003')
ON CONFLICT (email) DO NOTHING;

-- Usuarios iniciales (Password: 123456)
-- Hash: $2b$12$b6F68AlmU9M9PetxcTkhlOYhN/1E8canEb3aYUagVdZDzXGq8ibKC

INSERT INTO usuarios (email, password, nombre, telefono, rol, enabled, fecha_creacion, ultimo_acceso)
VALUES
('admin@restaurant.com', '$2b$12$b6F68AlmU9M9PetxcTkhlOYhN/1E8canEb3aYUagVdZDzXGq8ibKC', 'Administrador', '000000000', 'DIRECTOR', true, NOW(), NOW()),
('director@restaurant.com', '$2b$12$b6F68AlmU9M9PetxcTkhlOYhN/1E8canEb3aYUagVdZDzXGq8ibKC', 'Director General', '600111222', 'DIRECTOR', true, NOW(), NOW()),
('ceo@restaurant.com', '$2b$12$b6F68AlmU9M9PetxcTkhlOYhN/1E8canEb3aYUagVdZDzXGq8ibKC', 'CEO Restaurant Chain', '600333444', 'CEO', true, NOW(), NOW()),
('empleado@restaurant.com', '$2b$12$b6F68AlmU9M9PetxcTkhlOYhN/1E8canEb3aYUagVdZDzXGq8ibKC', 'Empleado Staff', '600555666', 'EMPLEADO', true, NOW(), NOW()),
('cliente@restaurant.com', '$2b$12$b6F68AlmU9M9PetxcTkhlOYhN/1E8canEb3aYUagVdZDzXGq8ibKC', 'Cliente Habitual', '600777888', 'USER', true, NOW(), NOW())
ON CONFLICT (email) DO NOTHING;

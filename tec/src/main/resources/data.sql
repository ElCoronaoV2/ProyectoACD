-- Update CEO email
UPDATE usuarios SET email = 'sergio.bernal.mz@gmail.com' WHERE id = 187;

-- Assign 4 restaurants to this CEO
UPDATE locales SET propietario_id = 187 WHERE nombre IN ('La Paella Valenciana', 'Asador de Aranda', 'Can Culleretes', 'El Rinconcillo');

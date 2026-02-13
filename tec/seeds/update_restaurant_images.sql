-- Update restaurant images to use local paths instead of Unsplash URLs
-- This script updates the 5 main restaurants with the new locally hosted images

UPDATE locales SET imagen_url = '/images/paella_valenciana.png' WHERE nombre = 'La Paella Valenciana';
UPDATE locales SET imagen_url = '/images/asador_aranda.png' WHERE nombre = 'Asador de Aranda';
UPDATE locales SET imagen_url = '/images/can_culleretes.png' WHERE nombre = 'Can Culleretes';
UPDATE locales SET imagen_url = '/images/el_rinconcillo.png' WHERE nombre = 'El Rinconcillo';
UPDATE locales SET imagen_url = '/images/restaurante_arzak.png' WHERE nombre = 'Restaurante Arzak';

-- Verify the updates
SELECT id, nombre, imagen_url FROM locales WHERE nombre IN ('La Paella Valenciana', 'Asador de Aranda', 'Can Culleretes', 'El Rinconcillo', 'Restaurante Arzak');

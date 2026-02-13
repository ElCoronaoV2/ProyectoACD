-- Programar menús para HOY para que aparezcan en la carta

-- La Paella Valenciana - Menú Fallero
INSERT INTO menu_programacion (local_id, menu_id, fecha)
VALUES (
    (SELECT id FROM locales WHERE nombre = 'La Paella Valenciana'),
    (SELECT id FROM menus WHERE nombre = 'Menú Fallero' AND local_id = (SELECT id FROM locales WHERE nombre = 'La Paella Valenciana')),
    CURRENT_DATE
) ON CONFLICT (local_id, fecha) DO NOTHING;

-- Asador de Aranda - Menú Lechazo
INSERT INTO menu_programacion (local_id, menu_id, fecha)
VALUES (
    (SELECT id FROM locales WHERE nombre = 'Asador de Aranda'),
    (SELECT id FROM menus WHERE nombre = 'Menú Lechazo' AND local_id = (SELECT id FROM locales WHERE nombre = 'Asador de Aranda')),
    CURRENT_DATE
) ON CONFLICT (local_id, fecha) DO NOTHING;

-- Can Culleretes - Menú Costa Brava
INSERT INTO menu_programacion (local_id, menu_id, fecha)
VALUES (
    (SELECT id FROM locales WHERE nombre = 'Can Culleretes'),
    (SELECT id FROM menus WHERE nombre = 'Menú Costa Brava' AND local_id = (SELECT id FROM locales WHERE nombre = 'Can Culleretes')),
    CURRENT_DATE
) ON CONFLICT (local_id, fecha) DO NOTHING;

-- El Rinconcillo - Menú Feria
INSERT INTO menu_programacion (local_id, menu_id, fecha)
VALUES (
    (SELECT id FROM locales WHERE nombre = 'El Rinconcillo'),
    (SELECT id FROM menus WHERE nombre = 'Menú Feria' AND local_id = (SELECT id FROM locales WHERE nombre = 'El Rinconcillo')),
    CURRENT_DATE
) ON CONFLICT (local_id, fecha) DO NOTHING;

-- Restaurante Arzak - Menú Donostia
INSERT INTO menu_programacion (local_id, menu_id, fecha)
VALUES (
    (SELECT id FROM locales WHERE nombre = 'Restaurante Arzak'),
    (SELECT id FROM menus WHERE nombre = 'Menú Donostia' AND local_id = (SELECT id FROM locales WHERE nombre = 'Restaurante Arzak')),
    CURRENT_DATE
) ON CONFLICT (local_id, fecha) DO NOTHING;

-- Restaurantes (Fuera de Alicante) - ID Propietario: 187 (CeoElPuerto)

-- 1. La Paella Valenciana (Valencia)
INSERT INTO locales (nombre, direccion, capacidad, capacidad_comida, capacidad_cena, horario, apertura_comida, cierre_comida, apertura_cena, cierre_cena, latitud, longitud, pos_x, pos_y, imagen_url, propietario_id)
VALUES ('La Paella Valenciana', 'Carrer de Sueca, 45, 46006 València, Valencia', 80, 40, 40, 'Lun-Dom: 13:00-16:00, 20:00-23:30', '13:00', '16:00', '20:00', '23:30', 39.4619, -0.3756, 50.0, 50.0, '/images/paella_valenciana.png', 187);

-- 2. Asador de Aranda (Madrid)
INSERT INTO locales (nombre, direccion, capacidad, capacidad_comida, capacidad_cena, horario, apertura_comida, cierre_comida, apertura_cena, cierre_cena, latitud, longitud, pos_x, pos_y, imagen_url, propietario_id)
VALUES ('Asador de Aranda', 'C. de Preciados, 42, 28013 Madrid', 120, 60, 60, 'Lun-Dom: 13:00-16:30, 20:30-00:00', '13:00', '16:30', '20:30', '00:00', 40.4196, -3.7067, 45.0, 40.0, '/images/asador_aranda.png', 187);

-- 3. Can Culleretes (Barcelona)
INSERT INTO locales (nombre, direccion, capacidad, capacidad_comida, capacidad_cena, horario, apertura_comida, cierre_comida, apertura_cena, cierre_cena, latitud, longitud, pos_x, pos_y, imagen_url, propietario_id)
VALUES ('Can Culleretes', 'Carrer d''en Quintana, 5, 08002 Barcelona', 90, 45, 45, 'Mar-Dom: 13:30-16:00, 20:00-23:00', '13:30', '16:00', '20:00', '23:00', 41.3807, 2.1764, 60.0, 30.0, '/images/can_culleretes.png', 187);

-- 4. El Rinconcillo (Sevilla)
INSERT INTO locales (nombre, direccion, capacidad, capacidad_comida, capacidad_cena, horario, apertura_comida, cierre_comida, apertura_cena, cierre_cena, latitud, longitud, pos_x, pos_y, imagen_url, propietario_id)
VALUES ('El Rinconcillo', 'C. Gerona, 40, 41003 Sevilla', 60, 30, 30, 'Lun-Dom: 12:00-01:00', '13:00', '16:00', '20:00', '23:30', 37.3938, -5.9868, 30.0, 70.0, '/images/el_rinconcillo.png', 187);

-- 5. Restaurante Arzak (San Sebastián)
INSERT INTO locales (nombre, direccion, capacidad, capacidad_comida, capacidad_cena, horario, apertura_comida, cierre_comida, apertura_cena, cierre_cena, latitud, longitud, pos_x, pos_y, imagen_url, propietario_id)
VALUES ('Restaurante Arzak', 'Avenida del, Alcalde J. Elosegi Hiribidea, 273, 20015 Donostia, Gipuzkoa', 50, 25, 25, 'Mar-Sab: 13:15-15:30, 20:45-22:30', '13:15', '15:30', '20:45', '22:30', 43.3183, -1.9542, 40.0, 10.0, '/images/restaurante_arzak.png', 187);

-- Menús (10 Menús variados)

-- Menú 1: Valenciano Tradicional (Local: La Paella Valenciana)
INSERT INTO menus (nombre, descripcion, precio, ingredientes, alergenos, disponible, propietario_id, local_id, primer_plato, primer_plato_desc, primer_plato_ingredientes, segundo_plato, segundo_plato_desc, segundo_plato_ingredientes, postre, postre_desc, postre_ingredientes)
VALUES ('Menú Fallero', 'Auténtica experiencia valenciana', 28.50, 'Arroz, pollo, conejo, judía ferradura, garrofó, naranja', 'Gluten', true, 187, (SELECT id FROM locales WHERE nombre = 'La Paella Valenciana'),
'Esgarraet Valenciano', 'Pimiento rojo asado con bacalao', 'Pimiento, bacalao, ajo, aceite de oliva',
'Paella Valenciana', 'La receta tradicional con leña de naranjo', 'Arroz bomba, pollo, conejo, bajoqueta, garrofó',
'Naranja Preparada', 'Naranja valenciana con miel y nueces', 'Naranja, miel, nueces');

-- Menú 2: Asado Castellano (Local: Asador de Aranda)
INSERT INTO menus (nombre, descripcion, precio, ingredientes, alergenos, disponible, propietario_id, local_id, primer_plato, primer_plato_desc, primer_plato_ingredientes, segundo_plato, segundo_plato_desc, segundo_plato_ingredientes, postre, postre_desc, postre_ingredientes)
VALUES ('Menú Lechazo', 'Sabor tradicional de Castilla', 45.00, 'Cordero lechal, morcilla, ensalada', 'Sulfitos', true, 187, (SELECT id FROM locales WHERE nombre = 'Asador de Aranda'),
'Morcilla de Burgos', 'Frita con pimientos', 'Morcilla, arroz, pimiento',
'Cuarto de Lechazo', 'Asado en horno de leña', 'Cordero lechal, agua, sal',
'Hojaldre de Crema', 'Postre casero templado', 'Hojaldre, crema pastelera, azúcar glass');

-- Menú 3: Catalán Moderno (Local: Can Culleretes)
INSERT INTO menus (nombre, descripcion, precio, ingredientes, alergenos, disponible, propietario_id, local_id, primer_plato, primer_plato_desc, primer_plato_ingredientes, segundo_plato, segundo_plato_desc, segundo_plato_ingredientes, postre, postre_desc, postre_ingredientes)
VALUES ('Menú Costa Brava', 'Mar y montaña en tu plato', 32.00, 'Gambas, pollo, chocolate', 'Crustáceos, Frutos de cáscara', true, 187, (SELECT id FROM locales WHERE nombre = 'Can Culleretes'),
'Esqueixada de Bacalao', 'Ensalada fría de bacalao', 'Bacalao, tomate, cebolla, aceitunas negras',
'Mar y Montaña', 'Pollo con gambas', 'Pollo, gambas, salsa de almendras',
'Crema Catalana', 'Con azúcar quemado', 'Leche, yema de huevo, azúcar, canela');

-- Menú 4: Tapas Sevillanas (Local: El Rinconcillo)
INSERT INTO menus (nombre, descripcion, precio, ingredientes, alergenos, disponible, propietario_id, local_id, primer_plato, primer_plato_desc, primer_plato_ingredientes, segundo_plato, segundo_plato_desc, segundo_plato_ingredientes, postre, postre_desc, postre_ingredientes)
VALUES ('Menú Feria', 'Selección de las mejores tapas', 25.00, 'Jamón, queso, pescado frito', 'Pescado, Gluten, Lácteos', true, 187, (SELECT id FROM locales WHERE nombre = 'El Rinconcillo'),
'Tabla de Ibéricos', 'Jamón de bellota y queso manchego', 'Jamón ibérico, queso curado',
'Fritura Variada', 'Pescaito frito a la andaluza', 'Boquerones, calamares, adobo',
'Tocino de Cielo', 'Dulce tradicional de yema', 'Yema de huevo, azúcar, caramelo');

-- Menú 5: Degustación vasca (Local: Restaurante Arzak)
INSERT INTO menus (nombre, descripcion, precio, ingredientes, alergenos, disponible, propietario_id, local_id, primer_plato, primer_plato_desc, primer_plato_ingredientes, segundo_plato, segundo_plato_desc, segundo_plato_ingredientes, postre, postre_desc, postre_ingredientes)
VALUES ('Menú Donostia', 'Alta cocina vasca', 85.00, 'Merluza, kokotxas, idiazabal', 'Pescado, Lácteos', true, 187, (SELECT id FROM locales WHERE nombre = 'Restaurante Arzak'),
'Kokotxas al Pil-Pil', 'Clásico de la cocina vasca', 'Kokotxas de merluza, ajo, guindilla, aceite',
'Txuleta a la Bradsa', 'Carne roja con pimientos de Padrón', 'Chuleta de vaca vieja, sal maldón',
'Pantxineta', 'Hojaldre relleno de crema y almendras', 'Hojaldre, crema, almendras tostadas');

-- Menú 6: Vegetariano Gourmet (General - Sin local específico, asignado al CEO)
INSERT INTO menus (nombre, descripcion, precio, ingredientes, alergenos, disponible, propietario_id, local_id, primer_plato, primer_plato_desc, primer_plato_ingredientes, segundo_plato, segundo_plato_desc, segundo_plato_ingredientes, postre, postre_desc, postre_ingredientes)
VALUES ('Menú Verde', 'Opción saludable y deliciosa', 22.00, 'Verduras de temporada, tofu, frutas', 'Soja', true, 187, (SELECT id FROM locales WHERE nombre = 'La Paella Valenciana'),
'Crema de Calabaza', 'Con pipas y aceite de trufa', 'Calabaza, patata, nata vegetal, pipas',
'Lasaña de Verduras', 'Sin pasta, con láminas de calabacín', 'Calabacín, berenjena, tomate, queso vegano',
'Brocheta de Frutas', 'Con chocolate negro fundido', 'Fresas, plátano, kiwi, chocolate 70%');

-- Menú 7: Infantil (Asignado a Asador de Aranda)
INSERT INTO menus (nombre, descripcion, precio, ingredientes, alergenos, disponible, propietario_id, local_id, primer_plato, primer_plato_desc, primer_plato_ingredientes, segundo_plato, segundo_plato_desc, segundo_plato_ingredientes, postre, postre_desc, postre_ingredientes)
VALUES ('Menú Pequeños', 'Platos sencillos para niños', 12.00, 'Pasta, pollo, helado', 'Gluten, Lácteos', true, 187, (SELECT id FROM locales WHERE nombre = 'Asador de Aranda'),
'Macarrones con Tomate', 'Con queso rallado por encima', 'Pasta, tomate frito, queso emmental',
'Escalope de Pollo', 'Empanado con patatas fritas', 'Pechuga de pollo, pan rallado, huevo, patatas',
'Helado de Vainilla', 'Dos bolas con sirope', 'Leche, vainilla, azúcar');

-- Menú 8: Marinero Especial (Local: La Paella Valenciana)
INSERT INTO menus (nombre, descripcion, precio, ingredientes, alergenos, disponible, propietario_id, local_id, primer_plato, primer_plato_desc, primer_plato_ingredientes, segundo_plato, segundo_plato_desc, segundo_plato_ingredientes, postre, postre_desc, postre_ingredientes)
VALUES ('Menú del Mar', 'Sabores del Mediterráneo', 35.00, 'Marisco, pescado, arroz', 'Crustáceos, Moluscos, Pescado', true, 187, (SELECT id FROM locales WHERE nombre = 'La Paella Valenciana'),
'Clóchinas al Vapor', 'Mejillón valenciano de temporada', 'Clóchinas, limón, pimienta',
'Arroz a Banda', 'El arroz marinero por excelencia', 'Arroz, fumet de pescado, sepia, gambas',
'Horchata y Fartons', 'Dulce típico de Alboraya', 'Chufa, azúcar, harina');

-- Menú 9: Ejecutivo Express (Local: Asador de Aranda)
INSERT INTO menus (nombre, descripcion, precio, ingredientes, alergenos, disponible, propietario_id, local_id, primer_plato, primer_plato_desc, primer_plato_ingredientes, segundo_plato, segundo_plato_desc, segundo_plato_ingredientes, postre, postre_desc, postre_ingredientes)
VALUES ('Menú Ejecutivo', 'Rápido y de calidad', 18.00, 'Ensalada, carne, fruta', 'Mostaza', true, 187, (SELECT id FROM locales WHERE nombre = 'Asador de Aranda'),
'Ensalada Ilustrada', 'Con atún y huevo duro', 'Lechuga, tomate, atún, huevo, cebolla',
'Solomillo al Whisky', 'Con guarnición de arroz', 'Solomillo de cerdo, salsa whisky, ajo',
'Macedonia', 'Fruta fresca cortada', 'Manzana, pera, melocotón, zumo de naranja');

-- Menú 10: Romántico (Local: Can Culleretes)
INSERT INTO menus (nombre, descripcion, precio, ingredientes, alergenos, disponible, propietario_id, local_id, primer_plato, primer_plato_desc, primer_plato_ingredientes, segundo_plato, segundo_plato_desc, segundo_plato_ingredientes, postre, postre_desc, postre_ingredientes)
VALUES ('Menú Romántico', 'Para una velada especial', 60.00, 'Caviar, solomillo, fresas', 'Pescado, Lácteos', true, 187, (SELECT id FROM locales WHERE nombre = 'Can Culleretes'),
'Tostas de Salmón', 'Con queso crema y eneldo', 'Pan tostado, salmón ahumado, queso crema',
'Chateaubriand', 'Para compartir (precio por persona)', 'Solomillo de ternera centro, salsa bearnesa',
'Fresas con Nata', 'Clásico y delicioso', 'Fresones, nata montada, azúcar');

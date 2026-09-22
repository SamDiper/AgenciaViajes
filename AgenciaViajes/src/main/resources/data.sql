INSERT INTO categorias_destino (nombre) VALUES ('Playa');
INSERT INTO categorias_destino (nombre) VALUES ('Montaña');

-- Destinos
INSERT INTO destinos (nombre, pais, ciudad, descripcion, id_categoria, imagen_url, estado, fecha_registro)
VALUES ('Cartagena', 'Colombia', 'Cartagena', 'Ciudad amurallada con playas caribeñas y arquitectura colonial.', 1, 'https://plus.unsplash.com/premium_photo-1697730195920-86bc1a6eeab0', 'ACTIVO', CURRENT_TIMESTAMP);

INSERT INTO destinos (nombre, pais, ciudad, descripcion, id_categoria, imagen_url, estado, fecha_registro)
VALUES ('Salento', 'Colombia', 'Salento', 'Pueblo cafetero rodeado de montañas y palmas de cera.', 2, 'https://images.unsplash.com/photo-1697818634560-27ddca612f88', 'ACTIVO', CURRENT_TIMESTAMP);

INSERT INTO destinos (nombre, pais, ciudad, descripcion, id_categoria, imagen_url, estado, fecha_registro)
VALUES ('Santa Marta', 'Colombia', 'Santa Marta', 'Ciudad costera entre el mar Caribe y la imponente Sierra Nevada.', 1, 'https://images.unsplash.com/photo-1635079552384-dd8adecd8a7c', 'ACTIVO', CURRENT_TIMESTAMP);

-- Roles y Usuarios
INSERT INTO roles (nombre_rol) VALUES ('ADMIN'), ('CLIENTE');

INSERT INTO usuarios (contrasena_hash, correo, estado, fecha_creacion, nombre_usuario, id_rol)
VALUES ('$2b$10$NbVWHG0SQUA9K3maWmWG/us3OvkpllcfN.KRpxqnpNwfQokBhIKTG', 'andres@gmail.com', 'ACTIVO', CURRENT_TIMESTAMP, 'Andres Gonzalez', 1);

INSERT INTO clientes (correo, direccion, documento, estado, fecha_registro, nombre_completo, telefono, tipo_documento, id_usuario)
VALUES ('andres@gmail.com', 'Calle 10 #20-30, Bello, Antioquia', '1000123456', 'ACTIVO', CURRENT_TIMESTAMP, 'Andres Gonzalez', '3001234567', 'CC', 1);

-- Aerolíneas
INSERT INTO aerolineas (nombre, codigo_iata, precio_adicional) VALUES ('Wingo', 'P5', 0);
INSERT INTO aerolineas (nombre, codigo_iata, precio_adicional) VALUES ('Avianca', 'AV', 50000);
INSERT INTO aerolineas (nombre, codigo_iata, precio_adicional) VALUES ('LATAM Colombia', 'LA', 120000);
INSERT INTO aerolineas (nombre, codigo_iata, precio_adicional) VALUES ('Clic Air', 'VE', 80000);

-- Hoteles
INSERT INTO hoteles (nombre, descripcion, estrellas, precio_adicional, id_destino, estado)
VALUES ('Hotel Caribe By Faranda', 'Hotel histórico frente al mar en Bocagrande con piscina colonial.', 4, 250000, 1, 'ACTIVO');

INSERT INTO hoteles (nombre, descripcion, estrellas, precio_adicional, id_destino, estado)
VALUES ('Hotel Las Américas Resort', 'Complejo de lujo 5 estrellas con acceso directo a la playa.', 5, 550000, 1, 'ACTIVO');

INSERT INTO hoteles (nombre, descripcion, estrellas, precio_adicional, id_destino, estado)
VALUES ('Posada Campestre Cocora', 'Alojamiento rústico con vistas a la montaña y desayuno típico.', 3, 120000, 2, 'ACTIVO');

INSERT INTO hoteles (nombre, descripcion, estrellas, precio_adicional, id_destino, estado)
VALUES ('Hotel Boutique Reserva del Café', 'Hermoso hotel entre cafetales con spa y catas de café.', 4, 280000, 2, 'ACTIVO');

INSERT INTO hoteles (nombre, descripcion, estrellas, precio_adicional, id_destino, estado)
VALUES ('Ecohabs Tayrona', 'Cabañas ecológicas de lujo en la cima de la montaña frente al mar.', 5, 480000, 3, 'ACTIVO');

INSERT INTO hoteles (nombre, descripcion, estrellas, precio_adicional, id_destino, estado)
VALUES ('Hotel Tamacá Beach Resort', 'Hotel frente a la bahía de El Rodadero con piscina y restaurante.', 4, 220000, 3, 'ACTIVO');

-- Paquetes — Destino 1: Cartagena
INSERT INTO paquetes (nombre, descripcion, precio_base, duracion_dias, imagen_url, id_destino, id_aerolinea, id_hotel, estado, fecha_registro)
VALUES ('Islas del Rosario y Barú', 'Recorre la ciudad amurallada, el castillo San Felipe y las playas de arena blanca en las Islas del Rosario.', 1200000, 4, 'https://plus.unsplash.com/premium_photo-1697730195920-86bc1a6eeab0', 1, 1, 1, 'ACTIVO', CURRENT_TIMESTAMP);

INSERT INTO paquetes (nombre, descripcion, precio_base, duracion_dias, imagen_url, id_destino, id_aerolinea, id_hotel, estado, fecha_registro)
VALUES ('Cartagena Colonial y Caribe', 'Experiencia premium en hoteles boutique del centro histórico con paseo en catamarán al atardecer.', 3500000, 7, 'https://plus.unsplash.com/premium_photo-1697730195920-86bc1a6eeab0', 1, 3, 2, 'ACTIVO', CURRENT_TIMESTAMP);

-- Paquetes — Destino 2: Salento
INSERT INTO paquetes (nombre, descripcion, precio_base, duracion_dias, imagen_url, id_destino, id_aerolinea, id_hotel, estado, fecha_registro)
VALUES ('Valle del Cocora', 'Senderismo por el bosque de niebla, avistamiento de palmas de cera y recorrido guiado por el pueblo de Salento.', 980000, 3, 'https://images.unsplash.com/photo-1697818634560-27ddca612f88', 2, 1, 3, 'ACTIVO', CURRENT_TIMESTAMP);

INSERT INTO paquetes (nombre, descripcion, precio_base, duracion_dias, imagen_url, id_destino, id_aerolinea, id_hotel, estado, fecha_registro)
VALUES ('Ruta del Café y Tradición', 'Visita haciendas cafeteras con degustación de cafés especiales, cabalgata por la montaña y posada campestre.', 1500000, 5, 'https://images.unsplash.com/photo-1697818634560-27ddca612f88', 2, 2, 4, 'ACTIVO', CURRENT_TIMESTAMP);

-- Paquetes — Destino 3: Santa Marta
INSERT INTO paquetes (nombre, descripcion, precio_base, duracion_dias, imagen_url, id_destino, id_aerolinea, id_hotel, estado, fecha_registro)
VALUES ('Parque Tayrona', 'Explora las playas de Cabo San Juan, La Piscina y Arrecifes. Incluye entrada al parque y estadía en cabañas ecológicas.', 1100000, 3, 'https://images.unsplash.com/photo-1635079552384-dd8adecd8a7c', 3, 1, 5, 'ACTIVO', CURRENT_TIMESTAMP);

INSERT INTO paquetes (nombre, descripcion, precio_base, duracion_dias, imagen_url, id_destino, id_aerolinea, id_hotel, estado, fecha_registro)
VALUES ('Ciudad Perdida y Playas', 'Aventura de senderismo arqueológico a Ciudad Perdida combinado con descanso en las playas de Santa Marta.', 2800000, 8, 'https://images.unsplash.com/photo-1635079552384-dd8adecd8a7c', 3, 3, 6, 'ACTIVO', CURRENT_TIMESTAMP);

-- ¿QUÉ INCLUYE? (paquete_incluye)

-- Paquete 1: Islas del Rosario y Barú
INSERT INTO paquete_incluye (id_paquete, item) VALUES (1, 'Transporte ida y vuelta en lancha rápida');
INSERT INTO paquete_incluye (id_paquete, item) VALUES (1, 'Guía bilingüe certificado');
INSERT INTO paquete_incluye (id_paquete, item) VALUES (1, 'Almuerzo típico caribeño e hidratación');
INSERT INTO paquete_incluye (id_paquete, item) VALUES (1, 'Snorkel y careteo en arrecifes de coral');
INSERT INTO paquete_incluye (id_paquete, item) VALUES (1, 'Asistencia médica y seguro de viaje');

-- Paquete 2: Cartagena Colonial y Caribe
INSERT INTO paquete_incluye (id_paquete, item) VALUES (2, 'Vuelos ida y vuelta con aerolínea seleccionada');
INSERT INTO paquete_incluye (id_paquete, item) VALUES (2, 'Hospedaje en hotel boutique centro histórico');
INSERT INTO paquete_incluye (id_paquete, item) VALUES (2, 'Tour privado en catamarán con barra libre');
INSERT INTO paquete_incluye (id_paquete, item) VALUES (2, 'Cenas gourmet en restaurantes emblemáticos');
INSERT INTO paquete_incluye (id_paquete, item) VALUES (2, 'Seguro médico integral premium');

-- Paquete 3: Valle del Cocora
INSERT INTO paquete_incluye (id_paquete, item) VALUES (3, 'Transporte en Jeep Willys tradicional');
INSERT INTO paquete_incluye (id_paquete, item) VALUES (3, 'Entrada a la reserva del Valle del Cocora');
INSERT INTO paquete_incluye (id_paquete, item) VALUES (3, 'Guía local experto en flora y fauna');
INSERT INTO paquete_incluye (id_paquete, item) VALUES (3, 'Almuerzo con trucha tradicional y patacón');
INSERT INTO paquete_incluye (id_paquete, item) VALUES (3, 'Seguro de senderismo y asistencia');

-- Paquete 4: Ruta del Café y Tradición
INSERT INTO paquete_incluye (id_paquete, item) VALUES (4, 'Hospedaje en finca cafetera con piscina');
INSERT INTO paquete_incluye (id_paquete, item) VALUES (4, 'Taller experiencial de catación de café');
INSERT INTO paquete_incluye (id_paquete, item) VALUES (4, 'Cabalgata ecológica por la montaña');
INSERT INTO paquete_incluye (id_paquete, item) VALUES (4, 'Desayunos campesinos diarios incluidos');
INSERT INTO paquete_incluye (id_paquete, item) VALUES (4, 'Souvenir de café de origen');

-- Paquete 5: Parque Tayrona
INSERT INTO paquete_incluye (id_paquete, item) VALUES (5, 'Entrada oficial al Parque Nacional Tayrona');
INSERT INTO paquete_incluye (id_paquete, item) VALUES (5, 'Alojamiento en ecohabs con vista al mar');
INSERT INTO paquete_incluye (id_paquete, item) VALUES (5, 'Caminata guiada a Cabo San Juan y La Piscina');
INSERT INTO paquete_incluye (id_paquete, item) VALUES (5, 'Desayunos e hidratación durante los recorridos');
INSERT INTO paquete_incluye (id_paquete, item) VALUES (5, 'Póliza de rescate y seguro de ecoturismo');

-- Paquete 6: Ciudad Perdida y Playas
INSERT INTO paquete_incluye (id_paquete, item) VALUES (6, 'Expedición guiada por guías indígenas certificados');
INSERT INTO paquete_incluye (id_paquete, item) VALUES (6, 'Alimentación completa durante la caminata');
INSERT INTO paquete_incluye (id_paquete, item) VALUES (6, 'Campamentos con hamacas y mosquiteros');
INSERT INTO paquete_incluye (id_paquete, item) VALUES (6, 'Aporte a las comunidades indígenas locales');
INSERT INTO paquete_incluye (id_paquete, item) VALUES (6, 'Días de descanso en hotel frente al mar en Santa Marta');

-- ITINERARIO (paquete_itinerario)

-- Paquete 1: Islas del Rosario y Barú
INSERT INTO paquete_itinerario (id_paquete, actividad) VALUES (1, 'Recepción y traslado al muelle de la Bodeguita para embarque hacia las islas.');
INSERT INTO paquete_itinerario (id_paquete, actividad) VALUES (1, 'Navegación por el archipiélago, parada en oceanario y sesión guiada de snorkel.');
INSERT INTO paquete_itinerario (id_paquete, actividad) VALUES (1, 'Día de sol y descanso en Playa Blanca Barú con almuerzo caribeño.');
INSERT INTO paquete_itinerario (id_paquete, actividad) VALUES (1, 'Retorno a Cartagena, compras de artesanías en Las Bóvedas y traslado al aeropuerto.');

-- Paquete 2: Cartagena Colonial y Caribe
INSERT INTO paquete_itinerario (id_paquete, actividad) VALUES (2, 'Llegada a Cartagena, check-in en hotel boutique y coctel de bienvenida.');
INSERT INTO paquete_itinerario (id_paquete, actividad) VALUES (2, 'Recorrido histórico por el Castillo San Felipe y el Convento de La Popa.');
INSERT INTO paquete_itinerario (id_paquete, actividad) VALUES (2, 'Tour en velero privado al atardecer por la bahía de Cartagena.');
INSERT INTO paquete_itinerario (id_paquete, actividad) VALUES (2, 'Día completo de club de playa en isla privada con almuerzo gourmet.');
INSERT INTO paquete_itinerario (id_paquete, actividad) VALUES (2, 'Tarde libre para compras y cena de degustación en restaurante galardonado.');

-- Paquete 3: Valle del Cocora
INSERT INTO paquete_itinerario (id_paquete, actividad) VALUES (3, 'Encuentro en la plaza de Salento y salida en Jeep Willys hacia el valle.');
INSERT INTO paquete_itinerario (id_paquete, actividad) VALUES (3, 'Caminata entre las palmas de cera más altas del mundo y miradores fotográficos.');
INSERT INTO paquete_itinerario (id_paquete, actividad) VALUES (3, 'Visita a la Calle Real de Salento, cata de café especial y despedida.');

-- Paquete 4: Ruta del Café y Tradición
INSERT INTO paquete_itinerario (id_paquete, actividad) VALUES (4, 'Llegada y bienvenida en la hacienda cafetera tradicional.');
INSERT INTO paquete_itinerario (id_paquete, actividad) VALUES (4, 'Recorrido por los cultivos, recolección de grano y proceso del café colombiano.');
INSERT INTO paquete_itinerario (id_paquete, actividad) VALUES (4, 'Cabalgata panorámica por senderos veredales y avistamiento de aves.');
INSERT INTO paquete_itinerario (id_paquete, actividad) VALUES (4, 'Tarde de relajación en aguas termales de Santa Rosa de Cabal.');
INSERT INTO paquete_itinerario (id_paquete, actividad) VALUES (4, 'Taller de preparación de café filtrado y check-out.');

-- Paquete 5: Parque Tayrona
INSERT INTO paquete_itinerario (id_paquete, actividad) VALUES (5, 'Entrada por Zaino, caminata por bosque tropical húmedo hasta Arrecifes.');
INSERT INTO paquete_itinerario (id_paquete, actividad) VALUES (5, 'Visita a playa La Piscina y baño en las aguas tranquilas de Cabo San Juan.');
INSERT INTO paquete_itinerario (id_paquete, actividad) VALUES (5, 'Atardecer en ecohabs, descanso y retorno programado hacia Santa Marta.');

-- Paquete 6: Ciudad Perdida y Playas
INSERT INTO paquete_itinerario (id_paquete, actividad) VALUES (6, 'Salida en 4x4 hacia Machete Pelao e inicio del trekking por la Sierra Nevada.');
INSERT INTO paquete_itinerario (id_paquete, actividad) VALUES (6, 'Caminata por senderos selváticos, cruce de ríos y noche en campamento indígena.');
INSERT INTO paquete_itinerario (id_paquete, actividad) VALUES (6, 'Ascenso de los 1.200 escalones de piedra y llegada a las terrazas de Teyuna (Ciudad Perdida).');
INSERT INTO paquete_itinerario (id_paquete, actividad) VALUES (6, 'Descenso hacia la civilización y traslado a hotel de playa en El Rodadero.');
INSERT INTO paquete_itinerario (id_paquete, actividad) VALUES (6, 'Día de descanso total frente al mar Caribe y cierre de expedición.');

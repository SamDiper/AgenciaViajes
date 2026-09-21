INSERT INTO categorias_destino (nombre) VALUES ('Playa');
INSERT INTO categorias_destino (nombre) VALUES ('Montaña');

INSERT INTO destinos (nombre, pais, ciudad, descripcion, id_categoria, imagen_url, estado, fecha_registro)
VALUES ('Cartagena de Indias', 'Colombia', 'Cartagena', 'Ciudad amurallada con playas caribeñas.', 1, 'https://plus.unsplash.com/premium_photo-1697730195920-86bc1a6eeab0', 'ACTIVO', CURRENT_TIMESTAMP);

INSERT INTO destinos (nombre, pais, ciudad, descripcion, id_categoria, imagen_url, estado, fecha_registro)
VALUES ('Salento', 'Colombia', 'Salento', 'Pueblo cafetero rodeado de montañas.', 2, 'https://images.unsplash.com/photo-1697818634560-27ddca612f88', 'ACTIVO', CURRENT_TIMESTAMP);

INSERT INTO destinos (nombre, pais, ciudad, descripcion, id_categoria, imagen_url, estado, fecha_registro)
VALUES ('Tayrona','Colombia','Santa Marta','Parque conocido por sus playas, selva tropical y paisajes naturales.',1,'https://images.unsplash.com/photo-1635079552384-dd8adecd8a7c','ACTIVO',CURRENT_TIMESTAMP);

INSERT INTO roles (nombre_rol) VALUES ('ADMIN');

INSERT INTO usuarios (contrasena_hash, correo, estado, fecha_creacion, nombre_usuario, id_rol)
VALUES ('$2b$10$NbVWHG0SQUA9K3maWmWG/us3OvkpllcfN.KRpxqnpNwfQokBhIKTG', 'andres@gmail.com', 'ACTIVO', CURRENT_TIMESTAMP, 'Andres Gonzalez', 1);

INSERT INTO clientes (correo, direccion, documento, estado, fecha_registro, nombre_completo, telefono, tipo_documento, id_usuario)
VALUES ('andres@gmail.com', 'Calle 10 #20-30, Bello, Antioquia', '1000123456', 'ACTIVO', CURRENT_TIMESTAMP, 'Andres Gonzalez', '3001234567', 'CC', 1);

-- Aerolíneas
INSERT INTO aerolineas (nombre, codigo_iata) VALUES ('Avianca', 'AV');
INSERT INTO aerolineas (nombre, codigo_iata) VALUES ('LATAM Colombia', 'LA');

-- Paquetes — Cartagena (id_destino = 1)
INSERT INTO paquetes (nombre, descripcion, precio_base, duracion_dias, imagen_url, id_destino, id_aerolinea, estado, fecha_registro)
VALUES ('Cartagena Clásica', 'Recorre la ciudad amurallada, el castillo San Felipe y las playas del Laguito. Incluye hotel 3 estrellas y desayuno.', 1200000, 4, 'https://plus.unsplash.com/premium_photo-1697730195920-86bc1a6eeab0', 1, 1, 'ACTIVO', CURRENT_TIMESTAMP);

INSERT INTO paquetes (nombre, descripcion, precio_base, duracion_dias, imagen_url, id_destino, id_aerolinea, estado, fecha_registro)
VALUES ('Cartagena Premium', 'Experiencia de lujo en hoteles boutique del centro histórico. Incluye tour en velero, snorkel en Islas del Rosario y cenas gourmet.', 3500000, 7, 'https://plus.unsplash.com/premium_photo-1697730195920-86bc1a6eeab0', 1, 2, 'ACTIVO', CURRENT_TIMESTAMP);

-- Paquetes — Salento (id_destino = 2)
INSERT INTO paquetes (nombre, descripcion, precio_base, duracion_dias, imagen_url, id_destino, id_aerolinea, estado, fecha_registro)
VALUES ('Ruta del Café', 'Visita fincas cafeteras, recorre el Valle del Cocora y disfruta del paisaje cultural cafetero. Transporte y guía incluidos.', 980000, 3, 'https://images.unsplash.com/photo-1697818634560-27ddca612f88', 2, 1, 'ACTIVO', CURRENT_TIMESTAMP);

INSERT INTO paquetes (nombre, descripcion, precio_base, duracion_dias, imagen_url, id_destino, id_aerolinea, estado, fecha_registro)
VALUES ('Salento Aventura', 'Senderismo al Valle del Cocora, avistamiento de cóndores, cabalgata por la montaña y noche en posada campestre.', 1500000, 5, 'https://images.unsplash.com/photo-1697818634560-27ddca612f88', 2, 2, 'ACTIVO', CURRENT_TIMESTAMP);

-- Paquetes — Tayrona (id_destino = 3)
INSERT INTO paquetes (nombre, descripcion, precio_base, duracion_dias, imagen_url, id_destino, id_aerolinea, estado, fecha_registro)
VALUES ('Tayrona Esencial', 'Explora las playas más icónicas del Parque Tayrona: Cabo San Juan, La Piscina y Arrecifes. Incluye ingreso y hospedaje en ecohabs.', 1100000, 3, 'https://images.unsplash.com/photo-1635079552384-dd8adecd8a7c', 3, 1, 'ACTIVO', CURRENT_TIMESTAMP);

INSERT INTO paquetes (nombre, descripcion, precio_base, duracion_dias, imagen_url, id_destino, id_aerolinea, estado, fecha_registro)
VALUES ('Tayrona & Santa Marta', 'Combina el parque natural Tayrona con la ciudad de Santa Marta: centro histórico, Ciudad Perdida y playas del Rodadero.', 2800000, 8, 'https://images.unsplash.com/photo-1635079552384-dd8adecd8a7c', 3, 2, 'ACTIVO', CURRENT_TIMESTAMP);
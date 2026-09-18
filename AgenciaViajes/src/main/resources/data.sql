INSERT INTO categorias_destino (nombre) VALUES ('Playa');
INSERT INTO categorias_destino (nombre) VALUES ('Montaña');

INSERT INTO destinos (nombre, pais, ciudad, descripcion, id_categoria, imagen_url, estado, fecha_registro)
VALUES ('Cartagena de Indias', 'Colombia', 'Cartagena', 'Ciudad amurallada con playas caribeñas.', 1, 'https://images.unsplash.com/photo-1544644181-1484b3fdfc62', 'ACTIVO', CURRENT_TIMESTAMP);

INSERT INTO destinos (nombre, pais, ciudad, descripcion, id_categoria, imagen_url, estado, fecha_registro)
VALUES ('Salento', 'Colombia', 'Salento', 'Pueblo cafetero rodeado de montañas.', 2, 'https://images.unsplash.com/photo-1533105079780-92b9be482077', 'ACTIVO', CURRENT_TIMESTAMP);
INSERT INTO categorias_destino (nombre) VALUES ('Playa');
INSERT INTO categorias_destino (nombre) VALUES ('Montaña');

INSERT INTO destinos (nombre, pais, ciudad, descripcion, id_categoria, imagen_url, estado, fecha_registro)
VALUES ('Cartagena de Indias', 'Colombia', 'Cartagena', 'Ciudad amurallada con playas caribeñas.', 1, 'https://plus.unsplash.com/premium_photo-1697730195920-86bc1a6eeab0', 'ACTIVO', CURRENT_TIMESTAMP);

INSERT INTO destinos (nombre, pais, ciudad, descripcion, id_categoria, imagen_url, estado, fecha_registro)
VALUES ('Salento', 'Colombia', 'Salento', 'Pueblo cafetero rodeado de montañas.', 2, 'https://images.unsplash.com/photo-1697818634560-27ddca612f88', 'ACTIVO', CURRENT_TIMESTAMP);

INSERT INTO destinos (nombre, pais, ciudad, descripcion, id_categoria, imagen_url, estado, fecha_registro)
VALUES ('Tayrona','Colombia','Santa Marta','Parque conocido por sus playas, selva tropical y paisajes naturales.',1,'https://images.unsplash.com/photo-1635079552384-dd8adecd8a7c','ACTIVO',CURRENT_TIMESTAMP);
INSERT INTO categorias_destino (nombre) VALUES ('Playa');
INSERT INTO categorias_destino (nombre) VALUES ('Montaña');

INSERT INTO destinos (nombre, pais, ciudad, descripcion, id_categoria, imagen_url, estado, fecha_registro)
VALUES ('Cartagena de Indias', 'Colombia', 'Cartagena', 'Ciudad amurallada con playas caribeñas.', 1, 'https://images.unsplash.com/photo-1544644181-1484b3fdfc62', 'ACTIVO', CURRENT_TIMESTAMP);

INSERT INTO destinos (nombre, pais, ciudad, descripcion, id_categoria, imagen_url, estado, fecha_registro)
VALUES ('Salento', 'Colombia', 'Salento', 'Pueblo cafetero rodeado de montañas.', 2, 'https://images.unsplash.com/photo-1533105079780-92b9be482077', 'ACTIVO', CURRENT_TIMESTAMP);

INSERT INTO roles (nombre_rol) VALUES ('ADMIN');

INSERT INTO usuarios (contrasena_hash, correo, estado, fecha_creacion, nombre_usuario, id_rol)
VALUES ('$2b$10$NbVWHG0SQUA9K3maWmWG/us3OvkpllcfN.KRpxqnpNwfQokBhIKTG', 'andres@gmail.com', 'ACTIVO', CURRENT_TIMESTAMP, 'Andres Gonzalez', 1);

INSERT INTO clientes (correo, direccion, documento, estado, fecha_registro, nombre_completo, telefono, tipo_documento, id_usuario)
VALUES ('andres@gmail.com', 'Calle 10 #20-30, Bello, Antioquia', '1000123456', 'ACTIVO', CURRENT_TIMESTAMP, 'Andres Gonzalez', '3001234567', 'CC', 1);
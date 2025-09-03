-- TPO Café - Esquema + datos de ejemplo
-- Ejecutar con un usuario con privilegios (p.ej., root) o adaptar los GRANT según tu entorno.

CREATE DATABASE IF NOT EXISTS tpo_cafe CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE USER IF NOT EXISTS 'tpo'@'%' IDENTIFIED BY 'tpo1234';
GRANT ALL PRIVILEGES ON tpo_cafe.* TO 'tpo'@'%';
FLUSH PRIVILEGES;

USE tpo_cafe;

-- Roles y Usuarios
CREATE TABLE IF NOT EXISTS roles (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  nombre VARCHAR(30) UNIQUE NOT NULL
);

CREATE TABLE IF NOT EXISTS usuarios (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  usuario VARCHAR(50) UNIQUE NOT NULL,
  correo VARCHAR(120) UNIQUE NOT NULL,
  contrasenia VARCHAR(255) NOT NULL,
  nombre VARCHAR(50) NOT NULL,
  apellido VARCHAR(50) NOT NULL,
  habilitado BOOLEAN NOT NULL DEFAULT TRUE,
  creado_en TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS usuarios_roles (
  usuario_id BIGINT NOT NULL,
  role_id BIGINT NOT NULL,
  PRIMARY KEY (usuario_id, role_id),
  CONSTRAINT fk_ur_user FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE,
  CONSTRAINT fk_ur_role FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE
);

-- Categorías / Productos
CREATE TABLE IF NOT EXISTS categorias (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  nombre VARCHAR(60) NOT NULL,
  parent_id BIGINT NULL,
  UNIQUE KEY uq_cat_name_parent (nombre, parent_id),
  CONSTRAINT fk_cat_parent FOREIGN KEY (parent_id) REFERENCES categorias(id) ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS productos (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  vendedor_id BIGINT NOT NULL,
  categoria_id BIGINT NOT NULL,
  nombre VARCHAR(120) NOT NULL,
  description TEXT NULL,
  precio DECIMAL(10,2) NOT NULL,
  stock INT NOT NULL,
  activo BOOLEAN NOT NULL DEFAULT TRUE,
  creado_en TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_prod_seller FOREIGN KEY (vendedor_id) REFERENCES usuarios(id),
  CONSTRAINT fk_prod_cat FOREIGN KEY (categoria_id) REFERENCES categorias(id),
  CONSTRAINT chk_price CHECK (precio >= 0),
  CONSTRAINT chk_stock CHECK (stock >= 0),
  UNIQUE KEY uq_seller_name (vendedor_id, nombre)
);

CREATE TABLE IF NOT EXISTS imagenes_producto (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  producto_id BIGINT NOT NULL,
  url VARCHAR(500) NOT NULL,
  es_principal BOOLEAN NOT NULL DEFAULT FALSE,
  CONSTRAINT fk_img_prod FOREIGN KEY (producto_id) REFERENCES productos(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS descuentos (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  producto_id BIGINT UNIQUE NOT NULL,
  porcentaje DECIMAL(5,2) NOT NULL,
  inicia_en DATETIME NOT NULL,
  termina_en DATETIME NOT NULL,
  CONSTRAINT fk_disc_prod FOREIGN KEY (producto_id) REFERENCES productos(id) ON DELETE CASCADE,
  CONSTRAINT chk_disc_pct CHECK (porcentaje >= 0 AND porcentaje <= 100)
);

-- Carrito / Ordenes
CREATE TABLE IF NOT EXISTS carts (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  usuario_id BIGINT NOT NULL,
  status ENUM('OPEN','CHECKED_OUT') NOT NULL DEFAULT 'OPEN',
  creado_en TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_cart_user FOREIGN KEY (usuario_id) REFERENCES usuarios(id)
);

CREATE TABLE IF NOT EXISTS cart_items (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  cart_id BIGINT NOT NULL,
  producto_id BIGINT NOT NULL,
  cantidad INT NOT NULL,
  precio_unitario DECIMAL(10,2) NOT NULL,
  porcentaje_descuento DECIMAL(5,2) NOT NULL DEFAULT 0,
  CONSTRAINT fk_ci_cart FOREIGN KEY (cart_id) REFERENCES carts(id) ON DELETE CASCADE,
  CONSTRAINT fk_ci_prod FOREIGN KEY (producto_id) REFERENCES productos(id),
  CONSTRAINT chk_qty CHECK (cantidad > 0),
  UNIQUE KEY uq_cart_product (cart_id, producto_id)
);

CREATE TABLE IF NOT EXISTS pedidos (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  usuario_id BIGINT NOT NULL,
  total DECIMAL(10,2) NOT NULL,
  creado_en TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_order_user FOREIGN KEY (usuario_id) REFERENCES usuarios(id)
);

CREATE TABLE IF NOT EXISTS items_pedido (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  order_id BIGINT NOT NULL,
  producto_id BIGINT NOT NULL,
  cantidad INT NOT NULL,
  precio_unitario DECIMAL(10,2) NOT NULL,
  porcentaje_descuento DECIMAL(5,2) NOT NULL DEFAULT 0,
  CONSTRAINT fk_oi_order FOREIGN KEY (order_id) REFERENCES pedidos(id) ON DELETE CASCADE,
  CONSTRAINT fk_oi_prod FOREIGN KEY (producto_id) REFERENCES productos(id)
);

-- Seeds
INSERT IGNORE INTO roles (nombre) VALUES ('BUYER'), ('SELLER'), ('ADMIN');

-- usuarios de demo (pass: 123456)
INSERT INTO usuarios (usuario, correo, contrasenia, nombre, apellido)
VALUES 
  ('seller1', 'seller1@example.com', '$2b$12$TUK6z03etIhOQJs3xiAa4efY1cWeUR7KdBXLJ1ykTk6yN3SIfG/Ky', 'Vendedor', 'Demo'),
  ('buyer1', 'buyer1@example.com', '$2b$12$TUK6z03etIhOQJs3xiAa4efY1cWeUR7KdBXLJ1ykTk6yN3SIfG/Ky', 'Comprador', 'Demo')
ON DUPLICATE KEY UPDATE correo = VALUES(correo);

SET @vendedor_id := (SELECT id FROM usuarios WHERE usuario='seller1');
SET @buyer_id := (SELECT id FROM usuarios WHERE usuario='buyer1');
SET @role_seller := (SELECT id FROM roles WHERE nombre='SELLER');
SET @role_buyer := (SELECT id FROM roles WHERE nombre='BUYER');

INSERT IGNORE INTO usuarios_roles (usuario_id, role_id) VALUES
  (@vendedor_id, @role_seller),
  (@buyer_id, @role_buyer);

-- Categorías
INSERT IGNORE INTO categorias (nombre, parent_id) VALUES ('Cafés', NULL);
SET @cafes_id := (SELECT id FROM categorias WHERE nombre='Cafés');
INSERT IGNORE INTO categorias (nombre, parent_id)
VALUES 
 ('Grano', @cafes_id),
 ('Molido', @cafes_id),
 ('Cápsulas', @cafes_id),
 ('Instantáneo', @cafes_id),
 ('Especialidad', @cafes_id),
 ('Descafeinado', @cafes_id);

INSERT IGNORE INTO categorias (nombre, parent_id) VALUES ('Accesorios', NULL);
SET @acc_id := (SELECT id FROM categorias WHERE nombre='Accesorios');
INSERT IGNORE INTO categorias (nombre, parent_id)
VALUES ('Molinillos', @acc_id), ('Prensas', @acc_id), ('Filtros', @acc_id);

-- Productos de ejemplo (con vendedor demo)
INSERT INTO productos (vendedor_id, categoria_id, nombre, description, precio, stock, activo)
VALUES
  (@vendedor_id, (SELECT id FROM categorias WHERE nombre='Grano' AND parent_id=@cafes_id), 'Café Etiopía Yirgacheffe 250g', 'Notas florales y cítricas. Tueste medio.', 6500.00, 30, TRUE),
  (@vendedor_id, (SELECT id FROM categorias WHERE nombre='Molido' AND parent_id=@cafes_id), 'Café Colombia Supremo 500g (Molido)', 'Balanceado, chocolate y frutos secos.', 9200.00, 15, TRUE),
  (@vendedor_id, (SELECT id FROM categorias WHERE nombre='Cápsulas' AND parent_id=@cafes_id), 'Cápsulas Espresso Intenso x10', 'Compatible con Nespresso®. Intensidad 10.', 4200.00, 0, TRUE), -- sin stock a propósito
  (@vendedor_id, (SELECT id FROM categorias WHERE nombre='Instantáneo' AND parent_id=@cafes_id), 'Café Instantáneo Gold 200g', 'Disolución rápida, sabor suave.', 3800.00, 25, TRUE);

SET @p1 := (SELECT id FROM productos WHERE nombre='Café Etiopía Yirgacheffe 250g');
SET @p2 := (SELECT id FROM productos WHERE nombre='Café Colombia Supremo 500g (Molido)');
SET @p3 := (SELECT id FROM productos WHERE nombre='Cápsulas Espresso Intenso x10');
SET @p4 := (SELECT id FROM productos WHERE nombre='Café Instantáneo Gold 200g');

INSERT IGNORE INTO imagenes_producto (producto_id, url, es_principal) VALUES
  (@p1, 'https://picsum.photos/seed/etiopia/600/400', TRUE),
  (@p2, 'https://picsum.photos/seed/colombia/600/400', TRUE),
  (@p3, 'https://picsum.photos/seed/capsulas/600/400', TRUE),
  (@p4, 'https://picsum.photos/seed/instantaneo/600/400', TRUE);

-- descuento de ejemplo: 15% sobre p1 durante este mes
INSERT INTO descuentos (producto_id, porcentaje, inicia_en, termina_en)
VALUES (@p1, 15.00, NOW(), DATE_ADD(NOW(), INTERVAL 30 DAY))
ON DUPLICATE KEY UPDATE porcentaje=VALUES(porcentaje), inicia_en=VALUES(inicia_en), termina_en=VALUES(termina_en);

-- Vistas/Consultas útiles
-- 1) Productos visibles al cliente (sin stock = ocultos por defecto)
--    NOTA: el backend debería filtrar por stock>0 en /productos (comportamiento por defecto).
--    Consulta equivalente:
--    SELECT * FROM productos WHERE activo=TRUE AND stock>0;

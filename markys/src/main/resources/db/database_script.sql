CREATE DATABASE IF NOT EXISTS markys3;
USE markys3;

-- Tabla: roles
CREATE TABLE IF NOT EXISTS roles (
    id BIGINT(20) NOT NULL AUTO_INCREMENT,
    nombre VARCHAR(255),
    PRIMARY KEY (id)
);

INSERT INTO roles (id, nombre) VALUES
(2, 'ADMIN'),
(1, 'CLIENTE');

-- Tabla: usuarios
CREATE TABLE IF NOT EXISTS usuarios (
    id BIGINT(20) NOT NULL AUTO_INCREMENT,
    nombre VARCHAR(255),
    apellido VARCHAR(255),
    correo VARCHAR(255),
    username VARCHAR(255),
    password VARCHAR(255) NOT NULL,
    PRIMARY KEY (id)
);

INSERT INTO usuarios (id, nombre, apellido, correo, username, password) VALUES
(1, 'Luis', 'Heredia', 'luis@example.com', 'luisito', '$2a$10$TqJxKt7nnrTriKlZe/HX9eibHeXA/nzVbiD4O4yuSKKw.PJvJinjC'),
(2, 'Juan', 'Alvarado', 'juan@example.com', 'juan123', '$2a$10$DqbaSZJuNF4wZBVSc00Vlu4CKYQJrKb7t1Aw1V.AXOM3EcP2iVOcq'),
(3, 'Lyan', 'Correa', 'lyan@example.com', 'lyan123', '$2a$10$ZH/6PfJpe0FV1VAdFoGfsOjm/S429RS9XTuNLUIFIucU9D4ANJ80C'),
(4, 'Pedro', 'Rios', 'pedro@example.com', 'pedro123', '$2a$10$lWeVEzwy3ytwh0.6XXG1W.iDFOhNuumxNqBGElIxT0m6GNi/4Ns5G');

-- Tabla: usuarios_roles
CREATE TABLE IF NOT EXISTS usuarios_roles (
    usuario_id BIGINT(20) NOT NULL,
    rol_id BIGINT(20) NOT NULL,
    PRIMARY KEY (usuario_id, rol_id),
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id),
    FOREIGN KEY (rol_id) REFERENCES roles(id)
);

INSERT INTO usuarios_roles (usuario_id, rol_id) VALUES
(1, 1),
(2, 1),
(3, 2),
(4, 1);

-- Tabla: platillos
CREATE TABLE IF NOT EXISTS platillos (
    id BIGINT(20) NOT NULL AUTO_INCREMENT,
    nombre VARCHAR(255) NOT NULL,
    descripcion VARCHAR(255) NOT NULL,
    precio DECIMAL(38,2) NOT NULL,
    estado ENUM('DISPONIBLE', 'AGOTADO') DEFAULT NULL,
    imagen VARCHAR(255) NOT NULL,
    PRIMARY KEY (id)
);

INSERT INTO platillos (id, nombre, descripcion, precio, estado, imagen) VALUES
(1, 'Pollo a la brasa', 'Pollo jugoso y crocante con papas fritas', 25.00, 'DISPONIBLE', '1pollo.jpg'),
(2, 'Ensalada César', 'Ensalada fresca con pollo, lechuga, crutones y aderezo César', 15.00, 'DISPONIBLE', '1pollo.jpg'),
(3, 'Papas Fritas', 'Papas fritas crujientes, perfectas para acompañar tus platillos', 12.00, 'AGOTADO', 'papasfritas.jpg'),
(4, 'Tallarines a la Huancaína', 'Tallarines con salsa de huancaína', 28.00, 'DISPONIBLE', '1pollo.jpg');


-- Tabla PEDIDOS
CREATE TABLE pedidos (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    fecha DATETIME NOT NULL,
    total DECIMAL(10, 2) NOT NULL,
    estado VARCHAR(50) NOT NULL
);

-- Tabla DETALLE_PEDIDO
CREATE TABLE detalle_pedido (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    pedido_id BIGINT NOT NULL,
    platillo_id BIGINT NOT NULL,
    cantidad INT NOT NULL,
    precio_unitario DECIMAL(10, 2) NOT NULL,

    FOREIGN KEY (pedido_id) REFERENCES pedidos(id) ON DELETE CASCADE,
    FOREIGN KEY (platillo_id) REFERENCES platillos(id) ON DELETE CASCADE
);

--Tabla alterada PEDIDOS
ALTER TABLE pedidos
ADD metodo_entrega VARCHAR(20) NOT NULL,
ADD direccion_entrega VARCHAR(255);
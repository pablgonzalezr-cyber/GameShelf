CREATE TABLE IF NOT EXISTS videojuegos (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    titulo VARCHAR(100) NOT NULL,
    descripcion VARCHAR(250) NOT NULL,
    precio DOUBLE NOT NULL,
    categoria_id BIGINT NOT NULL,
    nombre_categoria VARCHAR(80) NOT NULL,
    plataforma VARCHAR(40) NOT NULL,
    estado VARCHAR(30) NOT NULL
);
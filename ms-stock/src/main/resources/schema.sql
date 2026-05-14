CREATE TABLE IF NOT EXISTS stocks (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    videojuego_id BIGINT NOT NULL UNIQUE,
    cantidad_total INT NOT NULL,
    cantidad_disponible INT NOT NULL,
    estado VARCHAR(20) NOT NULL
);
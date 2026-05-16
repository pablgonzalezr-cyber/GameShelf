CREATE DATABASE IF NOT EXISTS gameshelf_reserva;
USE gameshelf_reserva;

CREATE TABLE IF NOT EXISTS reservas (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    usuario_id BIGINT NOT NULL,
    videojuego_id BIGINT NOT NULL,
    fecha_reserva DATE NOT NULL,
    fecha_vencimiento DATE NOT NULL,
    estado VARCHAR(30) NOT NULL
);
CREATE DATABASE IF NOT EXISTS db_reserva;
USE db_reserva;

CREATE TABLE IF NOT EXISTS reservas (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    usuario_id BIGINT NOT NULL,
    videojuego_id BIGINT NOT NULL,
    fecha_reserva DATE NOT NULL,
    estado VARCHAR(30) NOT NULL
);

CREATE TABLE IF NOT EXISTS historial_reservas (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    reserva_id BIGINT NOT NULL,
    estado_anterior VARCHAR(30),
    estado_nuevo VARCHAR(30) NOT NULL,
    fecha_cambio DATE NOT NULL,
    motivo VARCHAR(255) NOT NULL,
    CONSTRAINT fk_historial_reserva
        FOREIGN KEY (reserva_id)
        REFERENCES reservas(id)
        ON DELETE CASCADE
);
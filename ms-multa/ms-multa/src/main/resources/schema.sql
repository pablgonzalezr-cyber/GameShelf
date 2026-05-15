CREATE TABLE IF NOT EXISTS multas (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    usuario_id BIGINT NOT NULL,
    prestamo_id BIGINT NOT NULL,
    monto DOUBLE NOT NULL,
    motivo VARCHAR(150) NOT NULL,
    fecha_multa DATE NOT NULL,
    estado VARCHAR(20) NOT NULL
);
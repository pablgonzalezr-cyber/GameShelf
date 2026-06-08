CREATE TABLE IF NOT EXISTS prestamos (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    usuario_id BIGINT NOT NULL,
    videojuego_id BIGINT NOT NULL,
    fecha_prestamo DATE NOT NULL,
    fecha_devolucion DATE,
    estado VARCHAR(20) NOT NULL
);

CREATE TABLE IF NOT EXISTS renovaciones_prestamo (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    prestamo_id BIGINT NOT NULL,
    fecha_anterior_devolucion DATE NOT NULL,
    nueva_fecha_devolucion DATE NOT NULL,
    motivo VARCHAR(255) NOT NULL,
    fecha_renovacion DATE NOT NULL,
    CONSTRAINT fk_renovacion_prestamo
        FOREIGN KEY (prestamo_id)
        REFERENCES prestamos(id)
        ON DELETE CASCADE
);
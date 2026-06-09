CREATE TABLE IF NOT EXISTS multas (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    usuario_id BIGINT NOT NULL,
    prestamo_id BIGINT NOT NULL,
    monto DOUBLE NOT NULL,
    motivo VARCHAR(150) NOT NULL,
    fecha_multa DATE NOT NULL,
    estado VARCHAR(20) NOT NULL
);

CREATE TABLE IF NOT EXISTS pagos_multa (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    multa_id BIGINT NOT NULL,
    monto_pagado DOUBLE NOT NULL,
    fecha_pago DATE NOT NULL,
    metodo_pago VARCHAR(30) NOT NULL,
    estado_pago VARCHAR(20) NOT NULL,
    CONSTRAINT fk_pago_multa
        FOREIGN KEY (multa_id)
        REFERENCES multas(id)
        ON DELETE CASCADE
);
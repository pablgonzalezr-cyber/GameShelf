CREATE TABLE autorizaciones (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    usuario_id BIGINT NOT NULL,
    rol VARCHAR(50) NOT NULL,
    modulo VARCHAR(50) NOT NULL,
    permiso VARCHAR(50) NOT NULL,
    estado VARCHAR(20) NOT NULL
);
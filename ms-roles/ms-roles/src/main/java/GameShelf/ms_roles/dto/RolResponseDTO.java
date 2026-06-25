package GameShelf.ms_roles.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "DTO de respuesta con los datos de un rol")
public class RolResponseDTO {

    @Schema(description = "ID único del rol", example = "1")
    private Long id;

    @Schema(description = "Nombre del rol registrado en el sistema", example = "CLIENTE")
    private String nombre;

    @Schema(description = "Descripción funcional del rol", example = "Usuario cliente que puede consultar videojuegos, generar reservas y solicitar préstamos")
    private String descripcion;

    @Schema(description = "Estado actual del rol", example = "ACTIVO", allowableValues = {"ACTIVO", "INACTIVO"})
    private String estado;
}
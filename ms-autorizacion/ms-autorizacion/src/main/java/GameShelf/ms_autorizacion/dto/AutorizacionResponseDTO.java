package GameShelf.ms_autorizacion.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "DTO de respuesta con los datos de una autorización")
public class AutorizacionResponseDTO {

    @Schema(description = "ID de la autorización", example = "1")
    private Long id;

    @Schema(description = "ID del usuario autorizado", example = "1")
    private Long usuarioId;

    @Schema(description = "Rol asociado al usuario", example = "ADMINISTRADOR")
    private String rol;

    @Schema(description = "Módulo autorizado", example = "PRESTAMOS")
    private String modulo;

    @Schema(description = "Permiso asignado", example = "GESTIONAR_PRESTAMOS")
    private String permiso;

    @Schema(description = "Estado de la autorización", example = "ACTIVO")
    private String estado;
}
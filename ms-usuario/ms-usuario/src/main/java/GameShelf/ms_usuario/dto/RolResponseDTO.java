package GameShelf.ms_usuario.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "DTO utilizado para recibir información de roles desde ms-roles")
public class RolResponseDTO {

    @Schema(description = "ID del rol", example = "1")
    private Long id;

    @Schema(description = "Nombre del rol", example = "CLIENTE")
    private String nombre;

    @Schema(description = "Descripción del rol", example = "Usuario cliente del sistema")
    private String descripcion;

    @Schema(description = "Estado del rol", example = "ACTIVO")
    private String estado;
}
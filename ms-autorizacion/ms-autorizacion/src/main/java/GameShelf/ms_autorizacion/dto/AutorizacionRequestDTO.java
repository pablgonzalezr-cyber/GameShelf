package GameShelf.ms_autorizacion.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "DTO utilizado para crear o actualizar una autorización")
public class AutorizacionRequestDTO {

    @NotNull(message = "El usuario es obligatorio")
    @Schema(description = "ID del usuario al que se le asignará la autorización", example = "1")
    private Long usuarioId;

    @NotBlank(message = "El rol es obligatorio")
    @Schema(description = "Rol asociado a la autorización", example = "ADMINISTRADOR")
    private String rol;

    @NotBlank(message = "El módulo es obligatorio")
    @Schema(description = "Módulo sobre el cual se aplica la autorización", example = "PRESTAMOS")
    private String modulo;

    @NotBlank(message = "El permiso es obligatorio")
    @Schema(description = "Permiso que tendrá el usuario sobre el módulo", example = "GESTIONAR_PRESTAMOS")
    private String permiso;

    @NotBlank(message = "El estado es obligatorio")
    @Schema(description = "Estado de la autorización", example = "ACTIVO")
    private String estado;
}
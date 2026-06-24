package GameShelf.ms_autorizacion.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "DTO utilizado para validar si un usuario tiene un permiso sobre un módulo")
public class ValidarAutorizacionRequestDTO {

    @NotNull(message = "El usuario es obligatorio")
    @Schema(description = "ID del usuario que se desea validar", example = "1")
    private Long usuarioId;

    @NotBlank(message = "El módulo es obligatorio")
    @Schema(description = "Módulo que se desea validar", example = "PRESTAMOS")
    private String modulo;

    @NotBlank(message = "El permiso es obligatorio")
    @Schema(description = "Permiso que se desea validar", example = "GESTIONAR_PRESTAMOS")
    private String permiso;
}
package GameShelf.ms_autorizacion.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "DTO utilizado para validar si un usuario tiene un permiso sobre un módulo")
public class ValidarAutorizacionRequestDTO {

    @NotNull(message = "El usuario es obligatorio")
    @Schema(
            description = "ID del usuario que se desea validar",
            example = "1",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private Long usuarioId;

    @NotBlank(message = "El módulo es obligatorio")
    @Schema(
            description = "Módulo que se desea validar",
            example = "PRESTAMOS",
            allowableValues = {"CATALOGO", "PRESTAMOS", "RESERVAS", "MULTAS", "SISTEMA"},
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String modulo;

    @NotBlank(message = "El permiso es obligatorio")
    @Schema(
            description = "Permiso que se desea validar sobre el módulo indicado",
            example = "GESTIONAR_PRESTAMOS",
            allowableValues = {
                    "TOTAL",
                    "ADMIN",
                    "VER_CATALOGO",
                    "GESTIONAR_MULTAS",
                    "GESTIONAR_RESERVAS",
                    "GESTIONAR_PRESTAMOS",
                    "TESTEADOR"
            },
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String permiso;
}
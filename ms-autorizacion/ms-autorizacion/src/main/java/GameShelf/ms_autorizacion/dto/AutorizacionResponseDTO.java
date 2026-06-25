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

    @Schema(description = "ID único de la autorización", example = "1")
    private Long id;

    @Schema(description = "ID del usuario autorizado", example = "1")
    private Long usuarioId;

    @Schema(description = "Rol asociado al usuario", example = "ADMINISTRADOR")
    private String rol;

    @Schema(
            description = "Módulo autorizado",
            example = "PRESTAMOS",
            allowableValues = {"CATALOGO", "PRESTAMOS", "RESERVAS", "MULTAS", "SISTEMA"}
    )
    private String modulo;

    @Schema(
            description = "Permiso asignado sobre el módulo",
            example = "GESTIONAR_PRESTAMOS",
            allowableValues = {
                    "TOTAL",
                    "ADMIN",
                    "VER_CATALOGO",
                    "GESTIONAR_MULTAS",
                    "GESTIONAR_RESERVAS",
                    "GESTIONAR_PRESTAMOS",
                    "TESTEADOR"
            }
    )
    private String permiso;

    @Schema(
            description = "Estado actual de la autorización",
            example = "ACTIVO",
            allowableValues = {"ACTIVO", "INACTIVO"}
    )
    private String estado;
}
package GameShelf.ms_autorizacion.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "DTO utilizado para recibir datos del usuario desde ms-usuario")
public class UsuarioResponseDTO {

    @Schema(description = "ID único del usuario", example = "1")
    private Long id;

    @Schema(description = "Nombre de usuario recibido desde ms-usuario", example = "pablo")
    private String usuario;

    @Schema(description = "Correo electrónico del usuario recibido desde ms-usuario", example = "pablo@gmail.com")
    private String correo;

    @Schema(description = "Rol del usuario recibido desde ms-usuario", example = "ADMINISTRADOR")
    private String rol;

    @Schema(
            description = "Estado actual del usuario recibido desde ms-usuario",
            example = "ACTIVO",
            allowableValues = {"ACTIVO", "INACTIVO"}
    )
    private String estado;
}
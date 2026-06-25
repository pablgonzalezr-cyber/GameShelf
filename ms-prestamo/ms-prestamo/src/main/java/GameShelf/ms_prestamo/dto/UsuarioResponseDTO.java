package GameShelf.ms_prestamo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "DTO interno usado por Feign para recibir información desde ms-usuario")
public class UsuarioResponseDTO {

    @Schema(description = "ID único del usuario", example = "1")
    private Long id;

    @Schema(description = "Nombre de usuario recibido desde ms-usuario", example = "pablo")
    private String usuario;

    @Schema(description = "Correo electrónico del usuario", example = "pablo@gmail.com")
    private String correo;

    @Schema(description = "Rol asociado al usuario", example = "CLIENTE")
    private String rol;

    @Schema(
            description = "Estado actual del usuario",
            example = "ACTIVO",
            allowableValues = {"ACTIVO", "INACTIVO"}
    )
    private String estado;
}
package GameShelf.ms_notificacion.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO utilizado para recibir datos del usuario desde ms-usuario")
public class UsuarioResponseDTO {

    @Schema(description = "ID del usuario", example = "1")
    private Long id;

    @Schema(description = "Nombre de usuario", example = "pablo")
    private String usuario;

    @Schema(description = "Correo electrónico del usuario", example = "pablo@gmail.com")
    private String correo;

    @Schema(description = "Rol asociado al usuario", example = "CLIENTE")
    private String rol;

    @Schema(description = "Estado del usuario", example = "ACTIVO")
    private String estado;
}
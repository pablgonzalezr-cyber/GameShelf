package GameShelf.ms_usuario.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "DTO utilizado para actualizar los datos de un usuario existente")
public class UsuarioUpdateDTO {

    @NotBlank(message = "El nombre de usuario es obligatorio")
    @Size(min = 3, max = 50, message = "El usuario debe tener entre 3 y 50 caracteres")
    @Schema(description = "Nuevo nombre de usuario", example = "pablo_actualizado")
    private String usuario;

    @Size(min = 4, max = 100, message = "La contraseña debe tener entre 4 y 100 caracteres")
    @Schema(description = "Nueva contraseña del usuario. Si se envía vacía o nula, se mantiene la contraseña actual", example = "12345", accessMode = Schema.AccessMode.WRITE_ONLY)
    private String contrasena;

    @NotBlank(message = "El correo es obligatorio")
    @Email(message = "El correo debe tener un formato válido")
    @Schema(description = "Nuevo correo electrónico del usuario", example = "pablo.actualizado@gmail.com")
    private String correo;

    @Schema(description = "Nuevo rol asignado al usuario", example = "ADMINISTRADOR")
    private String rol;
}

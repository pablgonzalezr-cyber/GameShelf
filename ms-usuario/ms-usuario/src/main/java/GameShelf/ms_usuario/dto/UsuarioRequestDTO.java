package GameShelf.ms_usuario.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "DTO utilizado para crear un nuevo usuario")
public class UsuarioRequestDTO {

    @NotBlank(message = "El nombre de usuario es obligatorio")
    @Size(min = 3, max = 50, message = "El usuario debe tener entre 3 y 50 caracteres")
    @Schema(description = "Nombre de usuario único dentro del sistema", example = "pablo")
    private String usuario;

    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 4, max = 100, message = "La contraseña debe tener entre 4 y 100 caracteres")
    @Schema(description = "Contraseña del usuario. Se almacena cifrada en la base de datos", example = "1234", accessMode = Schema.AccessMode.WRITE_ONLY)
    private String contrasena;

    @NotBlank(message = "El correo es obligatorio")
    @Email(message = "El correo debe tener un formato válido")
    @Schema(description = "Correo electrónico único del usuario", example = "pablo@gmail.com")
    private String correo;

    @Schema(description = "Rol asignado al usuario. Si no se envía, el sistema asigna CLIENTE por defecto", example = "CLIENTE")
    private String rol;
}
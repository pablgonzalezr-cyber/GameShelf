package GameShelf.ms_roles.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "DTO utilizado para crear o actualizar un rol")
public class RolRequestDTO {

    @NotBlank(message = "El nombre del rol es obligatorio")
    @Size(min = 3, max = 30, message = "El nombre del rol debe tener entre 3 y 30 caracteres")
    @Schema(description = "Nombre del rol. El sistema lo guarda en mayúsculas", example = "CLIENTE")
    private String nombre;

    @NotBlank(message = "La descripción del rol es obligatoria")
    @Size(min = 5, max = 150, message = "La descripción debe tener entre 5 y 150 caracteres")
    @Schema(description = "Descripción del rol dentro del sistema", example = "Usuario cliente que puede reservar y solicitar préstamos")
    private String descripcion;

    @Schema(description = "Estado del rol. Si no se envía, el sistema asigna ACTIVO por defecto", example = "ACTIVO")
    private String estado;
}


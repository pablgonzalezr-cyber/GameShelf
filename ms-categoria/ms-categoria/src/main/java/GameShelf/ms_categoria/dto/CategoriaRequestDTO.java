package GameShelf.ms_categoria.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "DTO utilizado para crear o actualizar una categoría")
public class CategoriaRequestDTO {

    @NotBlank(message = "El nombre de la categoría es obligatorio")
    @Size(min = 3, max = 80, message = "El nombre debe tener entre 3 y 80 caracteres")
    @Schema(description = "Nombre de la categoría. El sistema lo guarda en mayúsculas", example = "AVENTURA")
    private String nombre;

    @NotBlank(message = "La descripción es obligatoria")
    @Size(min = 5, max = 200, message = "La descripción debe tener entre 5 y 200 caracteres")
    @Schema(description = "Descripción de la categoría", example = "Videojuegos de aventura, exploración y misiones")
    private String descripcion;

    @Schema(description = "Estado de la categoría. Si no se envía, el sistema asigna ACTIVO por defecto", example = "ACTIVO")
    private String estado;
}

package GameShelf.ms_videojuego.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "DTO utilizado para crear o actualizar un videojuego")
public class VideoJuegoRequestDTO {

    @NotBlank(message = "El título del videojuego es obligatorio")
    @Size(min = 2, max = 100, message = "El título debe tener entre 2 y 100 caracteres")
    @Schema(
            description = "Título del videojuego. El sistema valida que no exista otro videojuego con el mismo título y plataforma",
            example = "The Legend of Zelda",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String titulo;

    @NotBlank(message = "La descripción es obligatoria")
    @Size(min = 5, max = 250, message = "La descripción debe tener entre 5 y 250 caracteres")
    @Schema(
            description = "Descripción breve del videojuego dentro del catálogo",
            example = "Videojuego de aventura, exploración y mundo abierto",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String descripcion;

    @NotNull(message = "El precio es obligatorio")
    @Positive(message = "El precio debe ser mayor a 0")
    @Schema(
            description = "Precio del videojuego en pesos chilenos",
            example = "49990",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private Double precio;

    @NotNull(message = "La categoría es obligatoria")
    @Schema(
            description = "ID de la categoría asociada al videojuego. Se valida remotamente mediante ms-categoria",
            example = "1",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private Long categoriaId;

    @NotBlank(message = "La plataforma es obligatoria")
    @Size(min = 2, max = 40, message = "La plataforma debe tener entre 2 y 40 caracteres")
    @Schema(
            description = "Plataforma del videojuego. El sistema la normaliza a mayúsculas",
            example = "PC",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String plataforma;

    @Schema(
            description = "Estado del videojuego. Si no se envía, el sistema asigna DISPONIBLE por defecto",
            example = "DISPONIBLE",
            allowableValues = {"DISPONIBLE", "NO_DISPONIBLE", "INACTIVO"},
            requiredMode = Schema.RequiredMode.NOT_REQUIRED
    )
    private String estado;
}
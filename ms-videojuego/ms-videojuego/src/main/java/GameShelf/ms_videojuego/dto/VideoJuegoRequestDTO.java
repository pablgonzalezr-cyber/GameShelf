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
    @Schema(description = "Título del videojuego", example = "The Legend of Zelda")
    private String titulo;

    @NotBlank(message = "La descripción es obligatoria")
    @Size(min = 5, max = 250, message = "La descripción debe tener entre 5 y 250 caracteres")
    @Schema(description = "Descripción breve del videojuego", example = "Videojuego de aventura y exploración")
    private String descripcion;

    @NotNull(message = "El precio es obligatorio")
    @Positive(message = "El precio debe ser mayor a 0")
    @Schema(description = "Precio del videojuego", example = "49990")
    private Double precio;

    @NotNull(message = "La categoría es obligatoria")
    @Schema(description = "ID de la categoría asociada al videojuego", example = "1")
    private Long categoriaId;

    @NotBlank(message = "La plataforma es obligatoria")
    @Size(min = 2, max = 40, message = "La plataforma debe tener entre 2 y 40 caracteres")
    @Schema(description = "Plataforma del videojuego", example = "PC")
    private String plataforma;

    @Schema(description = "Estado del videojuego. Si no se envía, el sistema asigna DISPONIBLE por defecto", example = "DISPONIBLE")
    private String estado;
}


package GameShelf.ms_videojuego.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "DTO de respuesta con los datos de un videojuego")
public class VideoJuegoResponseDTO {

    @Schema(description = "ID del videojuego", example = "1")
    private Long id;

    @Schema(description = "Título del videojuego", example = "The Legend of Zelda")
    private String titulo;

    @Schema(description = "Descripción del videojuego", example = "Videojuego de aventura y exploración")
    private String descripcion;

    @Schema(description = "Precio del videojuego", example = "49990")
    private Double precio;

    @Schema(description = "ID de la categoría asociada", example = "1")
    private Long categoriaId;

    @Schema(description = "Nombre de la categoría asociada", example = "AVENTURA")
    private String nombreCategoria;

    @Schema(description = "Plataforma del videojuego", example = "PC")
    private String plataforma;

    @Schema(description = "Estado del videojuego", example = "DISPONIBLE")
    private String estado;
}

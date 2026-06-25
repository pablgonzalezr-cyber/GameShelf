package GameShelf.ms_prestamo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "DTO interno usado por Feign para recibir información desde ms-videojuego")
public class VideoJuegoResponseDTO {

    @Schema(description = "ID único del videojuego", example = "2")
    private Long id;

    @Schema(description = "Título del videojuego", example = "The Legend of Zelda")
    private String titulo;

    @Schema(description = "Descripción del videojuego", example = "Videojuego de aventura y exploración")
    private String descripcion;

    @Schema(description = "Precio referencial del videojuego", example = "49990")
    private Double precio;

    @Schema(description = "ID de la categoría asociada al videojuego", example = "1")
    private Long categoriaId;

    @Schema(description = "Nombre de la categoría asociada", example = "AVENTURA")
    private String nombreCategoria;

    @Schema(description = "Plataforma del videojuego", example = "PC")
    private String plataforma;

    @Schema(
            description = "Estado actual del videojuego. Para crear préstamos debe estar DISPONIBLE",
            example = "DISPONIBLE",
            allowableValues = {"DISPONIBLE", "NO_DISPONIBLE", "INACTIVO"}
    )
    private String estado;
}
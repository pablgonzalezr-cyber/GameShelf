package GameShelf.ms_videojuego.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "DTO utilizado para recibir información de categorías desde ms-categoria")
public class CategoriaResponseDTO {

    @Schema(description = "ID único de la categoría", example = "1")
    private Long id;

    @Schema(description = "Nombre de la categoría recibida desde ms-categoria", example = "AVENTURA")
    private String nombre;

    @Schema(description = "Descripción de la categoría recibida desde ms-categoria", example = "Videojuegos de aventura y exploración")
    private String descripcion;

    @Schema(
            description = "Estado de la categoría recibida desde ms-categoria",
            example = "ACTIVO",
            allowableValues = {"ACTIVO", "INACTIVO"}
    )
    private String estado;
}
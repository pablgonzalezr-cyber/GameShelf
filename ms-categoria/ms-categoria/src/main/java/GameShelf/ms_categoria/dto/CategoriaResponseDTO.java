package GameShelf.ms_categoria.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "DTO de respuesta con los datos de una categoría")
public class CategoriaResponseDTO {

    @Schema(description = "ID de la categoría", example = "1")
    private Long id;

    @Schema(description = "Nombre de la categoría", example = "AVENTURA")
    private String nombre;

    @Schema(description = "Descripción de la categoría", example = "Videojuegos de aventura, exploración y misiones")
    private String descripcion;

    @Schema(description = "Estado de la categoría", example = "ACTIVO")
    private String estado;
}
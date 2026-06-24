package GameShelf.ms_stock.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "DTO interno usado por Feign para recibir información del microservicio de videojuegos.")
public class VideoJuegoResponseDTO {

    @Schema(description = "ID del videojuego.", example = "2")
    private Long id;

    @Schema(description = "Título del videojuego.", example = "The Legend of Zelda")
    private String titulo;

    @Schema(description = "Descripción del videojuego.", example = "Videojuego de aventura y exploración")
    private String descripcion;

    @Schema(description = "Precio referencial del videojuego.", example = "49990")
    private Double precio;

    @Schema(description = "ID de la categoría asociada al videojuego.", example = "1")
    private Long categoriaId;

    @Schema(description = "Nombre de la categoría asociada.", example = "Aventura")
    private String nombreCategoria;

    @Schema(description = "Plataforma del videojuego.", example = "Nintendo Switch")
    private String plataforma;

    @Schema(description = "Estado del videojuego recibido desde ms-videojuego. Para crear stock debe estar DISPONIBLE.", example = "DISPONIBLE")
    private String estado;
}
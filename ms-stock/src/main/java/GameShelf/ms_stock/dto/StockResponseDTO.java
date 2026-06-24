package GameShelf.ms_stock.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "DTO de respuesta con la información del stock de un videojuego.")
public class StockResponseDTO {

    @Schema(description = "ID único del registro de stock.", example = "1")
    private Long id;

    @Schema(description = "ID del videojuego asociado al stock.", example = "2")
    private Long videojuegoId;

    @Schema(description = "Cantidad total de copias existentes para el videojuego.", example = "10")
    private Integer cantidadTotal;

    @Schema(description = "Cantidad disponible actual para préstamo o reserva.", example = "8")
    private Integer cantidadDisponible;

    @Schema(description = "Estado actual del stock. Valores esperados: ACTIVO o INACTIVO.", example = "ACTIVO")
    private String estado;
}
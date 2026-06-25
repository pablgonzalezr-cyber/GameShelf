package GameShelf.ms_prestamo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "DTO interno usado por Feign para recibir información desde ms-stock")
public class StockResponseDTO {

    @Schema(description = "ID único del stock", example = "1")
    private Long id;

    @Schema(description = "ID del videojuego asociado al stock", example = "2")
    private Long videojuegoId;

    @Schema(description = "Cantidad total de copias existentes", example = "10")
    private Integer cantidadTotal;

    @Schema(description = "Cantidad disponible para préstamo", example = "8")
    private Integer cantidadDisponible;

    @Schema(
            description = "Estado actual del stock. Para crear préstamos debe estar ACTIVO",
            example = "ACTIVO",
            allowableValues = {"ACTIVO", "INACTIVO"}
    )
    private String estado;
}
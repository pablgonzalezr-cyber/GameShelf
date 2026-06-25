package GameShelf.ms_stock.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "DTO de entrada para crear o actualizar stock de un videojuego")
public class StockRequestDTO {

    @NotNull(message = "El ID del videojuego es obligatorio")
    @Schema(
            description = "ID del videojuego al que pertenece el stock. Se valida remotamente mediante ms-videojuego",
            example = "2",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private Long videojuegoId;

    @NotNull(message = "La cantidad total es obligatoria")
    @Min(value = 0, message = "La cantidad total no puede ser negativa")
    @Schema(
            description = "Cantidad total de copias existentes para el videojuego",
            example = "10",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private Integer cantidadTotal;

    @NotNull(message = "La cantidad disponible es obligatoria")
    @Min(value = 0, message = "La cantidad disponible no puede ser negativa")
    @Schema(
            description = "Cantidad de copias disponibles para préstamo o reserva. No puede ser mayor que la cantidad total",
            example = "10",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private Integer cantidadDisponible;

    @Schema(
            description = "Estado del stock. Si no se envía, el sistema asigna ACTIVO por defecto",
            example = "ACTIVO",
            allowableValues = {"ACTIVO", "INACTIVO"},
            requiredMode = Schema.RequiredMode.NOT_REQUIRED
    )
    private String estado;
}
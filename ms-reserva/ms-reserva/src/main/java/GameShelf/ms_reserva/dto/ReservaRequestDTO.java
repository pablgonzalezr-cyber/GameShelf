package GameShelf.ms_reserva.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO de entrada para crear o actualizar una reserva")
public class ReservaRequestDTO {

    @NotNull(message = "El ID del usuario es obligatorio")
    @Schema(
            description = "ID del usuario que realiza la reserva. Se valida remotamente mediante ms-usuario",
            example = "1",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private Long usuarioId;

    @NotNull(message = "El ID del videojuego es obligatorio")
    @Schema(
            description = "ID del videojuego reservado. Se valida remotamente mediante ms-videojuego",
            example = "2",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private Long videojuegoId;

    @Schema(
            description = "Estado de la reserva. En creación el sistema asigna PENDIENTE automáticamente; en actualización puede usarse para cambiar el estado",
            example = "PENDIENTE",
            allowableValues = {"PENDIENTE", "CONFIRMADA", "CANCELADA", "EXPIRADA"},
            requiredMode = Schema.RequiredMode.NOT_REQUIRED
    )
    private String estado;
}
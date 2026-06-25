package GameShelf.ms_multa.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "DTO de entrada para crear o actualizar una multa")
public class MultaRequestDTO {

    @NotNull(message = "El ID del usuario es obligatorio")
    @Schema(
            description = "ID del usuario asociado a la multa. Se valida remotamente mediante ms-usuario",
            example = "1",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private Long usuarioId;

    @NotNull(message = "El ID del préstamo es obligatorio")
    @Schema(
            description = "ID del préstamo asociado a la multa. Se valida remotamente mediante ms-prestamo",
            example = "1",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private Long prestamoId;

    @NotNull(message = "El monto es obligatorio")
    @DecimalMin(value = "1.0", message = "El monto debe ser mayor a 0")
    @Schema(
            description = "Monto total de la multa",
            example = "5000",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private Double monto;

    @NotBlank(message = "El motivo es obligatorio")
    @Size(min = 5, max = 150, message = "El motivo debe tener entre 5 y 150 caracteres")
    @Schema(
            description = "Motivo por el cual se genera la multa",
            example = "Atraso en la devolución del videojuego",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String motivo;

    @Schema(
            description = "Estado de la multa. En creación el sistema asigna PENDIENTE automáticamente; en actualización debe ser PENDIENTE, PAGADA o ANULADA",
            example = "PENDIENTE",
            allowableValues = {"PENDIENTE", "PAGADA", "ANULADA"},
            requiredMode = Schema.RequiredMode.NOT_REQUIRED
    )
    private String estado;
}
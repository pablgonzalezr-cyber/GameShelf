package GameShelf.ms_multa.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "DTO de entrada para crear o actualizar una multa.")
public class MultaRequestDTO {

    @NotNull(message = "El ID del usuario es obligatorio")
    @Schema(description = "ID del usuario asociado a la multa.", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long usuarioId;

    @NotNull(message = "El ID del préstamo es obligatorio")
    @Schema(description = "ID del préstamo asociado a la multa.", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long prestamoId;

    @NotNull(message = "El monto es obligatorio")
    @DecimalMin(value = "1.0", message = "El monto debe ser mayor a 0")
    @Schema(description = "Monto total de la multa.", example = "5000", requiredMode = Schema.RequiredMode.REQUIRED)
    private Double monto;

    @NotBlank(message = "El motivo es obligatorio")
    @Schema(description = "Motivo por el cual se genera la multa.", example = "Atraso en la devolución del videojuego", requiredMode = Schema.RequiredMode.REQUIRED)
    private String motivo;

    @Schema(description = "Estado de la multa. Valores esperados: PENDIENTE, PAGADA o ANULADA. Al crear una multa el sistema la asigna como PENDIENTE.", example = "PENDIENTE")
    private String estado;
}
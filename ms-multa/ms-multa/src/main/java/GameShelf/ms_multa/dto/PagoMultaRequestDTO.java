package GameShelf.ms_multa.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "DTO de entrada para registrar el pago completo de una multa.")
public class PagoMultaRequestDTO {

    @NotNull(message = "El monto pagado es obligatorio")
    @DecimalMin(value = "1.0", message = "El monto pagado debe ser mayor a 0")
    @Schema(description = "Monto pagado por el usuario. Debe ser exactamente igual al monto total de la multa.", example = "5000", requiredMode = Schema.RequiredMode.REQUIRED)
    private Double montoPagado;

    @NotBlank(message = "El método de pago es obligatorio")
    @Schema(description = "Método utilizado para pagar la multa.", example = "EFECTIVO", requiredMode = Schema.RequiredMode.REQUIRED)
    private String metodoPago;
}
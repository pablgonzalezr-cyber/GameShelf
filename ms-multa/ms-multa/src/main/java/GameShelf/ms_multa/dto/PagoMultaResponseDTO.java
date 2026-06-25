package GameShelf.ms_multa.dto;

import java.time.LocalDate;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "DTO de respuesta con la información de un pago asociado a una multa")
public class PagoMultaResponseDTO {

    @Schema(description = "ID único del pago", example = "1")
    private Long id;

    @Schema(description = "ID de la multa pagada", example = "1")
    private Long multaId;

    @Schema(description = "Monto pagado en la operación", example = "5000")
    private Double montoPagado;

    @Schema(description = "Fecha en que se registró el pago", example = "2026-06-24")
    private LocalDate fechaPago;

    @Schema(description = "Método utilizado para realizar el pago", example = "EFECTIVO")
    private String metodoPago;

    @Schema(
            description = "Estado del pago registrado",
            example = "CONFIRMADO",
            allowableValues = {"CONFIRMADO"}
    )
    private String estadoPago;
}
package GameShelf.ms_multa.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PagoMultaRequestDTO {

    @NotNull(message = "El monto pagado es obligatorio")
    @DecimalMin(value = "1.0", message = "El monto pagado debe ser mayor a 0")
    private Double montoPagado;

    @NotBlank(message = "El método de pago es obligatorio")
    private String metodoPago;
}

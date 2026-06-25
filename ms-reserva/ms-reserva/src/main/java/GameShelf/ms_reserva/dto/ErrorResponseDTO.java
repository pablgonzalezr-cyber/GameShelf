package GameShelf.ms_reserva.dto;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO utilizado para representar errores de validación, negocio o comunicación entre microservicios")
public class ErrorResponseDTO {

    @Schema(description = "Fecha y hora en que ocurrió el error", example = "2026-06-24T15:30:00")
    private LocalDateTime fecha;

    @Schema(description = "Código HTTP del error", example = "400")
    private int estado;

    @Schema(description = "Mensaje descriptivo del error", example = "Estado de reserva inválido")
    private String mensaje;

    @Schema(description = "Tipo o categoría del error", example = "Dato inválido")
    private String error;
}
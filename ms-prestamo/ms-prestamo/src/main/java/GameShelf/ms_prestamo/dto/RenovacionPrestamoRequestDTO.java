package GameShelf.ms_prestamo.dto;

import java.time.LocalDate;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "DTO de entrada para renovar un préstamo existente.")
public class RenovacionPrestamoRequestDTO {

    @NotNull(message = "La nueva fecha de devolución es obligatoria")
    @Future(message = "La nueva fecha de devolución debe ser futura")
    @Schema(description = "Nueva fecha de devolución solicitada para el préstamo.", example = "2026-07-15", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDate nuevaFechaDevolucion;

    @NotBlank(message = "El motivo de la renovación es obligatorio")
    @Schema(description = "Motivo por el cual se solicita la renovación del préstamo.", example = "El usuario solicita más tiempo para devolver el videojuego", requiredMode = Schema.RequiredMode.REQUIRED)
    private String motivo;
}
package GameShelf.ms_prestamo.dto;

import java.time.LocalDate;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "DTO de entrada para crear un préstamo de videojuego.")
public class PrestamoRequestDTO {

    @NotNull(message = "El ID del usuario es obligatorio")
    @Schema(description = "ID del usuario que solicita el préstamo.", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long usuarioId;

    @NotNull(message = "El ID del videojuego es obligatorio")
    @Schema(description = "ID del videojuego que será prestado.", example = "2", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long videojuegoId;

    @Schema(description = "Fecha de creación del préstamo. El sistema la asigna automáticamente usando la fecha actual.", example = "2026-06-24")
    private LocalDate fechaPrestamo;

    @NotNull(message = "La fecha de devolución es obligatoria")
    @Schema(description = "Fecha límite de devolución del videojuego.", example = "2026-07-01", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDate fechaDevolucion;

    @Schema(description = "Estado del préstamo. El sistema lo asigna automáticamente como PRESTADO al crear.", example = "PRESTADO")
    private String estado;
}
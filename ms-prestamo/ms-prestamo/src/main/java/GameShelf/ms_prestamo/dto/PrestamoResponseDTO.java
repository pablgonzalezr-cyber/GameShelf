package GameShelf.ms_prestamo.dto;

import java.time.LocalDate;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "DTO de respuesta con la información de un préstamo registrado")
public class PrestamoResponseDTO {

    @Schema(description = "ID único del préstamo", example = "1")
    private Long id;

    @Schema(description = "ID del usuario asociado al préstamo", example = "1")
    private Long usuarioId;

    @Schema(description = "ID del videojuego asociado al préstamo", example = "2")
    private Long videojuegoId;

    @Schema(description = "Fecha en que se creó el préstamo", example = "2026-06-24")
    private LocalDate fechaPrestamo;

    @Schema(description = "Fecha límite o fecha final de devolución del préstamo", example = "2026-07-01")
    private LocalDate fechaDevolucion;

    @Schema(
            description = "Estado actual del préstamo",
            example = "PRESTADO",
            allowableValues = {"PRESTADO", "DEVUELTO", "CANCELADO"}
    )
    private String estado;
}
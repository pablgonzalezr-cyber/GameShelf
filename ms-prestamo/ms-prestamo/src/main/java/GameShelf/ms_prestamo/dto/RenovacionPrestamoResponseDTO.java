package GameShelf.ms_prestamo.dto;

import java.time.LocalDate;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "DTO de respuesta con la información de una renovación de préstamo")
public class RenovacionPrestamoResponseDTO {

    @Schema(description = "ID único de la renovación", example = "1")
    private Long id;

    @Schema(description = "ID del préstamo renovado", example = "1")
    private Long prestamoId;

    @Schema(description = "Fecha de devolución anterior antes de aplicar la renovación", example = "2026-07-01")
    private LocalDate fechaAnteriorDevolucion;

    @Schema(description = "Nueva fecha de devolución asignada al préstamo", example = "2026-07-15")
    private LocalDate nuevaFechaDevolucion;

    @Schema(description = "Motivo registrado para la renovación", example = "El usuario solicita más tiempo para devolver el videojuego")
    private String motivo;

    @Schema(description = "Fecha en que se registró la renovación", example = "2026-06-24")
    private LocalDate fechaRenovacion;
}
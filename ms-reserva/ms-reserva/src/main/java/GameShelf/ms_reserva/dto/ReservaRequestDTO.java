package GameShelf.ms_reserva.dto;

import java.time.LocalDate;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO de entrada para crear o actualizar una reserva.")
public class ReservaRequestDTO {

    @NotNull(message = "El ID del usuario es obligatorio")
    @Schema(description = "ID del usuario que realiza la reserva.", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long usuarioId;

    @NotNull(message = "El ID del videojuego es obligatorio")
    @Schema(description = "ID del videojuego reservado.", example = "2", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long videojuegoId;

    @Schema(description = "Fecha de creación de la reserva. El sistema puede asignarla automáticamente usando la fecha actual.", example = "2026-06-24")
    private LocalDate fechaReserva;

    @Schema(description = "Estado de la reserva. Valores esperados: PENDIENTE, CONFIRMADA o CANCELADA. Al crear una reserva normalmente inicia como PENDIENTE.", example = "PENDIENTE")
    private String estado;
}
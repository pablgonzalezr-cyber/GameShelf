package GameShelf.ms_reserva.dto;

import java.time.LocalDate;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO de respuesta con la información de una reserva registrada")
public class ReservaResponseDTO {

    @Schema(description = "ID único de la reserva", example = "1")
    private Long id;

    @Schema(description = "ID del usuario asociado a la reserva", example = "1")
    private Long usuarioId;

    @Schema(description = "ID del videojuego asociado a la reserva", example = "2")
    private Long videojuegoId;

    @Schema(description = "Fecha en que se generó la reserva", example = "2026-06-24")
    private LocalDate fechaReserva;

    @Schema(
            description = "Estado actual de la reserva",
            example = "PENDIENTE",
            allowableValues = {"PENDIENTE", "CONFIRMADA", "CANCELADA", "EXPIRADA"}
    )
    private String estado;
}
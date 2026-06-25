package GameShelf.ms_reserva.dto;

import java.time.LocalDate;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "DTO de respuesta con el historial de cambios de estado de una reserva")
public class HistorialReservaResponseDTO {

    @Schema(description = "ID único del registro de historial", example = "1")
    private Long id;

    @Schema(description = "ID de la reserva asociada al historial", example = "1")
    private Long reservaId;

    @Schema(
            description = "Estado anterior de la reserva antes del cambio",
            example = "PENDIENTE",
            allowableValues = {"PENDIENTE", "CONFIRMADA", "CANCELADA", "EXPIRADA"}
    )
    private String estadoAnterior;

    @Schema(
            description = "Nuevo estado asignado a la reserva",
            example = "CONFIRMADA",
            allowableValues = {"PENDIENTE", "CONFIRMADA", "CANCELADA", "EXPIRADA"}
    )
    private String estadoNuevo;

    @Schema(description = "Fecha en que se registró el cambio de estado", example = "2026-06-24")
    private LocalDate fechaCambio;

    @Schema(description = "Motivo o descripción del cambio realizado", example = "Reserva confirmada correctamente")
    private String motivo;
}
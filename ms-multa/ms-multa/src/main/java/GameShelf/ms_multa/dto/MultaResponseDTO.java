package GameShelf.ms_multa.dto;

import java.time.LocalDate;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "DTO de respuesta con la información de una multa registrada")
public class MultaResponseDTO {

    @Schema(description = "ID único de la multa", example = "1")
    private Long id;

    @Schema(description = "ID del usuario asociado a la multa", example = "1")
    private Long usuarioId;

    @Schema(description = "ID del préstamo asociado a la multa", example = "1")
    private Long prestamoId;

    @Schema(description = "Monto total de la multa", example = "5000")
    private Double monto;

    @Schema(description = "Motivo registrado para la multa", example = "Atraso en la devolución del videojuego")
    private String motivo;

    @Schema(description = "Fecha en que se generó la multa", example = "2026-06-24")
    private LocalDate fechaMulta;

    @Schema(
            description = "Estado actual de la multa",
            example = "PENDIENTE",
            allowableValues = {"PENDIENTE", "PAGADA", "ANULADA"}
    )
    private String estado;
}
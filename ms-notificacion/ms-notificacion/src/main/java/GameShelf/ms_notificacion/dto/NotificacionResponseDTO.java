package GameShelf.ms_notificacion.dto;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO de respuesta con los datos de una notificación")
public class NotificacionResponseDTO {

    @Schema(description = "ID único de la notificación", example = "1")
    private Long id;

    @Schema(description = "ID del usuario asociado a la notificación", example = "1")
    private Long usuarioId;

    @Schema(description = "Título breve de la notificación", example = "Reserva creada")
    private String titulo;

    @Schema(description = "Mensaje detallado de la notificación", example = "Tu reserva fue creada correctamente")
    private String mensaje;

    @Schema(
            description = "Tipo de notificación",
            example = "RESERVA",
            allowableValues = {"RESERVA", "PRESTAMO", "MULTA", "SISTEMA"}
    )
    private String tipo;

    @Schema(
            description = "Estado actual de la notificación",
            example = "PENDIENTE",
            allowableValues = {"PENDIENTE", "LEIDA", "ELIMINADA"}
    )
    private String estado;

    @Schema(description = "Fecha y hora en que se creó la notificación", example = "2026-06-24T15:30:00")
    private LocalDateTime fechaCreacion;

    @Schema(description = "Fecha y hora en que se marcó como leída la notificación", example = "2026-06-24T16:00:00")
    private LocalDateTime fechaLectura;

    @Schema(description = "ID de referencia asociado a otra entidad del sistema", example = "10")
    private Long referenciaId;

    @Schema(
            description = "Tipo de referencia asociada a la notificación",
            example = "RESERVA",
            allowableValues = {"RESERVA", "PRESTAMO", "MULTA", "SISTEMA"}
    )
    private String referenciaTipo;
}
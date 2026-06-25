package GameShelf.ms_notificacion.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO utilizado para crear o actualizar una notificación")
public class NotificacionRequestDTO {

    @NotNull(message = "El usuario es obligatorio")
    @Schema(
            description = "ID del usuario que recibirá la notificación. Se valida remotamente mediante ms-usuario",
            example = "1",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private Long usuarioId;

    @NotBlank(message = "El título es obligatorio")
    @Size(min = 3, max = 100, message = "El título debe tener entre 3 y 100 caracteres")
    @Schema(
            description = "Título breve de la notificación",
            example = "Reserva creada",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String titulo;

    @NotBlank(message = "El mensaje es obligatorio")
    @Size(min = 5, max = 500, message = "El mensaje debe tener entre 5 y 500 caracteres")
    @Schema(
            description = "Mensaje detallado de la notificación",
            example = "Tu reserva fue creada correctamente",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String mensaje;

    @NotBlank(message = "El tipo es obligatorio")
    @Schema(
            description = "Tipo de notificación",
            example = "RESERVA",
            allowableValues = {"RESERVA", "PRESTAMO", "MULTA", "SISTEMA"},
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String tipo;

    @Schema(
            description = "Estado de la notificación. Si no se envía, el sistema asigna PENDIENTE por defecto",
            example = "PENDIENTE",
            allowableValues = {"PENDIENTE", "LEIDA", "ELIMINADA"},
            requiredMode = Schema.RequiredMode.NOT_REQUIRED
    )
    private String estado;

    @Schema(
            description = "ID de referencia asociado a otra entidad del sistema, por ejemplo una reserva, préstamo o multa",
            example = "10",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED
    )
    private Long referenciaId;

    @Size(max = 50, message = "El tipo de referencia no puede superar los 50 caracteres")
    @Schema(
            description = "Tipo de referencia asociada a la notificación",
            example = "RESERVA",
            allowableValues = {"RESERVA", "PRESTAMO", "MULTA", "SISTEMA"},
            requiredMode = Schema.RequiredMode.NOT_REQUIRED
    )
    private String referenciaTipo;
}
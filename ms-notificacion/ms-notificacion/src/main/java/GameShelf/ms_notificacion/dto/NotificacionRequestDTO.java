package GameShelf.ms_notificacion.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO utilizado para crear o actualizar una notificación")
public class NotificacionRequestDTO {

    @NotNull(message = "El usuario es obligatorio")
    @Schema(description = "ID del usuario que recibirá la notificación", example = "1")
    private Long usuarioId;

    @NotBlank(message = "El título es obligatorio")
    @Schema(description = "Título de la notificación", example = "Reserva creada")
    private String titulo;

    @NotBlank(message = "El mensaje es obligatorio")
    @Schema(description = "Mensaje detallado de la notificación", example = "Tu reserva fue creada correctamente")
    private String mensaje;

    @NotBlank(message = "El tipo es obligatorio")
    @Schema(description = "Tipo de notificación. Valores permitidos: RESERVA, PRESTAMO, MULTA o SISTEMA", example = "RESERVA")
    private String tipo;

    @Schema(description = "Estado de la notificación. Si no se envía, se asigna PENDIENTE. Valores permitidos: PENDIENTE, LEIDA o ELIMINADA", example = "PENDIENTE")
    private String estado;

    @Schema(description = "ID de referencia asociado a otra entidad del sistema, por ejemplo una reserva, préstamo o multa", example = "10")
    private Long referenciaId;

    @Schema(description = "Tipo de referencia asociada a la notificación", example = "RESERVA")
    private String referenciaTipo;
}


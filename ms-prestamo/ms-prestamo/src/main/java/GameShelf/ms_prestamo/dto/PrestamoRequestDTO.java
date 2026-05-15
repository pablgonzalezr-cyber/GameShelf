package GameShelf.ms_prestamo.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PrestamoRequestDTO {

    @NotNull(message = "El ID del usuario es obligatorio")
    private Long usuarioId;

    @NotNull(message = "El ID del videojuego es obligatorio")
    private Long videojuegoId;
}
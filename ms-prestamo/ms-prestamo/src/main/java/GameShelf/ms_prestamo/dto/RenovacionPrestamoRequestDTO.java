package GameShelf.ms_prestamo.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RenovacionPrestamoRequestDTO {

    @NotNull(message = "La nueva fecha de devolución es obligatoria")
    @Future(message = "La nueva fecha de devolución debe ser futura")
    private LocalDate nuevaFechaDevolucion;

    @NotBlank(message = "El motivo de la renovación es obligatorio")
    private String motivo;
}
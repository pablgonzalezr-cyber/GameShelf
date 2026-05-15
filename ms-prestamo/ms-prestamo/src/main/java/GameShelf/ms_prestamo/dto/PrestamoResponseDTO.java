package GameShelf.ms_prestamo.dto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PrestamoResponseDTO {

    private Long id;
    private Long usuarioId;
    private Long videojuegoId;
    private LocalDate fechaPrestamo;
    private LocalDate fechaDevolucion;
    private String estado;
}
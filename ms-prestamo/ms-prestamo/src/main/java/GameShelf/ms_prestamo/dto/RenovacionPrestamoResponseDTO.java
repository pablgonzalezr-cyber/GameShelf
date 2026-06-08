package GameShelf.ms_prestamo.dto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RenovacionPrestamoResponseDTO {

    private Long id;
    private Long prestamoId;
    private LocalDate fechaAnteriorDevolucion;
    private LocalDate nuevaFechaDevolucion;
    private String motivo;
    private LocalDate fechaRenovacion;
}
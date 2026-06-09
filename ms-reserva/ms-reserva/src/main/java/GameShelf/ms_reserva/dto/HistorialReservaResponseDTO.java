package GameShelf.ms_reserva.dto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HistorialReservaResponseDTO {

    private Long id;
    private Long reservaId;
    private String estadoAnterior;
    private String estadoNuevo;
    private LocalDate fechaCambio;
    private String motivo;
}

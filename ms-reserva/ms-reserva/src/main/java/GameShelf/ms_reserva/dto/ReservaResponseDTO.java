package GameShelf.ms_reserva.dto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReservaResponseDTO {

    private Long id;
    private Long usuarioId;
    private Long videojuegoId;
    private LocalDate fechaReserva;
    private String estado;
}
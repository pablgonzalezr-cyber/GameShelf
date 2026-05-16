package GameShelf.ms_reserva.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StockResponseDTO {

    private Long id;
    private Long videojuegoId;
    private Integer cantidadTotal;
    private Integer cantidadDisponible;
    private String estado;
}

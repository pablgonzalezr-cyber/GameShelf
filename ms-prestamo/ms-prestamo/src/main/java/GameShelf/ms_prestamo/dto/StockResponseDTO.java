package GameShelf.ms_prestamo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StockResponseDTO {

    private Long id;
    private Long videojuegoId;
    private Integer cantidadTotal;
    private Integer cantidadDisponible;
    private String estado;
}
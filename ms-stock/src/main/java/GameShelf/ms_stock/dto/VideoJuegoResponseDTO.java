package GameShelf.ms_stock.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VideoJuegoResponseDTO {

    private Long id;
    private String titulo;
    private String descripcion;
    private Double precio;
    private Long categoriaId;
    private String estado;
}

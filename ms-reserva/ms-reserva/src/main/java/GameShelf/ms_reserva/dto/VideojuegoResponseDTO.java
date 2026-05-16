package GameShelf.ms_reserva.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VideojuegoResponseDTO {

    private Long id;
    private String titulo;
    private String descripcion;
    private Double precio;
    private Long categoriaId;
    private String nombreCategoria;
    private String plataforma;
    private String estado;
}

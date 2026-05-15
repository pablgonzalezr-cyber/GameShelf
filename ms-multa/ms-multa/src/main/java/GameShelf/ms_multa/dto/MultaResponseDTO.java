package GameShelf.ms_multa.dto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MultaResponseDTO {

    private Long id;
    private Long usuarioId;
    private Long prestamoId;
    private Double monto;
    private String motivo;
    private LocalDate fechaMulta;
    private String estado;
}
package GameShelf.ms_multa.dto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PagoMultaResponseDTO {

    private Long id;
    private Long multaId;
    private Double montoPagado;
    private LocalDate fechaPago;
    private String metodoPago;
    private String estadoPago;
}
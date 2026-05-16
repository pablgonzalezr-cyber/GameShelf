package GameShelf.ms_autorizacion.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AutorizacionResponseDTO {

    private Long id;
    private Long usuarioId;
    private String rol;
    private String modulo;
    private String permiso;
    private String estado;
}
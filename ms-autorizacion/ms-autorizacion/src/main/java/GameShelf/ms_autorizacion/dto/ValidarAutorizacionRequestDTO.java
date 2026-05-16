package GameShelf.ms_autorizacion.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ValidarAutorizacionRequestDTO {

    @NotNull(message = "El usuario es obligatorio")
    private Long usuarioId;

    @NotBlank(message = "El módulo es obligatorio")
    private String modulo;

    @NotBlank(message = "El permiso es obligatorio")
    private String permiso;
}

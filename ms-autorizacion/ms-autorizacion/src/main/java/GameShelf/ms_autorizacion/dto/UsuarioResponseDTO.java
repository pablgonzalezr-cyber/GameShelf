package GameShelf.ms_autorizacion.dto;

import lombok.Data;

@Data
public class UsuarioResponseDTO {

    private Long id;
    private String usuario;
    private String correo;
    private String rol;
}
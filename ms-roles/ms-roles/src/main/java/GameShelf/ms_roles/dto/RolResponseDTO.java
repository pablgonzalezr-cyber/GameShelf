package GameShelf.ms_roles.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RolResponseDTO {

    private Long id;
    private String nombre;
    private String descripcion;
    private String estado;
}
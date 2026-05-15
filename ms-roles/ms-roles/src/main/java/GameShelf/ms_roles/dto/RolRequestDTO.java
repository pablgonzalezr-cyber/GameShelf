package GameShelf.ms_roles.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RolRequestDTO {

    @NotBlank(message = "El nombre del rol es obligatorio")
    @Size(min = 3, max = 30, message = "El nombre del rol debe tener entre 3 y 30 caracteres")
    private String nombre;

    @NotBlank(message = "La descripción del rol es obligatoria")
    @Size(min = 5, max = 150, message = "La descripción debe tener entre 5 y 150 caracteres")
    private String descripcion;

    private String estado;
}
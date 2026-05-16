package GameShelf.ms_autorizacion.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "autorizaciones")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AutorizacionModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long usuarioId;

    private String rol;

    private String modulo;

    private String permiso;

    private String estado;
}

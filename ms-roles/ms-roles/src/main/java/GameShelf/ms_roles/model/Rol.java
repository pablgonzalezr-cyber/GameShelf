package GameShelf.ms_roles.model;

import lombok.NoArgsConstructor;
import lombok.Data;
import jakarta.persistence.*;

@NoArgsConstructor
@Data
@Entity
public class Rol {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique=true )
    private String nombre;

    @Column(nullable= false)
    private String descripcion;

    @Column(nullable=false)
    private String estado;

}

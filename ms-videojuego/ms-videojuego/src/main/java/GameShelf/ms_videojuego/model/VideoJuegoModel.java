package GameShelf.ms_videojuego.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "videojuegos")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VideoJuegoModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String titulo;

    @Column(nullable = false, length = 250)
    private String descripcion;

    @Column(nullable = false)
    private Double precio;

    @Column(nullable = false)
    private Long categoriaId;

    @Column(nullable = false, length = 80)
    private String nombreCategoria;

    @Column(nullable = false, length = 40)
    private String plataforma;

    @Column(nullable = false, length = 30)
    private String estado;
}
package GameShelf.ms_videojuego.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "videojuegos")
@Data
@AllArgsConstructor
@NoArgsConstructor

public class VideoJuegoModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String titulo;

    private String descripcion;

    private Double precio;

    // Este ID conecta con el microservicio ms-categoria
    @Column(nullable = false)
    private Long categoriaId;
}

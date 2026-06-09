package GameShelf.ms_prestamo.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "prestamos")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PrestamoModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Relación distribuida con ms-usuario.
    // Se valida mediante UsuarioClient.
    @Column(nullable = false)
    private Long usuarioId;

    // Relación distribuida con ms-videojuego.
    // Se valida mediante VideoJuegoClient.
    @Column(nullable = false)
    private Long videojuegoId;

    @Column(nullable = false)
    private LocalDate fechaPrestamo;

    @Column(nullable = false)
    private LocalDate fechaDevolucion;

    @Column(nullable = false, length = 20)
    private String estado;

    // Relación JPA interna:
    // Un préstamo puede tener muchas renovaciones.
    @OneToMany(mappedBy = "prestamo", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<RenovacionPrestamoModel> renovaciones = new ArrayList<>();
}
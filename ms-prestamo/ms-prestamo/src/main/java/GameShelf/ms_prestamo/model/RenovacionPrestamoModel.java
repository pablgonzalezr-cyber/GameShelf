package GameShelf.ms_prestamo.model;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "renovaciones_prestamo")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RenovacionPrestamoModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Relación JPA interna:
    // Muchas renovaciones pertenecen a un préstamo.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prestamo_id", nullable = false)
    private PrestamoModel prestamo;

    @Column(nullable = false)
    private LocalDate fechaAnteriorDevolucion;

    @Column(nullable = false)
    private LocalDate nuevaFechaDevolucion;

    @Column(nullable = false, length = 255)
    private String motivo;

    @Column(nullable = false)
    private LocalDate fechaRenovacion;
}
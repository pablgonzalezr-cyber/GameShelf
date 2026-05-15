package GameShelf.ms_multa.model;

import java.time.LocalDate;

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
@Table(name = "multas")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MultaModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long usuarioId;

    @Column(nullable = false)
    private Long prestamoId;

    @Column(nullable = false)
    private Double monto;

    @Column(nullable = false, length = 150)
    private String motivo;

    @Column(nullable = false)
    private LocalDate fechaMulta;

    @Column(nullable = false, length = 20)
    private String estado;
}
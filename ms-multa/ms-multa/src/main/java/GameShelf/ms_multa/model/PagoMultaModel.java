package GameShelf.ms_multa.model;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonIgnore;

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
@Table(name = "pagos_multa")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PagoMultaModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "multa_id", nullable = false)
    @JsonIgnore
    private MultaModel multa;

    @Column(nullable = false)
    private Double montoPagado;

    @Column(nullable = false)
    private LocalDate fechaPago;

    @Column(nullable = false, length = 30)
    private String metodoPago;

    @Column(nullable = false, length = 20)
    private String estadoPago;
}
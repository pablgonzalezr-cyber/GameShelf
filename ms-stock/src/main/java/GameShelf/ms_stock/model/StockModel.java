package GameShelf.ms_stock.model;

import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "stocks")
public class StockModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Relación lógica con el microservicio ms-videojuego
    @Column(nullable = false, unique = true)
    private Long videojuegoId;

    private Integer cantidadTotal;    // Cuántos juegos existen en total
    private Integer cantidadDisponible; // Cuántos están libres para préstamo

}

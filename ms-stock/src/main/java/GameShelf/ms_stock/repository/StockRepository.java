package GameShelf.ms_stock.repository;


import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import GameShelf.ms_stock.model.StockModel;

@Repository
public interface StockRepository extends JpaRepository<StockModel, Long>{
    
    // Buscar el stock de un videojuego específico
    Optional<StockModel> findByVideojuegoId(Long videojuegoId);
}

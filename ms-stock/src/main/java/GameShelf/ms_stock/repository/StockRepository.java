package GameShelf.ms_stock.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import GameShelf.ms_stock.model.StockModel;

@Repository
public interface StockRepository extends JpaRepository<StockModel, Long> {

    Optional<StockModel> findByVideojuegoId(Long videojuegoId);

    List<StockModel> findByEstado(String estado);

    boolean existsByVideojuegoId(Long videojuegoId);
}
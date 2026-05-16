package GameShelf.ms_reserva.repository;

import GameShelf.ms_reserva.model.ReservaModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReservaRepository extends JpaRepository<ReservaModel, Long> {

    List<ReservaModel> findByUsuarioId(Long usuarioId);

    List<ReservaModel> findByVideojuegoId(Long videojuegoId);

    boolean existsByUsuarioIdAndVideojuegoIdAndEstado(Long usuarioId, Long videojuegoId, String estado);
}
package GameShelf.ms_reserva.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import GameShelf.ms_reserva.model.ReservaModel;

@Repository
public interface ReservaRepository extends JpaRepository<ReservaModel, Long> {

    List<ReservaModel> findByUsuarioId(Long usuarioId);

    List<ReservaModel> findByVideojuegoId(Long videojuegoId);

    List<ReservaModel> findByEstado(String estado);

    boolean existsByUsuarioIdAndVideojuegoIdAndEstadoIn(
            Long usuarioId,
            Long videojuegoId,
            List<String> estados
    );
}
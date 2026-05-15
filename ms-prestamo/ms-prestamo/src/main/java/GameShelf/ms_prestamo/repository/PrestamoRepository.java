package GameShelf.ms_prestamo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import GameShelf.ms_prestamo.model.PrestamoModel;

@Repository
public interface PrestamoRepository extends JpaRepository<PrestamoModel, Long> {

    List<PrestamoModel> findByUsuarioId(Long usuarioId);

    List<PrestamoModel> findByVideojuegoId(Long videojuegoId);

    List<PrestamoModel> findByEstado(String estado);

    boolean existsByUsuarioIdAndVideojuegoIdAndEstado(Long usuarioId, Long videojuegoId, String estado);
}
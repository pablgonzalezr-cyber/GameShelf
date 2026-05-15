package GameShelf.ms_multa.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import GameShelf.ms_multa.model.MultaModel;

@Repository
public interface MultaRepository extends JpaRepository<MultaModel, Long> {

    List<MultaModel> findByUsuarioId(Long usuarioId);

    List<MultaModel> findByPrestamoId(Long prestamoId);

    List<MultaModel> findByEstado(String estado);

    boolean existsByPrestamoIdAndEstado(Long prestamoId, String estado);
}

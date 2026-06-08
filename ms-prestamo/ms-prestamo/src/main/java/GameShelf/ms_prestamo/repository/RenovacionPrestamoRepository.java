package GameShelf.ms_prestamo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import GameShelf.ms_prestamo.model.RenovacionPrestamoModel;

@Repository
public interface RenovacionPrestamoRepository extends JpaRepository<RenovacionPrestamoModel, Long> {

    List<RenovacionPrestamoModel> findByPrestamoIdOrderByFechaRenovacionAsc(Long prestamoId);
}
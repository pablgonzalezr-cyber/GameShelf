package GameShelf.ms_reserva.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import GameShelf.ms_reserva.model.HistorialReservaModel;

@Repository
public interface HistorialReservaRepository extends JpaRepository<HistorialReservaModel, Long> {

    List<HistorialReservaModel> findByReservaId(Long reservaId);
}
package GameShelf.ms_multa.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import GameShelf.ms_multa.model.PagoMultaModel;

@Repository
public interface PagoMultaRepository extends JpaRepository<PagoMultaModel, Long> {

    List<PagoMultaModel> findByMultaId(Long multaId);
}
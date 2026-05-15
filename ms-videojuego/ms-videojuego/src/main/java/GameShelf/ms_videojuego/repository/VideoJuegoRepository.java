package GameShelf.ms_videojuego.repository;

import java.util.List;

import GameShelf.ms_videojuego.model.VideoJuegoModel;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VideoJuegoRepository extends JpaRepository<VideoJuegoModel, Long> {

    boolean existsByTituloIgnoreCaseAndPlataformaIgnoreCase(String titulo, String plataforma);

    boolean existsByTituloIgnoreCaseAndPlataformaIgnoreCaseAndIdNot(String titulo, String plataforma, Long id);

    List<VideoJuegoModel> findByCategoriaId(Long categoriaId);

    List<VideoJuegoModel> findByTituloContainingIgnoreCase(String titulo);

    List<VideoJuegoModel> findByEstado(String estado);

    List<VideoJuegoModel> findByPlataforma(String plataforma);
}
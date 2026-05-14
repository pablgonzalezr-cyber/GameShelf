package GameShelf.ms_videojuego.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import GameShelf.ms_videojuego.model.VideoJuegoModel;

@Repository
public interface VideoJuegoRepository extends JpaRepository<VideoJuegoModel, Long> {

    List<VideoJuegoModel> findByCategoriaId(Long categoriaId);

    List<VideoJuegoModel> findByTituloContainingIgnoreCase(String titulo);

    List<VideoJuegoModel> findByEstado(String estado);

    boolean existsByTitulo(String titulo);
}
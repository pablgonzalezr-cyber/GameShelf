package GameShelf.ms_categoria.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import GameShelf.ms_categoria.model.CategoriaModel;

@Repository
public interface CategoriaRepository extends JpaRepository<CategoriaModel, Long> {

    boolean existsByNombre(String nombre);

    Optional<CategoriaModel> findByNombre(String nombre);

    List<CategoriaModel> findByEstado(String estado);

    List<CategoriaModel> findByNombreContainingIgnoreCase(String nombre);
}
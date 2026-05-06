package GameShelf.ms_categoria.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import GameShelf.ms_categoria.model.CategoriaModel;

@Repository
public interface CategoriaRepository extends JpaRepository<CategoriaModel, Long> {
    
    // Método para validar si una categoría existe por nombre 
    boolean existsByNombre(String nombre);

    // Buscar una categoría específica por su nombre
    Optional<CategoriaModel> findByNombre(String nombre);
}

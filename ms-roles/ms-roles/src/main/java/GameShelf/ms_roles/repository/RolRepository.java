package GameShelf.ms_roles.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import GameShelf.ms_roles.model.Rol;

@Repository
public interface RolRepository extends JpaRepository<Rol, Long> {

    Optional<Rol> findByNombre(String nombre);

    boolean existsByNombre(String nombre);

    boolean existsByNombreAndIdNot(String nombre, Long id);

    boolean existsByNombreAndEstado(String nombre, String estado);

    List<Rol> findByEstado(String estado);

    List<Rol> findByNombreContainingIgnoreCase(String nombre);
}
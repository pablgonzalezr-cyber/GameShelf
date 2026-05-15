package GameShelf.ms_roles.repository;

import java.util.List;
import java.util.Optional;

import GameShelf.ms_roles.model.Rol;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RolRepository extends JpaRepository<Rol, Long> {

    Optional<Rol> findByNombre(String nombre);

    boolean existsByNombre(String nombre);

    boolean existsByNombreAndIdNot(String nombre, Long id);

    List<Rol> findByEstado(String estado);

    List<Rol> findByNombreContainingIgnoreCase(String nombre);
}

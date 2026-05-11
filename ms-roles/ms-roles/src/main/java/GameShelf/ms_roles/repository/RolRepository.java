package GameShelf.ms_roles.repository;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface RolRepository extends JpaRepository<Rol, Long> {

    Optional<Rol> findByNombre(String nombre);
    
    boolean existsByNombre(String nombre);

    List<Rol> findByEstado(String estado);

    List<Rol> findByNombreContainingIgnoreCase(String nombre);
}

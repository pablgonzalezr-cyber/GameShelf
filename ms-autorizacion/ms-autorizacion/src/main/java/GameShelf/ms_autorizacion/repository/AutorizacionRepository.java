package GameShelf.ms_autorizacion.repository;

import GameShelf.ms_autorizacion.model.AutorizacionModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AutorizacionRepository extends JpaRepository<AutorizacionModel, Long> {

    List<AutorizacionModel> findByUsuarioId(Long usuarioId);

    boolean existsByUsuarioIdAndModuloIgnoreCaseAndPermisoIgnoreCaseAndEstadoIgnoreCase(
            Long usuarioId,
            String modulo,
            String permiso,
            String estado
    );
}
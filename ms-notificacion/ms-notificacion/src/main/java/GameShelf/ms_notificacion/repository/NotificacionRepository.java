package GameShelf.ms_notificacion.repository;

import GameShelf.ms_notificacion.model.NotificacionModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificacionRepository extends JpaRepository<NotificacionModel, Long> {

    List<NotificacionModel> findByUsuarioId(Long usuarioId);

    List<NotificacionModel> findByUsuarioIdAndEstado(Long usuarioId, String estado);
}

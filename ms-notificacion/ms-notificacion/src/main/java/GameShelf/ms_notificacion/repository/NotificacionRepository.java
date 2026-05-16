package GameShelf.ms_notificacion.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import GameShelf.ms_notificacion.model.NotificacionModel;

@Repository
public interface NotificacionRepository extends JpaRepository<NotificacionModel, Long> {

    List<NotificacionModel> findByUsuarioId(Long usuarioId);

    List<NotificacionModel> findByUsuarioIdAndEstado(Long usuarioId, String estado);

    List<NotificacionModel> findByEstado(String estado);
}
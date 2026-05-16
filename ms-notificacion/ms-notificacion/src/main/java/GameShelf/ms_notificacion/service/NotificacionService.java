package GameShelf.ms_notificacion.service;

import GameShelf.ms_notificacion.dto.NotificacionRequestDTO;
import GameShelf.ms_notificacion.dto.NotificacionResponseDTO;

import java.util.List;

public interface NotificacionService {

    List<NotificacionResponseDTO> listarNotificaciones();

    NotificacionResponseDTO obtenerNotificacionPorId(Long id);

    List<NotificacionResponseDTO> listarPorUsuario(Long usuarioId);

    List<NotificacionResponseDTO> listarPendientesPorUsuario(Long usuarioId);

    NotificacionResponseDTO crearNotificacion(NotificacionRequestDTO notificacionRequestDTO);

    NotificacionResponseDTO actualizarNotificacion(Long id, NotificacionRequestDTO notificacionRequestDTO);

    NotificacionResponseDTO marcarComoLeida(Long id);

    void eliminarNotificacion(Long id);
}
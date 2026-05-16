package GameShelf.ms_notificacion.service;

import java.util.List;

import GameShelf.ms_notificacion.dto.NotificacionRequestDTO;
import GameShelf.ms_notificacion.dto.NotificacionResponseDTO;

public interface NotificacionService {

    List<NotificacionResponseDTO> listarNotificaciones();

    NotificacionResponseDTO obtenerNotificacionPorId(Long id);

    List<NotificacionResponseDTO> listarPorUsuario(Long usuarioId);

    List<NotificacionResponseDTO> listarPendientesPorUsuario(Long usuarioId);

    List<NotificacionResponseDTO> listarPorEstado(String estado);

    NotificacionResponseDTO crearNotificacion(NotificacionRequestDTO notificacionRequestDTO);

    NotificacionResponseDTO actualizarNotificacion(Long id, NotificacionRequestDTO notificacionRequestDTO);

    NotificacionResponseDTO marcarComoLeida(Long id);

    void eliminarNotificacion(Long id);
}
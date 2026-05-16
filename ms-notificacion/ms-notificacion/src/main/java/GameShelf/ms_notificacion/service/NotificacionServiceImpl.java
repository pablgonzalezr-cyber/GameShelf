package GameShelf.ms_notificacion.service;

import GameShelf.ms_notificacion.client.UsuarioClient;
import GameShelf.ms_notificacion.dto.NotificacionRequestDTO;
import GameShelf.ms_notificacion.dto.NotificacionResponseDTO;
import GameShelf.ms_notificacion.exception.DatoInvalidoException;
import GameShelf.ms_notificacion.exception.RecursoNoEncontradoException;
import GameShelf.ms_notificacion.model.NotificacionModel;
import GameShelf.ms_notificacion.repository.NotificacionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificacionServiceImpl implements NotificacionService {

    private final NotificacionRepository notificacionRepository;
    private final UsuarioClient usuarioClient;

    @Override
    public List<NotificacionResponseDTO> listarNotificaciones() {

        List<NotificacionModel> notificaciones = notificacionRepository.findAll();
        List<NotificacionResponseDTO> respuesta = new ArrayList<>();

        for (NotificacionModel notificacion : notificaciones) {
            respuesta.add(convertirAResponseDTO(notificacion));
        }

        return respuesta;
    }

    @Override
    public NotificacionResponseDTO obtenerNotificacionPorId(Long id) {

        NotificacionModel notificacion = buscarNotificacion(id);

        return convertirAResponseDTO(notificacion);
    }

    @Override
    public List<NotificacionResponseDTO> listarPorUsuario(Long usuarioId) {

        usuarioClient.obtenerUsuarioPorId(usuarioId);

        List<NotificacionModel> notificaciones = notificacionRepository.findByUsuarioId(usuarioId);
        List<NotificacionResponseDTO> respuesta = new ArrayList<>();

        for (NotificacionModel notificacion : notificaciones) {
            respuesta.add(convertirAResponseDTO(notificacion));
        }

        return respuesta;
    }

    @Override
    public List<NotificacionResponseDTO> listarPendientesPorUsuario(Long usuarioId) {

        usuarioClient.obtenerUsuarioPorId(usuarioId);

        List<NotificacionModel> notificaciones = notificacionRepository.findByUsuarioIdAndEstado(usuarioId, "PENDIENTE");
        List<NotificacionResponseDTO> respuesta = new ArrayList<>();

        for (NotificacionModel notificacion : notificaciones) {
            respuesta.add(convertirAResponseDTO(notificacion));
        }

        return respuesta;
    }

    @Override
    public NotificacionResponseDTO crearNotificacion(NotificacionRequestDTO notificacionRequestDTO) {

        log.info("Creando notificación para usuario ID: {}", notificacionRequestDTO.getUsuarioId());

        usuarioClient.obtenerUsuarioPorId(notificacionRequestDTO.getUsuarioId());

        validarTipo(notificacionRequestDTO.getTipo());

        if (notificacionRequestDTO.getEstado() != null && !notificacionRequestDTO.getEstado().isEmpty()) {
            validarEstado(notificacionRequestDTO.getEstado());
        }

        NotificacionModel notificacion = new NotificacionModel();

        notificacion.setUsuarioId(notificacionRequestDTO.getUsuarioId());
        notificacion.setTitulo(notificacionRequestDTO.getTitulo());
        notificacion.setMensaje(notificacionRequestDTO.getMensaje());
        notificacion.setTipo(notificacionRequestDTO.getTipo().toUpperCase());
        notificacion.setReferenciaId(notificacionRequestDTO.getReferenciaId());
        notificacion.setReferenciaTipo(notificacionRequestDTO.getReferenciaTipo());

        if (notificacionRequestDTO.getEstado() == null || notificacionRequestDTO.getEstado().isEmpty()) {
            notificacion.setEstado("PENDIENTE");
        } else {
            notificacion.setEstado(notificacionRequestDTO.getEstado().toUpperCase());
        }

        NotificacionModel notificacionGuardada = notificacionRepository.save(notificacion);

        return convertirAResponseDTO(notificacionGuardada);
    }

    @Override
    public NotificacionResponseDTO actualizarNotificacion(Long id, NotificacionRequestDTO notificacionRequestDTO) {

        log.info("Actualizando notificación ID: {}", id);

        NotificacionModel notificacion = buscarNotificacion(id);

        usuarioClient.obtenerUsuarioPorId(notificacionRequestDTO.getUsuarioId());

        validarTipo(notificacionRequestDTO.getTipo());

        if (notificacionRequestDTO.getEstado() != null && !notificacionRequestDTO.getEstado().isEmpty()) {
            validarEstado(notificacionRequestDTO.getEstado());
            notificacion.setEstado(notificacionRequestDTO.getEstado().toUpperCase());
        }

        notificacion.setUsuarioId(notificacionRequestDTO.getUsuarioId());
        notificacion.setTitulo(notificacionRequestDTO.getTitulo());
        notificacion.setMensaje(notificacionRequestDTO.getMensaje());
        notificacion.setTipo(notificacionRequestDTO.getTipo().toUpperCase());
        notificacion.setReferenciaId(notificacionRequestDTO.getReferenciaId());
        notificacion.setReferenciaTipo(notificacionRequestDTO.getReferenciaTipo());

        NotificacionModel notificacionActualizada = notificacionRepository.save(notificacion);

        return convertirAResponseDTO(notificacionActualizada);
    }

    @Override
    public NotificacionResponseDTO marcarComoLeida(Long id) {

        log.info("Marcando notificación como leída ID: {}", id);

        NotificacionModel notificacion = buscarNotificacion(id);

        if (notificacion.getEstado().equalsIgnoreCase("ELIMINADA")) {
            throw new DatoInvalidoException("No se puede marcar como leída una notificación eliminada");
        }

        notificacion.setEstado("LEIDA");
        notificacion.setFechaLectura(LocalDateTime.now());

        NotificacionModel notificacionActualizada = notificacionRepository.save(notificacion);

        return convertirAResponseDTO(notificacionActualizada);
    }

    @Override
    public void eliminarNotificacion(Long id) {

        log.info("Eliminando notificación ID: {}", id);

        NotificacionModel notificacion = buscarNotificacion(id);

        if (notificacion.getEstado().equalsIgnoreCase("ELIMINADA")) {
            throw new DatoInvalidoException("La notificación ya está eliminada");
        }

        notificacion.setEstado("ELIMINADA");

        notificacionRepository.save(notificacion);
    }

    private NotificacionModel buscarNotificacion(Long id) {

        return notificacionRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Notificación no encontrada"));
    }

    private void validarEstado(String estado) {

        String estadoMayuscula = estado.toUpperCase();

        if (!estadoMayuscula.equals("PENDIENTE") &&
                !estadoMayuscula.equals("LEIDA") &&
                !estadoMayuscula.equals("ELIMINADA")) {

            throw new DatoInvalidoException("Estado inválido. Debe ser PENDIENTE, LEIDA o ELIMINADA");
        }
    }

    private void validarTipo(String tipo) {

        String tipoMayuscula = tipo.toUpperCase();

        if (!tipoMayuscula.equals("RESERVA") &&
                !tipoMayuscula.equals("PRESTAMO") &&
                !tipoMayuscula.equals("MULTA") &&
                !tipoMayuscula.equals("SISTEMA")) {

            throw new DatoInvalidoException("Tipo inválido. Debe ser RESERVA, PRESTAMO, MULTA o SISTEMA");
        }
    }

    private NotificacionResponseDTO convertirAResponseDTO(NotificacionModel notificacion) {

        return new NotificacionResponseDTO(
                notificacion.getId(),
                notificacion.getUsuarioId(),
                notificacion.getTitulo(),
                notificacion.getMensaje(),
                notificacion.getTipo(),
                notificacion.getEstado(),
                notificacion.getFechaCreacion(),
                notificacion.getFechaLectura(),
                notificacion.getReferenciaId(),
                notificacion.getReferenciaTipo()
        );
    }
}

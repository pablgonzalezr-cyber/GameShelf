package GameShelf.ms_notificacion.service;

import GameShelf.ms_notificacion.client.UsuarioClient;
import GameShelf.ms_notificacion.dto.NotificacionRequestDTO;
import GameShelf.ms_notificacion.dto.NotificacionResponseDTO;
import GameShelf.ms_notificacion.dto.UsuarioResponseDTO;
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

        validarUsuario(usuarioId);

        List<NotificacionModel> notificaciones = notificacionRepository.findByUsuarioId(usuarioId);
        List<NotificacionResponseDTO> respuesta = new ArrayList<>();

        for (NotificacionModel notificacion : notificaciones) {
            respuesta.add(convertirAResponseDTO(notificacion));
        }

        return respuesta;
    }

    @Override
    public List<NotificacionResponseDTO> listarPendientesPorUsuario(Long usuarioId) {

        validarUsuario(usuarioId);

        List<NotificacionModel> notificaciones = notificacionRepository.findByUsuarioIdAndEstado(usuarioId, "PENDIENTE");
        List<NotificacionResponseDTO> respuesta = new ArrayList<>();

        for (NotificacionModel notificacion : notificaciones) {
            respuesta.add(convertirAResponseDTO(notificacion));
        }

        return respuesta;
    }

    @Override
    public List<NotificacionResponseDTO> listarPorEstado(String estado) {

        String estadoLimpio = validarEstado(estado);

        List<NotificacionModel> notificaciones = notificacionRepository.findByEstado(estadoLimpio);
        List<NotificacionResponseDTO> respuesta = new ArrayList<>();

        for (NotificacionModel notificacion : notificaciones) {
            respuesta.add(convertirAResponseDTO(notificacion));
        }

        return respuesta;
    }

    @Override
    public NotificacionResponseDTO crearNotificacion(NotificacionRequestDTO notificacionRequestDTO) {

        log.info("Creando notificación para usuario ID: {}", notificacionRequestDTO.getUsuarioId());

        validarUsuario(notificacionRequestDTO.getUsuarioId());

        String tipoLimpio = validarTipo(notificacionRequestDTO.getTipo());

        String estadoLimpio = "PENDIENTE";

        if (notificacionRequestDTO.getEstado() != null && !notificacionRequestDTO.getEstado().trim().isEmpty()) {
            estadoLimpio = validarEstado(notificacionRequestDTO.getEstado());
        }

        NotificacionModel notificacion = new NotificacionModel();

        notificacion.setUsuarioId(notificacionRequestDTO.getUsuarioId());
        notificacion.setTitulo(notificacionRequestDTO.getTitulo());
        notificacion.setMensaje(notificacionRequestDTO.getMensaje());
        notificacion.setTipo(tipoLimpio);
        notificacion.setEstado(estadoLimpio);
        notificacion.setReferenciaId(notificacionRequestDTO.getReferenciaId());
        notificacion.setReferenciaTipo(notificacionRequestDTO.getReferenciaTipo());

        if (estadoLimpio.equals("LEIDA")) {
            notificacion.setFechaLectura(LocalDateTime.now());
        }

        NotificacionModel notificacionGuardada = notificacionRepository.save(notificacion);

        log.info("Notificación creada correctamente con ID: {}", notificacionGuardada.getId());

        return convertirAResponseDTO(notificacionGuardada);
    }

    @Override
    public NotificacionResponseDTO actualizarNotificacion(Long id, NotificacionRequestDTO notificacionRequestDTO) {

        log.info("Actualizando notificación ID: {}", id);

        NotificacionModel notificacion = buscarNotificacion(id);

        validarUsuario(notificacionRequestDTO.getUsuarioId());

        String tipoLimpio = validarTipo(notificacionRequestDTO.getTipo());

        notificacion.setUsuarioId(notificacionRequestDTO.getUsuarioId());
        notificacion.setTitulo(notificacionRequestDTO.getTitulo());
        notificacion.setMensaje(notificacionRequestDTO.getMensaje());
        notificacion.setTipo(tipoLimpio);
        notificacion.setReferenciaId(notificacionRequestDTO.getReferenciaId());
        notificacion.setReferenciaTipo(notificacionRequestDTO.getReferenciaTipo());

        if (notificacionRequestDTO.getEstado() != null && !notificacionRequestDTO.getEstado().trim().isEmpty()) {

            String estadoLimpio = validarEstado(notificacionRequestDTO.getEstado());

            notificacion.setEstado(estadoLimpio);

            if (estadoLimpio.equals("LEIDA") && notificacion.getFechaLectura() == null) {
                notificacion.setFechaLectura(LocalDateTime.now());
            }

            if (estadoLimpio.equals("PENDIENTE")) {
                notificacion.setFechaLectura(null);
            }
        }

        NotificacionModel notificacionActualizada = notificacionRepository.save(notificacion);

        log.info("Notificación actualizada correctamente con ID: {}", notificacionActualizada.getId());

        return convertirAResponseDTO(notificacionActualizada);
    }

    @Override
    public NotificacionResponseDTO marcarComoLeida(Long id) {

        log.info("Marcando notificación como leída ID: {}", id);

        NotificacionModel notificacion = buscarNotificacion(id);

        if (notificacion.getEstado().equalsIgnoreCase("ELIMINADA")) {
            throw new DatoInvalidoException("No se puede marcar como leída una notificación eliminada");
        }

        if (notificacion.getEstado().equalsIgnoreCase("LEIDA")) {
            throw new DatoInvalidoException("La notificación ya está leída");
        }

        notificacion.setEstado("LEIDA");
        notificacion.setFechaLectura(LocalDateTime.now());

        NotificacionModel notificacionActualizada = notificacionRepository.save(notificacion);

        log.info("Notificación marcada como leída correctamente con ID: {}", id);

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

        log.info("Notificación eliminada lógicamente con ID: {}", id);
    }

    private NotificacionModel buscarNotificacion(Long id) {

        return notificacionRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Notificación no encontrada"));
    }

    private void validarUsuario(Long usuarioId) {

        if (usuarioId == null) {
            throw new DatoInvalidoException("El usuario es obligatorio");
        }

        UsuarioResponseDTO usuario = usuarioClient.obtenerUsuarioPorId(usuarioId);

        if (usuario == null) {
            throw new DatoInvalidoException("El usuario no existe");
        }

        if (usuario.getEstado() != null && !usuario.getEstado().equalsIgnoreCase("ACTIVO")) {
            throw new DatoInvalidoException("El usuario no está activo");
        }
    }

    private String validarEstado(String estado) {

        if (estado == null || estado.trim().isEmpty()) {
            throw new DatoInvalidoException("El estado es obligatorio");
        }

        String estadoLimpio = estado.trim().toUpperCase();

        if (!estadoLimpio.equals("PENDIENTE")
                && !estadoLimpio.equals("LEIDA")
                && !estadoLimpio.equals("ELIMINADA")) {

            throw new DatoInvalidoException("Estado inválido. Debe ser PENDIENTE, LEIDA o ELIMINADA");
        }

        return estadoLimpio;
    }

    private String validarTipo(String tipo) {

        if (tipo == null || tipo.trim().isEmpty()) {
            throw new DatoInvalidoException("El tipo es obligatorio");
        }

        String tipoLimpio = tipo.trim().toUpperCase();

        if (!tipoLimpio.equals("RESERVA")
                && !tipoLimpio.equals("PRESTAMO")
                && !tipoLimpio.equals("MULTA")
                && !tipoLimpio.equals("SISTEMA")) {

            throw new DatoInvalidoException("Tipo inválido. Debe ser RESERVA, PRESTAMO, MULTA o SISTEMA");
        }

        return tipoLimpio;
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
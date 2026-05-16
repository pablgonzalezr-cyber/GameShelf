package GameShelf.ms_notificacion.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import GameShelf.ms_notificacion.dto.NotificacionRequestDTO;
import GameShelf.ms_notificacion.dto.NotificacionResponseDTO;
import GameShelf.ms_notificacion.service.NotificacionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/notificaciones")
@RequiredArgsConstructor
public class NotificacionController {

    private final NotificacionService notificacionService;

    @GetMapping
    public ResponseEntity<List<NotificacionResponseDTO>> listarNotificaciones() {

        log.info("Petición GET para listar notificaciones");

        return ResponseEntity.ok(notificacionService.listarNotificaciones());
    }

    @GetMapping("/{id}")
    public ResponseEntity<NotificacionResponseDTO> obtenerNotificacionPorId(@PathVariable Long id) {

        log.info("Petición GET para buscar notificación ID: {}", id);

        return ResponseEntity.ok(notificacionService.obtenerNotificacionPorId(id));
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<NotificacionResponseDTO>> listarPorUsuario(@PathVariable Long usuarioId) {

        log.info("Petición GET para listar notificaciones por usuario ID: {}", usuarioId);

        return ResponseEntity.ok(notificacionService.listarPorUsuario(usuarioId));
    }

    @GetMapping("/usuario/{usuarioId}/pendientes")
    public ResponseEntity<List<NotificacionResponseDTO>> listarPendientesPorUsuario(@PathVariable Long usuarioId) {

        log.info("Petición GET para listar notificaciones pendientes por usuario ID: {}", usuarioId);

        return ResponseEntity.ok(notificacionService.listarPendientesPorUsuario(usuarioId));
    }

    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<NotificacionResponseDTO>> listarPorEstado(@PathVariable String estado) {

        log.info("Petición GET para listar notificaciones por estado: {}", estado);

        return ResponseEntity.ok(notificacionService.listarPorEstado(estado));
    }

    @PostMapping
    public ResponseEntity<NotificacionResponseDTO> crearNotificacion(
            @Valid @RequestBody NotificacionRequestDTO notificacionRequestDTO) {

        log.info("Petición POST para crear notificación");

        NotificacionResponseDTO notificacion = notificacionService.crearNotificacion(notificacionRequestDTO);

        return ResponseEntity.status(HttpStatus.CREATED).body(notificacion);
    }

    @PutMapping("/{id}")
    public ResponseEntity<NotificacionResponseDTO> actualizarNotificacion(
            @PathVariable Long id,
            @Valid @RequestBody NotificacionRequestDTO notificacionRequestDTO) {

        log.info("Petición PUT para actualizar notificación ID: {}", id);

        return ResponseEntity.ok(notificacionService.actualizarNotificacion(id, notificacionRequestDTO));
    }

    @PutMapping("/{id}/leer")
    public ResponseEntity<NotificacionResponseDTO> marcarComoLeida(@PathVariable Long id) {

        log.info("Petición PUT para marcar notificación como leída ID: {}", id);

        return ResponseEntity.ok(notificacionService.marcarComoLeida(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarNotificacion(@PathVariable Long id) {

        log.info("Petición DELETE para eliminar notificación ID: {}", id);

        notificacionService.eliminarNotificacion(id);

        return ResponseEntity.noContent().build();
    }
}
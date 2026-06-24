package GameShelf.ms_notificacion.controller;

import GameShelf.ms_notificacion.dto.NotificacionRequestDTO;
import GameShelf.ms_notificacion.dto.NotificacionResponseDTO;
import GameShelf.ms_notificacion.service.NotificacionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Tag(
        name = "Notificaciones",
        description = "Endpoints para crear, listar, actualizar, leer y eliminar lógicamente notificaciones en GameShelf"
)
@RestController
@RequestMapping("/api/notificaciones")
@RequiredArgsConstructor
public class NotificacionController {

    private final NotificacionService notificacionService;

    @Operation(
            summary = "Listar notificaciones",
            description = "Obtiene todas las notificaciones registradas en el sistema."
    )
    @GetMapping
    public ResponseEntity<List<NotificacionResponseDTO>> listarNotificaciones() {

        log.info("Petición GET para listar notificaciones");

        return ResponseEntity.ok(notificacionService.listarNotificaciones());
    }

    @Operation(
            summary = "Buscar notificación por ID",
            description = "Obtiene una notificación específica mediante su identificador."
    )
    @GetMapping("/{id}")
    public ResponseEntity<NotificacionResponseDTO> obtenerNotificacionPorId(
            @Parameter(description = "ID de la notificación", example = "1")
            @PathVariable Long id) {

        log.info("Petición GET para buscar notificación ID: {}", id);

        return ResponseEntity.ok(notificacionService.obtenerNotificacionPorId(id));
    }

    @Operation(
            summary = "Listar notificaciones por usuario",
            description = "Obtiene todas las notificaciones asociadas a un usuario específico."
    )
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<NotificacionResponseDTO>> listarPorUsuario(
            @Parameter(description = "ID del usuario", example = "1")
            @PathVariable Long usuarioId) {

        log.info("Petición GET para listar notificaciones por usuario ID: {}", usuarioId);

        return ResponseEntity.ok(notificacionService.listarPorUsuario(usuarioId));
    }

    @Operation(
            summary = "Listar notificaciones pendientes por usuario",
            description = "Obtiene las notificaciones en estado PENDIENTE asociadas a un usuario específico."
    )
    @GetMapping("/usuario/{usuarioId}/pendientes")
    public ResponseEntity<List<NotificacionResponseDTO>> listarPendientesPorUsuario(
            @Parameter(description = "ID del usuario", example = "1")
            @PathVariable Long usuarioId) {

        log.info("Petición GET para listar notificaciones pendientes por usuario ID: {}", usuarioId);

        return ResponseEntity.ok(notificacionService.listarPendientesPorUsuario(usuarioId));
    }

    @Operation(
            summary = "Listar notificaciones por estado",
            description = "Obtiene notificaciones filtradas por estado. Estados permitidos: PENDIENTE, LEIDA o ELIMINADA."
    )
    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<NotificacionResponseDTO>> listarPorEstado(
            @Parameter(description = "Estado de la notificación", example = "PENDIENTE")
            @PathVariable String estado) {

        log.info("Petición GET para listar notificaciones por estado: {}", estado);

        return ResponseEntity.ok(notificacionService.listarPorEstado(estado));
    }

    @Operation(
            summary = "Crear notificación",
            description = "Registra una nueva notificación para un usuario. Valida la existencia del usuario mediante ms-usuario y permite tipos RESERVA, PRESTAMO, MULTA o SISTEMA."
    )
    @PostMapping
    public ResponseEntity<NotificacionResponseDTO> crearNotificacion(
            @Valid @RequestBody NotificacionRequestDTO notificacionRequestDTO) {

        log.info("Petición POST para crear notificación");

        NotificacionResponseDTO notificacion = notificacionService.crearNotificacion(notificacionRequestDTO);

        return ResponseEntity.status(HttpStatus.CREATED).body(notificacion);
    }

    @Operation(
            summary = "Actualizar notificación",
            description = "Actualiza los datos de una notificación existente, incluyendo usuario, título, mensaje, tipo, estado y referencia."
    )
    @PutMapping("/{id}")
    public ResponseEntity<NotificacionResponseDTO> actualizarNotificacion(
            @Parameter(description = "ID de la notificación", example = "1")
            @PathVariable Long id,
            @Valid @RequestBody NotificacionRequestDTO notificacionRequestDTO) {

        log.info("Petición PUT para actualizar notificación ID: {}", id);

        return ResponseEntity.ok(notificacionService.actualizarNotificacion(id, notificacionRequestDTO));
    }

    @Operation(
            summary = "Marcar notificación como leída",
            description = "Cambia el estado de una notificación a LEIDA y registra la fecha de lectura."
    )
    @PutMapping("/{id}/leer")
    public ResponseEntity<NotificacionResponseDTO> marcarComoLeida(
            @Parameter(description = "ID de la notificación", example = "1")
            @PathVariable Long id) {

        log.info("Petición PUT para marcar notificación como leída ID: {}", id);

        return ResponseEntity.ok(notificacionService.marcarComoLeida(id));
    }

    @Operation(
            summary = "Eliminar notificación",
            description = "Realiza un borrado lógico de la notificación, cambiando su estado a ELIMINADA."
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarNotificacion(
            @Parameter(description = "ID de la notificación", example = "1")
            @PathVariable Long id) {

        log.info("Petición DELETE para eliminar notificación ID: {}", id);

        notificacionService.eliminarNotificacion(id);

        return ResponseEntity.noContent().build();
    }
}
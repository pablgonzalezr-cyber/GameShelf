package GameShelf.ms_notificacion.controller;

import java.util.List;
import java.util.Map;

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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

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
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Notificaciones listadas correctamente",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = NotificacionResponseDTO.class)))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Error interno del servidor",
                    content = @Content(schema = @Schema(implementation = Map.class))
            )
    })
    @GetMapping
    public ResponseEntity<List<NotificacionResponseDTO>> listarNotificaciones() {

        log.info("Petición GET para listar notificaciones");

        return ResponseEntity.ok(notificacionService.listarNotificaciones());
    }

    @Operation(
            summary = "Buscar notificación por ID",
            description = "Obtiene una notificación específica mediante su identificador."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Notificación encontrada correctamente",
                    content = @Content(schema = @Schema(implementation = NotificacionResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Notificación no encontrada",
                    content = @Content(schema = @Schema(implementation = Map.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Error interno del servidor",
                    content = @Content(schema = @Schema(implementation = Map.class))
            )
    })
    @GetMapping("/{id}")
    public ResponseEntity<NotificacionResponseDTO> obtenerNotificacionPorId(
            @Parameter(description = "ID de la notificación", example = "1")
            @PathVariable Long id) {

        log.info("Petición GET para buscar notificación ID: {}", id);

        return ResponseEntity.ok(notificacionService.obtenerNotificacionPorId(id));
    }

    @Operation(
            summary = "Listar notificaciones por usuario",
            description = "Obtiene todas las notificaciones asociadas a un usuario específico. Valida la existencia del usuario mediante ms-usuario."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Notificaciones del usuario listadas correctamente",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = NotificacionResponseDTO.class)))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Usuario inválido o usuario inactivo",
                    content = @Content(schema = @Schema(implementation = Map.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Usuario no encontrado en ms-usuario",
                    content = @Content(schema = @Schema(implementation = Map.class))
            ),
            @ApiResponse(
                    responseCode = "503",
                    description = "No se pudo comunicar con ms-usuario",
                    content = @Content(schema = @Schema(implementation = Map.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Error interno del servidor",
                    content = @Content(schema = @Schema(implementation = Map.class))
            )
    })
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<NotificacionResponseDTO>> listarPorUsuario(
            @Parameter(description = "ID del usuario", example = "1")
            @PathVariable Long usuarioId) {

        log.info("Petición GET para listar notificaciones por usuario ID: {}", usuarioId);

        return ResponseEntity.ok(notificacionService.listarPorUsuario(usuarioId));
    }

    @Operation(
            summary = "Listar notificaciones pendientes por usuario",
            description = "Obtiene las notificaciones en estado PENDIENTE asociadas a un usuario específico. Valida la existencia del usuario mediante ms-usuario."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Notificaciones pendientes listadas correctamente",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = NotificacionResponseDTO.class)))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Usuario inválido o usuario inactivo",
                    content = @Content(schema = @Schema(implementation = Map.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Usuario no encontrado en ms-usuario",
                    content = @Content(schema = @Schema(implementation = Map.class))
            ),
            @ApiResponse(
                    responseCode = "503",
                    description = "No se pudo comunicar con ms-usuario",
                    content = @Content(schema = @Schema(implementation = Map.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Error interno del servidor",
                    content = @Content(schema = @Schema(implementation = Map.class))
            )
    })
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
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Notificaciones encontradas correctamente por estado",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = NotificacionResponseDTO.class)))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Estado inválido. Los valores permitidos son PENDIENTE, LEIDA o ELIMINADA",
                    content = @Content(schema = @Schema(implementation = Map.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Error interno del servidor",
                    content = @Content(schema = @Schema(implementation = Map.class))
            )
    })
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
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Notificación creada correctamente",
                    content = @Content(schema = @Schema(implementation = NotificacionResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Datos inválidos, tipo incorrecto, estado incorrecto o usuario inactivo",
                    content = @Content(schema = @Schema(implementation = Map.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Usuario no encontrado en ms-usuario",
                    content = @Content(schema = @Schema(implementation = Map.class))
            ),
            @ApiResponse(
                    responseCode = "503",
                    description = "No se pudo comunicar con ms-usuario",
                    content = @Content(schema = @Schema(implementation = Map.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Error interno del servidor",
                    content = @Content(schema = @Schema(implementation = Map.class))
            )
    })
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
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Notificación actualizada correctamente",
                    content = @Content(schema = @Schema(implementation = NotificacionResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Datos inválidos, tipo incorrecto, estado incorrecto o usuario inactivo",
                    content = @Content(schema = @Schema(implementation = Map.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Notificación no encontrada o usuario no encontrado en ms-usuario",
                    content = @Content(schema = @Schema(implementation = Map.class))
            ),
            @ApiResponse(
                    responseCode = "503",
                    description = "No se pudo comunicar con ms-usuario",
                    content = @Content(schema = @Schema(implementation = Map.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Error interno del servidor",
                    content = @Content(schema = @Schema(implementation = Map.class))
            )
    })
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
            description = "Cambia el estado de una notificación a LEIDA y registra la fecha de lectura. No permite marcar como leída una notificación eliminada o ya leída."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Notificación marcada como leída correctamente",
                    content = @Content(schema = @Schema(implementation = NotificacionResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "La notificación ya está leída o se encuentra eliminada",
                    content = @Content(schema = @Schema(implementation = Map.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Notificación no encontrada",
                    content = @Content(schema = @Schema(implementation = Map.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Error interno del servidor",
                    content = @Content(schema = @Schema(implementation = Map.class))
            )
    })
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
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Notificación eliminada lógicamente correctamente",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "La notificación ya está eliminada",
                    content = @Content(schema = @Schema(implementation = Map.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Notificación no encontrada",
                    content = @Content(schema = @Schema(implementation = Map.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Error interno del servidor",
                    content = @Content(schema = @Schema(implementation = Map.class))
            )
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarNotificacion(
            @Parameter(description = "ID de la notificación", example = "1")
            @PathVariable Long id) {

        log.info("Petición DELETE para eliminar notificación ID: {}", id);

        notificacionService.eliminarNotificacion(id);

        return ResponseEntity.noContent().build();
    }
}
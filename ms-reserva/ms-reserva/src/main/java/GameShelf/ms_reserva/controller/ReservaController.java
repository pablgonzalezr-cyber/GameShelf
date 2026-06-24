package GameShelf.ms_reserva.controller;

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

import GameShelf.ms_reserva.dto.HistorialReservaResponseDTO;
import GameShelf.ms_reserva.dto.ReservaRequestDTO;
import GameShelf.ms_reserva.dto.ReservaResponseDTO;
import GameShelf.ms_reserva.service.ReservaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/reservas")
@Tag(
        name = "Reservas",
        description = "Endpoints para gestionar reservas, confirmaciones, cancelaciones, búsquedas e historial de cambios de reserva."
)
public class ReservaController {

    private final ReservaService reservaService;

    public ReservaController(ReservaService reservaService) {
        this.reservaService = reservaService;
    }

    @Operation(
            summary = "Listar reservas",
            description = "Obtiene todas las reservas registradas en el microservicio."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Lista de reservas obtenida correctamente",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = ReservaResponseDTO.class)))
            ),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping
    public ResponseEntity<List<ReservaResponseDTO>> listarReservas() {

        log.info("Petición GET para listar reservas");

        return ResponseEntity.ok(reservaService.listarReservas());
    }

    @Operation(
            summary = "Buscar reserva por ID",
            description = "Obtiene una reserva específica mediante su identificador único."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Reserva encontrada correctamente",
                    content = @Content(schema = @Schema(implementation = ReservaResponseDTO.class))
            ),
            @ApiResponse(responseCode = "404", description = "Reserva no encontrada"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ReservaResponseDTO> buscarReservaPorId(
            @Parameter(description = "ID de la reserva", example = "1", required = true)
            @PathVariable Long id) {

        log.info("Petición GET para buscar reserva ID: {}", id);

        return ResponseEntity.ok(reservaService.buscarReservaPorId(id));
    }

    @Operation(
            summary = "Buscar reservas por usuario",
            description = "Obtiene todas las reservas asociadas a un usuario específico."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Reservas del usuario obtenidas correctamente",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = ReservaResponseDTO.class)))
            ),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<ReservaResponseDTO>> buscarReservasPorUsuario(
            @Parameter(description = "ID del usuario", example = "1", required = true)
            @PathVariable Long usuarioId) {

        log.info("Petición GET para buscar reservas por usuario ID: {}", usuarioId);

        return ResponseEntity.ok(reservaService.buscarReservasPorUsuario(usuarioId));
    }

    @Operation(
            summary = "Buscar reservas por videojuego",
            description = "Obtiene todas las reservas asociadas a un videojuego específico."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Reservas del videojuego obtenidas correctamente",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = ReservaResponseDTO.class)))
            ),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/videojuego/{videojuegoId}")
    public ResponseEntity<List<ReservaResponseDTO>> buscarReservasPorVideojuego(
            @Parameter(description = "ID del videojuego", example = "2", required = true)
            @PathVariable Long videojuegoId) {

        log.info("Petición GET para buscar reservas por videojuego ID: {}", videojuegoId);

        return ResponseEntity.ok(reservaService.buscarReservasPorVideojuego(videojuegoId));
    }

    @Operation(
            summary = "Buscar reservas por estado",
            description = "Obtiene reservas filtradas por estado. Estados esperados: PENDIENTE, CONFIRMADA o CANCELADA."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Reservas filtradas por estado obtenidas correctamente",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = ReservaResponseDTO.class)))
            ),
            @ApiResponse(responseCode = "400", description = "Estado inválido"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<ReservaResponseDTO>> buscarReservasPorEstado(
            @Parameter(description = "Estado de la reserva", example = "PENDIENTE", required = true)
            @PathVariable String estado) {

        log.info("Petición GET para buscar reservas por estado: {}", estado);

        return ResponseEntity.ok(reservaService.buscarReservasPorEstado(estado));
    }

    @Operation(
            summary = "Crear una reserva",
            description = "Crea una reserva validando usuario, videojuego y stock disponible mediante comunicación con otros microservicios."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Reserva creada correctamente",
                    content = @Content(schema = @Schema(implementation = ReservaResponseDTO.class))
            ),
            @ApiResponse(responseCode = "400", description = "Datos inválidos o videojuego no disponible"),
            @ApiResponse(responseCode = "503", description = "Error de comunicación con ms-usuario, ms-videojuego o ms-stock"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PostMapping
    public ResponseEntity<ReservaResponseDTO> crearReserva(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Datos necesarios para crear una reserva",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = ReservaRequestDTO.class),
                            examples = @ExampleObject(
                                    name = "Ejemplo de reserva",
                                    value = """
                                            {
                                              "usuarioId": 1,
                                              "videojuegoId": 2,
                                              "fechaReserva": "2026-06-24",
                                              "estado": "PENDIENTE"
                                            }
                                            """
                            )
                    )
            )
            @Valid @RequestBody ReservaRequestDTO reservaRequestDTO) {

        log.info("Petición POST para crear reserva");

        ReservaResponseDTO reserva = reservaService.crearReserva(reservaRequestDTO);

        return ResponseEntity.status(HttpStatus.CREATED).body(reserva);
    }

    @Operation(
            summary = "Actualizar una reserva",
            description = "Actualiza los datos de una reserva existente, validando usuario, videojuego, fecha y estado."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Reserva actualizada correctamente",
                    content = @Content(schema = @Schema(implementation = ReservaResponseDTO.class))
            ),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "404", description = "Reserva no encontrada"),
            @ApiResponse(responseCode = "503", description = "Error de comunicación con otro microservicio"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PutMapping("/{id}")
    public ResponseEntity<ReservaResponseDTO> actualizarReserva(
            @Parameter(description = "ID de la reserva a actualizar", example = "1", required = true)
            @PathVariable Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Datos actualizados de la reserva",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = ReservaRequestDTO.class),
                            examples = @ExampleObject(
                                    name = "Ejemplo de actualización",
                                    value = """
                                            {
                                              "usuarioId": 1,
                                              "videojuegoId": 2,
                                              "fechaReserva": "2026-06-25",
                                              "estado": "PENDIENTE"
                                            }
                                            """
                            )
                    )
            )
            @Valid @RequestBody ReservaRequestDTO reservaRequestDTO) {

        log.info("Petición PUT para actualizar reserva ID: {}", id);

        return ResponseEntity.ok(reservaService.actualizarReserva(id, reservaRequestDTO));
    }

    @Operation(
            summary = "Confirmar reserva",
            description = "Cambia el estado de la reserva a CONFIRMADA y registra el cambio en el historial de reserva."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Reserva confirmada correctamente",
                    content = @Content(schema = @Schema(implementation = ReservaResponseDTO.class))
            ),
            @ApiResponse(responseCode = "400", description = "La reserva no puede confirmarse por su estado actual"),
            @ApiResponse(responseCode = "404", description = "Reserva no encontrada"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PutMapping("/confirmar/{id}")
    public ResponseEntity<ReservaResponseDTO> confirmarReserva(
            @Parameter(description = "ID de la reserva a confirmar", example = "1", required = true)
            @PathVariable Long id) {

        log.info("Petición PUT para confirmar reserva ID: {}", id);

        return ResponseEntity.ok(reservaService.confirmarReserva(id));
    }

    @Operation(
            summary = "Cancelar reserva",
            description = "Cambia el estado de la reserva a CANCELADA y registra el cambio en el historial de reserva."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Reserva cancelada correctamente",
                    content = @Content(schema = @Schema(implementation = ReservaResponseDTO.class))
            ),
            @ApiResponse(responseCode = "400", description = "La reserva no puede cancelarse por su estado actual"),
            @ApiResponse(responseCode = "404", description = "Reserva no encontrada"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PutMapping("/cancelar/{id}")
    public ResponseEntity<ReservaResponseDTO> cancelarReserva(
            @Parameter(description = "ID de la reserva a cancelar", example = "1", required = true)
            @PathVariable Long id) {

        log.info("Petición PUT para cancelar reserva ID: {}", id);

        return ResponseEntity.ok(reservaService.cancelarReserva(id));
    }

    @Operation(
            summary = "Listar historial de una reserva",
            description = "Obtiene el historial de cambios de estado de una reserva específica. Este historial se genera mediante una relación JPA interna entre ReservaModel e HistorialReservaModel."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Historial de reserva obtenido correctamente",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = HistorialReservaResponseDTO.class)))
            ),
            @ApiResponse(responseCode = "404", description = "Reserva no encontrada"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/{id}/historial")
    public ResponseEntity<List<HistorialReservaResponseDTO>> listarHistorialPorReserva(
            @Parameter(description = "ID de la reserva", example = "1", required = true)
            @PathVariable Long id) {

        log.info("Petición GET para listar historial de reserva ID: {}", id);

        return ResponseEntity.ok(reservaService.listarHistorialPorReserva(id));
    }

    @Operation(
            summary = "Eliminar reserva",
            description = "Elimina o cancela una reserva según la lógica del servicio. En la respuesta exitosa no retorna contenido."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Reserva eliminada correctamente"),
            @ApiResponse(responseCode = "404", description = "Reserva no encontrada"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarReserva(
            @Parameter(description = "ID de la reserva a eliminar", example = "1", required = true)
            @PathVariable Long id) {

        log.info("Petición DELETE para eliminar/cancelar reserva ID: {}", id);

        reservaService.eliminarReserva(id);

        return ResponseEntity.noContent().build();
    }
}
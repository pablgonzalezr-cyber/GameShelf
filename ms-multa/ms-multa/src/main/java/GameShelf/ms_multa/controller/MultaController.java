package GameShelf.ms_multa.controller;

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

import GameShelf.ms_multa.dto.MultaRequestDTO;
import GameShelf.ms_multa.dto.MultaResponseDTO;
import GameShelf.ms_multa.dto.PagoMultaRequestDTO;
import GameShelf.ms_multa.dto.PagoMultaResponseDTO;
import GameShelf.ms_multa.service.MultaService;
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
@RequestMapping("/api/multas")
@Tag(
        name = "Multas",
        description = "Endpoints para gestionar multas, pagos asociados, anulación, búsqueda y eliminación de multas."
)
public class MultaController {

    private final MultaService multaService;

    public MultaController(MultaService multaService) {
        this.multaService = multaService;
    }

    @Operation(
            summary = "Crear una multa",
            description = "Crea una multa asociada a un usuario y a un préstamo. Valida que el usuario exista, que el préstamo exista y que el préstamo pertenezca al usuario indicado."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Multa creada correctamente",
                    content = @Content(schema = @Schema(implementation = MultaResponseDTO.class))
            ),
            @ApiResponse(responseCode = "400", description = "Datos inválidos, préstamo no pertenece al usuario o ya existe una multa pendiente"),
            @ApiResponse(responseCode = "503", description = "Error de comunicación con ms-usuario o ms-prestamo"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PostMapping
    public ResponseEntity<MultaResponseDTO> crearMulta(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Datos necesarios para crear una multa",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = MultaRequestDTO.class),
                            examples = @ExampleObject(
                                    name = "Ejemplo de multa",
                                    value = """
                                            {
                                              "usuarioId": 1,
                                              "prestamoId": 1,
                                              "monto": 5000,
                                              "motivo": "Atraso en la devolución del videojuego",
                                              "estado": "PENDIENTE"
                                            }
                                            """
                            )
                    )
            )
            @Valid @RequestBody MultaRequestDTO multaRequestDTO) {

        log.info("Petición POST para crear multa");

        MultaResponseDTO multa = multaService.crearMulta(multaRequestDTO);

        return ResponseEntity.status(HttpStatus.CREATED).body(multa);
    }

    @Operation(
            summary = "Listar multas",
            description = "Obtiene todas las multas registradas en el microservicio."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Lista de multas obtenida correctamente",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = MultaResponseDTO.class)))
            ),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping
    public ResponseEntity<List<MultaResponseDTO>> listarMultas() {

        log.info("Petición GET para listar multas");

        return ResponseEntity.ok(multaService.listarMultas());
    }

    @Operation(
            summary = "Buscar multa por ID",
            description = "Obtiene una multa específica mediante su identificador único."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Multa encontrada correctamente",
                    content = @Content(schema = @Schema(implementation = MultaResponseDTO.class))
            ),
            @ApiResponse(responseCode = "404", description = "Multa no encontrada"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/{id}")
    public ResponseEntity<MultaResponseDTO> buscarPorId(
            @Parameter(description = "ID de la multa", example = "1", required = true)
            @PathVariable Long id) {

        log.info("Petición GET para buscar multa ID: {}", id);

        return ResponseEntity.ok(multaService.buscarPorId(id));
    }

    @Operation(
            summary = "Buscar multas por usuario",
            description = "Obtiene todas las multas asociadas a un usuario específico."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Multas del usuario obtenidas correctamente",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = MultaResponseDTO.class)))
            ),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<MultaResponseDTO>> buscarPorUsuario(
            @Parameter(description = "ID del usuario", example = "1", required = true)
            @PathVariable Long usuarioId) {

        log.info("Petición GET para buscar multas por usuario ID: {}", usuarioId);

        return ResponseEntity.ok(multaService.buscarPorUsuario(usuarioId));
    }

    @Operation(
            summary = "Buscar multas por préstamo",
            description = "Obtiene todas las multas asociadas a un préstamo específico."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Multas del préstamo obtenidas correctamente",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = MultaResponseDTO.class)))
            ),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/prestamo/{prestamoId}")
    public ResponseEntity<List<MultaResponseDTO>> buscarPorPrestamo(
            @Parameter(description = "ID del préstamo", example = "1", required = true)
            @PathVariable Long prestamoId) {

        log.info("Petición GET para buscar multas por préstamo ID: {}", prestamoId);

        return ResponseEntity.ok(multaService.buscarPorPrestamo(prestamoId));
    }

    @Operation(
            summary = "Buscar multas por estado",
            description = "Obtiene multas filtradas por estado. Estados válidos: PENDIENTE, PAGADA o ANULADA."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Multas filtradas por estado obtenidas correctamente",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = MultaResponseDTO.class)))
            ),
            @ApiResponse(responseCode = "400", description = "Estado inválido"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<MultaResponseDTO>> buscarPorEstado(
            @Parameter(description = "Estado de la multa", example = "PENDIENTE", required = true)
            @PathVariable String estado) {

        log.info("Petición GET para buscar multas por estado: {}", estado);

        return ResponseEntity.ok(multaService.buscarPorEstado(estado));
    }

    @Operation(
            summary = "Actualizar una multa",
            description = "Actualiza los datos de una multa existente, validando usuario, préstamo, monto, motivo y estado."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Multa actualizada correctamente",
                    content = @Content(schema = @Schema(implementation = MultaResponseDTO.class))
            ),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "404", description = "Multa no encontrada"),
            @ApiResponse(responseCode = "503", description = "Error de comunicación con otro microservicio"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PutMapping("/{id}")
    public ResponseEntity<MultaResponseDTO> actualizarMulta(
            @Parameter(description = "ID de la multa a actualizar", example = "1", required = true)
            @PathVariable Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Datos actualizados de la multa",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = MultaRequestDTO.class),
                            examples = @ExampleObject(
                                    name = "Ejemplo de actualización",
                                    value = """
                                            {
                                              "usuarioId": 1,
                                              "prestamoId": 1,
                                              "monto": 7000,
                                              "motivo": "Atraso extendido en la devolución",
                                              "estado": "PENDIENTE"
                                            }
                                            """
                            )
                    )
            )
            @Valid @RequestBody MultaRequestDTO multaRequestDTO) {

        log.info("Petición PUT para actualizar multa ID: {}", id);

        return ResponseEntity.ok(multaService.actualizarMulta(id, multaRequestDTO));
    }

    @Operation(
            summary = "Marcar multa como pagada",
            description = "Cambia el estado de una multa pendiente a PAGADA. Este endpoint no registra detalle de pago; para registrar pago con relación JPA se recomienda usar POST /api/multas/{id}/pagos."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Multa marcada como pagada correctamente",
                    content = @Content(schema = @Schema(implementation = MultaResponseDTO.class))
            ),
            @ApiResponse(responseCode = "400", description = "Solo se pueden pagar multas pendientes"),
            @ApiResponse(responseCode = "404", description = "Multa no encontrada"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PutMapping("/pagar/{id}")
    public ResponseEntity<MultaResponseDTO> pagarMulta(
            @Parameter(description = "ID de la multa a marcar como pagada", example = "1", required = true)
            @PathVariable Long id) {

        log.info("Petición PUT para pagar multa ID: {}", id);

        return ResponseEntity.ok(multaService.pagarMulta(id));
    }

    @Operation(
            summary = "Anular una multa",
            description = "Cambia el estado de una multa a ANULADA. No se puede anular una multa que ya está pagada."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Multa anulada correctamente",
                    content = @Content(schema = @Schema(implementation = MultaResponseDTO.class))
            ),
            @ApiResponse(responseCode = "400", description = "No se puede anular una multa pagada"),
            @ApiResponse(responseCode = "404", description = "Multa no encontrada"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PutMapping("/anular/{id}")
    public ResponseEntity<MultaResponseDTO> anularMulta(
            @Parameter(description = "ID de la multa a anular", example = "1", required = true)
            @PathVariable Long id) {

        log.info("Petición PUT para anular multa ID: {}", id);

        return ResponseEntity.ok(multaService.anularMulta(id));
    }

    @Operation(
            summary = "Registrar pago de multa",
            description = "Registra un pago asociado a una multa mediante relación JPA interna. El monto pagado debe ser exactamente igual al monto de la multa para cambiarla a estado PAGADA."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Pago registrado correctamente",
                    content = @Content(schema = @Schema(implementation = PagoMultaResponseDTO.class))
            ),
            @ApiResponse(responseCode = "400", description = "Monto incorrecto, multa anulada o multa ya pagada"),
            @ApiResponse(responseCode = "404", description = "Multa no encontrada"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PostMapping("/{id}/pagos")
    public ResponseEntity<PagoMultaResponseDTO> registrarPago(
            @Parameter(description = "ID de la multa que será pagada", example = "1", required = true)
            @PathVariable Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Datos del pago de la multa",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = PagoMultaRequestDTO.class),
                            examples = @ExampleObject(
                                    name = "Ejemplo de pago",
                                    value = """
                                            {
                                              "montoPagado": 5000,
                                              "metodoPago": "EFECTIVO"
                                            }
                                            """
                            )
                    )
            )
            @Valid @RequestBody PagoMultaRequestDTO pagoRequestDTO) {

        log.info("Petición POST para registrar pago de multa ID: {}", id);

        PagoMultaResponseDTO pago = multaService.registrarPago(id, pagoRequestDTO);

        return ResponseEntity.status(HttpStatus.CREATED).body(pago);
    }

    @Operation(
            summary = "Listar pagos de una multa",
            description = "Obtiene todos los pagos asociados a una multa específica."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Pagos de la multa obtenidos correctamente",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = PagoMultaResponseDTO.class)))
            ),
            @ApiResponse(responseCode = "404", description = "Multa no encontrada"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/{id}/pagos")
    public ResponseEntity<List<PagoMultaResponseDTO>> listarPagosPorMulta(
            @Parameter(description = "ID de la multa", example = "1", required = true)
            @PathVariable Long id) {

        log.info("Petición GET para listar pagos de multa ID: {}", id);

        return ResponseEntity.ok(multaService.listarPagosPorMulta(id));
    }

    @Operation(
            summary = "Eliminar multa",
            description = "Elimina una multa del sistema. En la implementación actual corresponde a eliminación física del registro."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Multa eliminada correctamente"),
            @ApiResponse(responseCode = "404", description = "Multa no encontrada"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminarMulta(
            @Parameter(description = "ID de la multa a eliminar", example = "1", required = true)
            @PathVariable Long id) {

        log.info("Petición DELETE para eliminar multa ID: {}", id);

        multaService.eliminarMulta(id);

        return ResponseEntity.ok("Multa eliminada correctamente");
    }
}
package GameShelf.ms_prestamo.controller;

import java.util.HashMap;
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

import GameShelf.ms_prestamo.dto.PrestamoRequestDTO;
import GameShelf.ms_prestamo.dto.PrestamoResponseDTO;
import GameShelf.ms_prestamo.dto.RenovacionPrestamoRequestDTO;
import GameShelf.ms_prestamo.dto.RenovacionPrestamoResponseDTO;
import GameShelf.ms_prestamo.service.PrestamoService;
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
@RequestMapping("/api/prestamos")
@Tag(
        name = "Préstamos",
        description = "Endpoints para gestionar préstamos, devoluciones, cancelaciones y renovaciones de videojuegos."
)
public class PrestamoController {

    private final PrestamoService prestamoService;

    public PrestamoController(PrestamoService prestamoService) {
        this.prestamoService = prestamoService;
    }

    @Operation(
            summary = "Crear préstamo",
            description = "Crea un préstamo validando usuario, videojuego y stock disponible mediante comunicación con ms-usuario, ms-videojuego y ms-stock. Al crear el préstamo, reduce el stock disponible del videojuego."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Préstamo creado correctamente",
                    content = @Content(schema = @Schema(implementation = PrestamoResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Datos inválidos, usuario inexistente, usuario inactivo, videojuego no disponible, stock insuficiente o préstamo duplicado",
                    content = @Content(schema = @Schema(implementation = Map.class))
            ),
            @ApiResponse(
                    responseCode = "503",
                    description = "Error de comunicación con ms-usuario, ms-videojuego o ms-stock",
                    content = @Content(schema = @Schema(implementation = Map.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Error interno del servidor",
                    content = @Content(schema = @Schema(implementation = Map.class))
            )
    })
    @PostMapping
    public ResponseEntity<PrestamoResponseDTO> crearPrestamo(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Datos necesarios para crear un préstamo",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = PrestamoRequestDTO.class),
                            examples = @ExampleObject(
                                    name = "Ejemplo de préstamo",
                                    value = """
                                            {
                                              "usuarioId": 1,
                                              "videojuegoId": 2,
                                              "fechaDevolucion": "2026-07-01"
                                            }
                                            """
                            )
                    )
            )
            @Valid @RequestBody PrestamoRequestDTO prestamoRequestDTO) {

        log.info("Petición POST para crear préstamo");

        PrestamoResponseDTO prestamo = prestamoService.crearPrestamo(prestamoRequestDTO);

        return ResponseEntity.status(HttpStatus.CREATED).body(prestamo);
    }

    @Operation(
            summary = "Listar préstamos",
            description = "Obtiene todos los préstamos registrados en el microservicio."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Lista de préstamos obtenida correctamente",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = PrestamoResponseDTO.class)))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Error interno del servidor",
                    content = @Content(schema = @Schema(implementation = Map.class))
            )
    })
    @GetMapping
    public ResponseEntity<List<PrestamoResponseDTO>> listarPrestamos() {

        log.info("Petición GET para listar préstamos");

        return ResponseEntity.ok(prestamoService.listarPrestamos());
    }

    @Operation(
            summary = "Buscar préstamo por ID",
            description = "Obtiene un préstamo específico mediante su identificador único."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Préstamo encontrado correctamente",
                    content = @Content(schema = @Schema(implementation = PrestamoResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Préstamo no encontrado",
                    content = @Content(schema = @Schema(implementation = Map.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Error interno del servidor",
                    content = @Content(schema = @Schema(implementation = Map.class))
            )
    })
    @GetMapping("/{id}")
    public ResponseEntity<PrestamoResponseDTO> buscarPorId(
            @Parameter(description = "ID del préstamo", example = "1", required = true)
            @PathVariable Long id) {

        log.info("Petición GET para buscar préstamo ID: {}", id);

        return ResponseEntity.ok(prestamoService.buscarPorId(id));
    }

    @Operation(
            summary = "Buscar préstamos por usuario",
            description = "Obtiene todos los préstamos asociados a un usuario específico."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Préstamos del usuario obtenidos correctamente",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = PrestamoResponseDTO.class)))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Error interno del servidor",
                    content = @Content(schema = @Schema(implementation = Map.class))
            )
    })
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<PrestamoResponseDTO>> buscarPorUsuario(
            @Parameter(description = "ID del usuario", example = "1", required = true)
            @PathVariable Long usuarioId) {

        log.info("Petición GET para buscar préstamos por usuario ID: {}", usuarioId);

        return ResponseEntity.ok(prestamoService.buscarPorUsuario(usuarioId));
    }

    @Operation(
            summary = "Buscar préstamos por videojuego",
            description = "Obtiene todos los préstamos asociados a un videojuego específico."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Préstamos del videojuego obtenidos correctamente",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = PrestamoResponseDTO.class)))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Error interno del servidor",
                    content = @Content(schema = @Schema(implementation = Map.class))
            )
    })
    @GetMapping("/videojuego/{videojuegoId}")
    public ResponseEntity<List<PrestamoResponseDTO>> buscarPorVideojuego(
            @Parameter(description = "ID del videojuego", example = "2", required = true)
            @PathVariable Long videojuegoId) {

        log.info("Petición GET para buscar préstamos por videojuego ID: {}", videojuegoId);

        return ResponseEntity.ok(prestamoService.buscarPorVideojuego(videojuegoId));
    }

    @Operation(
            summary = "Buscar préstamos por estado",
            description = "Obtiene préstamos filtrados por estado. Estados válidos: PRESTADO, DEVUELTO o CANCELADO."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Préstamos filtrados por estado obtenidos correctamente",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = PrestamoResponseDTO.class)))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Estado inválido. Los valores permitidos son PRESTADO, DEVUELTO o CANCELADO",
                    content = @Content(schema = @Schema(implementation = Map.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Error interno del servidor",
                    content = @Content(schema = @Schema(implementation = Map.class))
            )
    })
    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<PrestamoResponseDTO>> buscarPorEstado(
            @Parameter(description = "Estado del préstamo", example = "PRESTADO", required = true)
            @PathVariable String estado) {

        log.info("Petición GET para buscar préstamos por estado: {}", estado);

        return ResponseEntity.ok(prestamoService.buscarPorEstado(estado));
    }

    @Operation(
            summary = "Devolver préstamo",
            description = "Marca un préstamo como DEVUELTO y aumenta el stock del videojuego asociado mediante comunicación con ms-stock."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Préstamo devuelto correctamente",
                    content = @Content(schema = @Schema(implementation = PrestamoResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "El préstamo no está activo o no puede devolverse",
                    content = @Content(schema = @Schema(implementation = Map.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Préstamo no encontrado",
                    content = @Content(schema = @Schema(implementation = Map.class))
            ),
            @ApiResponse(
                    responseCode = "503",
                    description = "Error de comunicación con ms-stock",
                    content = @Content(schema = @Schema(implementation = Map.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Error interno del servidor",
                    content = @Content(schema = @Schema(implementation = Map.class))
            )
    })
    @PutMapping("/devolver/{id}")
    public ResponseEntity<PrestamoResponseDTO> devolverPrestamo(
            @Parameter(description = "ID del préstamo a devolver", example = "1", required = true)
            @PathVariable Long id) {

        log.info("Petición PUT para devolver préstamo ID: {}", id);

        return ResponseEntity.ok(prestamoService.devolverPrestamo(id));
    }

    @Operation(
            summary = "Cancelar préstamo",
            description = "Cancela un préstamo cambiando su estado a CANCELADO. Si estaba en estado PRESTADO, aumenta el stock del videojuego asociado mediante ms-stock."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Préstamo cancelado correctamente",
                    content = @Content(schema = @Schema(implementation = Map.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Préstamo no encontrado",
                    content = @Content(schema = @Schema(implementation = Map.class))
            ),
            @ApiResponse(
                    responseCode = "503",
                    description = "Error de comunicación con ms-stock",
                    content = @Content(schema = @Schema(implementation = Map.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Error interno del servidor",
                    content = @Content(schema = @Schema(implementation = Map.class))
            )
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> cancelarPrestamo(
            @Parameter(description = "ID del préstamo a cancelar", example = "1", required = true)
            @PathVariable Long id) {

        log.info("Petición DELETE para cancelar préstamo ID: {}", id);

        prestamoService.cancelarPrestamo(id);

        Map<String, String> respuesta = new HashMap<>();
        respuesta.put("mensaje", "Préstamo cancelado correctamente");

        return ResponseEntity.ok(respuesta);
    }

    @Operation(
            summary = "Renovar préstamo",
            description = "Registra una renovación asociada al préstamo mediante relación JPA interna y actualiza la fecha de devolución del préstamo."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Préstamo renovado correctamente",
                    content = @Content(schema = @Schema(implementation = RenovacionPrestamoResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Fecha inválida, motivo inválido o préstamo no renovable",
                    content = @Content(schema = @Schema(implementation = Map.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Préstamo no encontrado",
                    content = @Content(schema = @Schema(implementation = Map.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Error interno del servidor",
                    content = @Content(schema = @Schema(implementation = Map.class))
            )
    })
    @PostMapping("/{id}/renovaciones")
    public ResponseEntity<RenovacionPrestamoResponseDTO> renovarPrestamo(
            @Parameter(description = "ID del préstamo a renovar", example = "1", required = true)
            @PathVariable Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Datos para renovar un préstamo",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = RenovacionPrestamoRequestDTO.class),
                            examples = @ExampleObject(
                                    name = "Ejemplo de renovación",
                                    value = """
                                            {
                                              "nuevaFechaDevolucion": "2026-07-15",
                                              "motivo": "El usuario solicita más tiempo para devolver el videojuego"
                                            }
                                            """
                            )
                    )
            )
            @Valid @RequestBody RenovacionPrestamoRequestDTO requestDTO) {

        log.info("Petición POST para renovar préstamo ID: {}", id);

        RenovacionPrestamoResponseDTO renovacion = prestamoService.renovarPrestamo(id, requestDTO);

        return ResponseEntity.status(HttpStatus.CREATED).body(renovacion);
    }

    @Operation(
            summary = "Listar renovaciones de un préstamo",
            description = "Obtiene el historial de renovaciones asociadas a un préstamo específico."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Renovaciones obtenidas correctamente",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = RenovacionPrestamoResponseDTO.class)))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Préstamo no encontrado",
                    content = @Content(schema = @Schema(implementation = Map.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Error interno del servidor",
                    content = @Content(schema = @Schema(implementation = Map.class))
            )
    })
    @GetMapping("/{id}/renovaciones")
    public ResponseEntity<List<RenovacionPrestamoResponseDTO>> listarRenovacionesPorPrestamo(
            @Parameter(description = "ID del préstamo", example = "1", required = true)
            @PathVariable Long id) {

        log.info("Petición GET para listar renovaciones del préstamo ID: {}", id);

        return ResponseEntity.ok(prestamoService.listarRenovacionesPorPrestamo(id));
    }
}
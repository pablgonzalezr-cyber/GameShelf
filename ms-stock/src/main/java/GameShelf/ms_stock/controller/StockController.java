package GameShelf.ms_stock.controller;

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

import GameShelf.ms_stock.dto.StockRequestDTO;
import GameShelf.ms_stock.dto.StockResponseDTO;
import GameShelf.ms_stock.service.StockService;
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
@RequestMapping("/api/stocks")
@Tag(
        name = "Stock",
        description = "Endpoints para gestionar stock de videojuegos, consultar disponibilidad, aumentar y reducir unidades disponibles."
)
public class StockController {

    private final StockService stockService;

    public StockController(StockService stockService) {
        this.stockService = stockService;
    }

    @Operation(
            summary = "Crear stock",
            description = "Crea un registro de stock para un videojuego. Valida que el videojuego exista y esté disponible mediante comunicación con ms-videojuego."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Stock creado correctamente",
                    content = @Content(schema = @Schema(implementation = StockResponseDTO.class))
            ),
            @ApiResponse(responseCode = "400", description = "Datos inválidos, videojuego no disponible o stock duplicado para el videojuego"),
            @ApiResponse(responseCode = "503", description = "Error de comunicación con ms-videojuego"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PostMapping
    public ResponseEntity<StockResponseDTO> crearStock(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Datos necesarios para crear stock de un videojuego",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = StockRequestDTO.class),
                            examples = @ExampleObject(
                                    name = "Ejemplo de stock",
                                    value = """
                                            {
                                              "videojuegoId": 2,
                                              "cantidadTotal": 10,
                                              "cantidadDisponible": 10,
                                              "estado": "ACTIVO"
                                            }
                                            """
                            )
                    )
            )
            @Valid @RequestBody StockRequestDTO stockRequestDTO) {

        log.info("Petición POST para crear stock");

        StockResponseDTO stockCreado = stockService.crearStock(stockRequestDTO);

        return new ResponseEntity<>(stockCreado, HttpStatus.CREATED);
    }

    @Operation(
            summary = "Listar stocks",
            description = "Obtiene todos los registros de stock existentes en el microservicio."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Lista de stocks obtenida correctamente",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = StockResponseDTO.class)))
            ),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping
    public ResponseEntity<List<StockResponseDTO>> listarStocks() {

        log.info("Petición GET para listar stocks");

        return ResponseEntity.ok(stockService.listarStocks());
    }

    @Operation(
            summary = "Buscar stock por ID",
            description = "Obtiene un registro de stock específico mediante su identificador único."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Stock encontrado correctamente",
                    content = @Content(schema = @Schema(implementation = StockResponseDTO.class))
            ),
            @ApiResponse(responseCode = "404", description = "Stock no encontrado"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/{id}")
    public ResponseEntity<StockResponseDTO> buscarStock(
            @Parameter(description = "ID del stock", example = "1", required = true)
            @PathVariable Long id) {

        log.info("Petición GET para buscar stock ID: {}", id);

        return ResponseEntity.ok(stockService.buscarPorId(id));
    }

    @Operation(
            summary = "Buscar stock por videojuego",
            description = "Obtiene el registro de stock asociado a un videojuego específico."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Stock del videojuego obtenido correctamente",
                    content = @Content(schema = @Schema(implementation = StockResponseDTO.class))
            ),
            @ApiResponse(responseCode = "404", description = "No existe stock para el videojuego indicado"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/videojuego/{videojuegoId}")
    public ResponseEntity<StockResponseDTO> buscarPorVideojuego(
            @Parameter(description = "ID del videojuego", example = "2", required = true)
            @PathVariable Long videojuegoId) {

        log.info("Petición GET para buscar stock por videojuego ID: {}", videojuegoId);

        return ResponseEntity.ok(stockService.buscarPorVideojuego(videojuegoId));
    }

    @Operation(
            summary = "Actualizar stock",
            description = "Actualiza el stock de un videojuego. Valida cantidades, estado y existencia del videojuego mediante ms-videojuego."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Stock actualizado correctamente",
                    content = @Content(schema = @Schema(implementation = StockResponseDTO.class))
            ),
            @ApiResponse(responseCode = "400", description = "Datos inválidos, estado incorrecto o cantidades inconsistentes"),
            @ApiResponse(responseCode = "404", description = "Stock no encontrado"),
            @ApiResponse(responseCode = "503", description = "Error de comunicación con ms-videojuego"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PutMapping("/{id}")
    public ResponseEntity<StockResponseDTO> actualizarStock(
            @Parameter(description = "ID del stock a actualizar", example = "1", required = true)
            @PathVariable Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Datos actualizados del stock",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = StockRequestDTO.class),
                            examples = @ExampleObject(
                                    name = "Ejemplo de actualización",
                                    value = """
                                            {
                                              "videojuegoId": 2,
                                              "cantidadTotal": 12,
                                              "cantidadDisponible": 8,
                                              "estado": "ACTIVO"
                                            }
                                            """
                            )
                    )
            )
            @Valid @RequestBody StockRequestDTO stockRequestDTO) {

        log.info("Petición PUT para actualizar stock ID: {}", id);

        return ResponseEntity.ok(stockService.actualizarStock(id, stockRequestDTO));
    }

    @Operation(
            summary = "Reducir stock disponible",
            description = "Reduce en una unidad la cantidad disponible de un videojuego. Se utiliza normalmente cuando se genera un préstamo."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Stock reducido correctamente",
                    content = @Content(schema = @Schema(implementation = StockResponseDTO.class))
            ),
            @ApiResponse(responseCode = "400", description = "Stock inactivo o sin copias disponibles"),
            @ApiResponse(responseCode = "404", description = "No existe stock para el videojuego indicado"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PutMapping("/reducir/{videojuegoId}")
    public ResponseEntity<StockResponseDTO> reducirStock(
            @Parameter(description = "ID del videojuego cuyo stock será reducido", example = "2", required = true)
            @PathVariable Long videojuegoId) {

        log.info("Petición PUT para reducir stock del videojuego ID: {}", videojuegoId);

        return ResponseEntity.ok(stockService.reducirStock(videojuegoId));
    }

    @Operation(
            summary = "Aumentar stock disponible",
            description = "Aumenta en una unidad la cantidad disponible de un videojuego. Se utiliza normalmente cuando un préstamo es devuelto o cancelado."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Stock aumentado correctamente",
                    content = @Content(schema = @Schema(implementation = StockResponseDTO.class))
            ),
            @ApiResponse(responseCode = "400", description = "Stock inactivo o cantidad disponible igual a cantidad total"),
            @ApiResponse(responseCode = "404", description = "No existe stock para el videojuego indicado"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PutMapping("/aumentar/{videojuegoId}")
    public ResponseEntity<StockResponseDTO> aumentarStock(
            @Parameter(description = "ID del videojuego cuyo stock será aumentado", example = "2", required = true)
            @PathVariable Long videojuegoId) {

        log.info("Petición PUT para aumentar stock del videojuego ID: {}", videojuegoId);

        return ResponseEntity.ok(stockService.aumentarStock(videojuegoId));
    }

    @Operation(
            summary = "Listar stock por estado",
            description = "Obtiene registros de stock filtrados por estado. Estados válidos: ACTIVO o INACTIVO."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Stocks filtrados por estado obtenidos correctamente",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = StockResponseDTO.class)))
            ),
            @ApiResponse(responseCode = "400", description = "Estado inválido"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<StockResponseDTO>> listarPorEstado(
            @Parameter(description = "Estado del stock", example = "ACTIVO", required = true)
            @PathVariable String estado) {

        log.info("Petición GET para listar stock por estado: {}", estado);

        return ResponseEntity.ok(stockService.listarPorEstado(estado));
    }

    @Operation(
            summary = "Desactivar stock",
            description = "Realiza borrado lógico del stock cambiando su estado a INACTIVO."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Stock desactivado correctamente"),
            @ApiResponse(responseCode = "404", description = "Stock no encontrado"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminarStock(
            @Parameter(description = "ID del stock a desactivar", example = "1", required = true)
            @PathVariable Long id) {

        log.info("Petición DELETE para desactivar stock ID: {}", id);

        stockService.eliminarStock(id);

        return ResponseEntity.ok("Stock desactivado correctamente");
    }
}
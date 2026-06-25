package GameShelf.ms_categoria.controller;

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

import GameShelf.ms_categoria.dto.CategoriaRequestDTO;
import GameShelf.ms_categoria.dto.CategoriaResponseDTO;
import GameShelf.ms_categoria.service.CategoriaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Tag(
        name = "Categorías",
        description = "Endpoints para crear, listar, buscar, actualizar y desactivar categorías de videojuegos en GameShelf"
)
@RestController
@RequestMapping("/api/categorias")
public class CategoriaController {

    private final CategoriaService categoriaService;

    public CategoriaController(CategoriaService categoriaService) {
        this.categoriaService = categoriaService;
    }

    @Operation(
            summary = "Crear categoría",
            description = "Registra una nueva categoría de videojuegos. Valida que el nombre no esté duplicado y asigna estado ACTIVO si no se envía estado."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Categoría creada correctamente",
                    content = @Content(schema = @Schema(implementation = CategoriaResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Datos inválidos, validación fallida, estado incorrecto o categoría duplicada",
                    content = @Content(schema = @Schema(implementation = Map.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Error interno del servidor",
                    content = @Content(schema = @Schema(implementation = Map.class))
            )
    })
    @PostMapping
    public ResponseEntity<CategoriaResponseDTO> crearCategoria(
            @Valid @RequestBody CategoriaRequestDTO categoriaRequestDTO) {

        log.info("Petición POST para crear categoría");

        CategoriaResponseDTO categoriaCreada = categoriaService.crearCategoria(categoriaRequestDTO);

        return new ResponseEntity<>(categoriaCreada, HttpStatus.CREATED);
    }

    @Operation(
            summary = "Listar categorías",
            description = "Obtiene todas las categorías registradas en el sistema."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Categorías listadas correctamente",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = CategoriaResponseDTO.class)))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Error interno del servidor",
                    content = @Content(schema = @Schema(implementation = Map.class))
            )
    })
    @GetMapping
    public ResponseEntity<List<CategoriaResponseDTO>> listarCategorias() {

        log.info("Petición GET para listar categorías");

        return ResponseEntity.ok(categoriaService.listarCategorias());
    }

    @Operation(
            summary = "Buscar categoría por ID",
            description = "Obtiene una categoría específica mediante su identificador."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Categoría encontrada correctamente",
                    content = @Content(schema = @Schema(implementation = CategoriaResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Categoría no encontrada",
                    content = @Content(schema = @Schema(implementation = Map.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Error interno del servidor",
                    content = @Content(schema = @Schema(implementation = Map.class))
            )
    })
    @GetMapping("/{id}")
    public ResponseEntity<CategoriaResponseDTO> buscarCategoria(
            @Parameter(description = "ID de la categoría", example = "1")
            @PathVariable Long id) {

        log.info("Petición GET para buscar categoría ID: {}", id);

        return ResponseEntity.ok(categoriaService.buscarPorId(id));
    }

    @Operation(
            summary = "Buscar categoría por nombre exacto",
            description = "Obtiene una categoría específica usando su nombre exacto."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Categoría encontrada correctamente por nombre exacto",
                    content = @Content(schema = @Schema(implementation = CategoriaResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Categoría no encontrada con el nombre indicado",
                    content = @Content(schema = @Schema(implementation = Map.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Error interno del servidor",
                    content = @Content(schema = @Schema(implementation = Map.class))
            )
    })
    @GetMapping("/nombre/{nombre}")
    public ResponseEntity<CategoriaResponseDTO> buscarPorNombreExacto(
            @Parameter(description = "Nombre exacto de la categoría", example = "AVENTURA")
            @PathVariable String nombre) {

        log.info("Petición GET para buscar categoría exacta por nombre: {}", nombre);

        return ResponseEntity.ok(categoriaService.buscarPorNombreExacto(nombre));
    }

    @Operation(
            summary = "Actualizar categoría",
            description = "Actualiza los datos de una categoría existente, incluyendo nombre, descripción y estado."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Categoría actualizada correctamente",
                    content = @Content(schema = @Schema(implementation = CategoriaResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Datos inválidos, validación fallida, estado incorrecto o nombre de categoría duplicado",
                    content = @Content(schema = @Schema(implementation = Map.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Categoría no encontrada",
                    content = @Content(schema = @Schema(implementation = Map.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Error interno del servidor",
                    content = @Content(schema = @Schema(implementation = Map.class))
            )
    })
    @PutMapping("/{id}")
    public ResponseEntity<CategoriaResponseDTO> actualizarCategoria(
            @Parameter(description = "ID de la categoría", example = "1")
            @PathVariable Long id,
            @Valid @RequestBody CategoriaRequestDTO categoriaRequestDTO) {

        log.info("Petición PUT para actualizar categoría ID: {}", id);

        return ResponseEntity.ok(categoriaService.actualizarCategoria(id, categoriaRequestDTO));
    }

    @Operation(
            summary = "Desactivar categoría",
            description = "Realiza un borrado lógico de la categoría, cambiando su estado a INACTIVO."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Categoría desactivada correctamente",
                    content = @Content(schema = @Schema(implementation = Map.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Categoría no encontrada",
                    content = @Content(schema = @Schema(implementation = Map.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Error interno del servidor",
                    content = @Content(schema = @Schema(implementation = Map.class))
            )
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> eliminarCategoria(
            @Parameter(description = "ID de la categoría", example = "1")
            @PathVariable Long id) {

        log.info("Petición DELETE para desactivar categoría ID: {}", id);

        categoriaService.eliminarCategoria(id);

        Map<String, String> respuesta = new HashMap<>();
        respuesta.put("mensaje", "Categoría desactivada correctamente");

        return ResponseEntity.ok(respuesta);
    }

    @Operation(
            summary = "Buscar categorías por estado",
            description = "Obtiene categorías filtradas por estado. Estados permitidos: ACTIVO o INACTIVO."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Categorías encontradas correctamente por estado",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = CategoriaResponseDTO.class)))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Estado inválido. Los valores permitidos son ACTIVO o INACTIVO",
                    content = @Content(schema = @Schema(implementation = Map.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Error interno del servidor",
                    content = @Content(schema = @Schema(implementation = Map.class))
            )
    })
    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<CategoriaResponseDTO>> buscarPorEstado(
            @Parameter(description = "Estado de la categoría", example = "ACTIVO")
            @PathVariable String estado) {

        log.info("Petición GET para buscar categorías por estado: {}", estado);

        return ResponseEntity.ok(categoriaService.buscarPorEstado(estado));
    }

    @Operation(
            summary = "Buscar categorías por nombre",
            description = "Busca categorías cuyo nombre contenga el texto ingresado."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Categorías encontradas correctamente por coincidencia de nombre",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = CategoriaResponseDTO.class)))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Error interno del servidor",
                    content = @Content(schema = @Schema(implementation = Map.class))
            )
    })
    @GetMapping("/buscar/{nombre}")
    public ResponseEntity<List<CategoriaResponseDTO>> buscarPorNombre(
            @Parameter(description = "Texto o nombre de categoría a buscar", example = "aventura")
            @PathVariable String nombre) {

        log.info("Petición GET para buscar categorías por nombre: {}", nombre);

        return ResponseEntity.ok(categoriaService.buscarPorNombre(nombre));
    }
}
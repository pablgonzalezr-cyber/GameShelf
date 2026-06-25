package GameShelf.ms_videojuego.controller;

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

import GameShelf.ms_videojuego.dto.VideoJuegoRequestDTO;
import GameShelf.ms_videojuego.dto.VideoJuegoResponseDTO;
import GameShelf.ms_videojuego.service.VideoJuegoService;
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
        name = "Videojuegos",
        description = "Endpoints para crear, listar, buscar, actualizar y desactivar videojuegos en GameShelf"
)
@RestController
@RequestMapping("/api/videojuegos")
public class VideoJuegoController {

    private final VideoJuegoService videoJuegoService;

    public VideoJuegoController(VideoJuegoService videoJuegoService) {
        this.videoJuegoService = videoJuegoService;
    }

    @Operation(
            summary = "Crear videojuego",
            description = "Registra un nuevo videojuego en el catálogo. Valida título, descripción, precio, plataforma y categoría mediante ms-categoria."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Videojuego creado correctamente",
                    content = @Content(schema = @Schema(implementation = VideoJuegoResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Datos inválidos, videojuego duplicado, estado incorrecto, plataforma inválida o categoría inactiva",
                    content = @Content(schema = @Schema(implementation = Map.class))
            ),
            @ApiResponse(
                    responseCode = "503",
                    description = "No se pudo validar la categoría porque ms-categoria no se encuentra disponible",
                    content = @Content(schema = @Schema(implementation = Map.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Error interno del servidor",
                    content = @Content(schema = @Schema(implementation = Map.class))
            )
    })
    @PostMapping
    public ResponseEntity<VideoJuegoResponseDTO> crearVideoJuego(
            @Valid @RequestBody VideoJuegoRequestDTO videoJuegoRequestDTO) {

        log.info("Petición POST para crear videojuego: {}", videoJuegoRequestDTO.getTitulo());

        VideoJuegoResponseDTO videojuegoCreado = videoJuegoService.crearVideoJuego(videoJuegoRequestDTO);

        return new ResponseEntity<>(videojuegoCreado, HttpStatus.CREATED);
    }

    @Operation(
            summary = "Listar videojuegos",
            description = "Obtiene todos los videojuegos registrados en el catálogo."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Videojuegos listados correctamente",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = VideoJuegoResponseDTO.class)))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Error interno del servidor",
                    content = @Content(schema = @Schema(implementation = Map.class))
            )
    })
    @GetMapping
    public ResponseEntity<List<VideoJuegoResponseDTO>> listarVideoJuegos() {

        log.info("Petición GET para listar videojuegos");

        return ResponseEntity.ok(videoJuegoService.listarVideoJuegos());
    }

    @Operation(
            summary = "Buscar videojuego por ID",
            description = "Obtiene un videojuego específico mediante su identificador."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Videojuego encontrado correctamente",
                    content = @Content(schema = @Schema(implementation = VideoJuegoResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Videojuego no encontrado",
                    content = @Content(schema = @Schema(implementation = Map.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Error interno del servidor",
                    content = @Content(schema = @Schema(implementation = Map.class))
            )
    })
    @GetMapping("/{id}")
    public ResponseEntity<VideoJuegoResponseDTO> buscarPorId(
            @Parameter(description = "ID del videojuego", example = "1")
            @PathVariable Long id) {

        log.info("Petición GET para buscar videojuego ID: {}", id);

        return ResponseEntity.ok(videoJuegoService.buscarPorId(id));
    }

    @Operation(
            summary = "Actualizar videojuego",
            description = "Actualiza los datos de un videojuego existente, incluyendo título, descripción, precio, categoría, plataforma y estado."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Videojuego actualizado correctamente",
                    content = @Content(schema = @Schema(implementation = VideoJuegoResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Datos inválidos, videojuego duplicado, estado incorrecto, plataforma inválida o categoría inactiva",
                    content = @Content(schema = @Schema(implementation = Map.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Videojuego no encontrado",
                    content = @Content(schema = @Schema(implementation = Map.class))
            ),
            @ApiResponse(
                    responseCode = "503",
                    description = "No se pudo validar la categoría porque ms-categoria no se encuentra disponible",
                    content = @Content(schema = @Schema(implementation = Map.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Error interno del servidor",
                    content = @Content(schema = @Schema(implementation = Map.class))
            )
    })
    @PutMapping("/{id}")
    public ResponseEntity<VideoJuegoResponseDTO> actualizarVideoJuego(
            @Parameter(description = "ID del videojuego", example = "1")
            @PathVariable Long id,
            @Valid @RequestBody VideoJuegoRequestDTO videoJuegoRequestDTO) {

        log.info("Petición PUT para actualizar videojuego ID: {}", id);

        return ResponseEntity.ok(videoJuegoService.actualizarVideoJuego(id, videoJuegoRequestDTO));
    }

    @Operation(
            summary = "Desactivar videojuego",
            description = "Realiza un borrado lógico del videojuego, cambiando su estado a INACTIVO."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Videojuego desactivado correctamente",
                    content = @Content(schema = @Schema(implementation = Map.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Videojuego no encontrado",
                    content = @Content(schema = @Schema(implementation = Map.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Error interno del servidor",
                    content = @Content(schema = @Schema(implementation = Map.class))
            )
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> eliminarVideoJuego(
            @Parameter(description = "ID del videojuego", example = "1")
            @PathVariable Long id) {

        log.info("Petición DELETE para desactivar videojuego con ID: {}", id);

        videoJuegoService.eliminarVideoJuego(id);

        Map<String, String> respuesta = new HashMap<>();
        respuesta.put("mensaje", "Videojuego desactivado correctamente");

        return ResponseEntity.ok(respuesta);
    }

    @Operation(
            summary = "Buscar videojuegos por categoría",
            description = "Obtiene todos los videojuegos asociados a una categoría específica."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Videojuegos encontrados correctamente por categoría",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = VideoJuegoResponseDTO.class)))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Error interno del servidor",
                    content = @Content(schema = @Schema(implementation = Map.class))
            )
    })
    @GetMapping("/categoria/{categoriaId}")
    public ResponseEntity<List<VideoJuegoResponseDTO>> buscarPorCategoria(
            @Parameter(description = "ID de la categoría", example = "1")
            @PathVariable Long categoriaId) {

        log.info("Petición GET para buscar videojuegos por categoría ID: {}", categoriaId);

        return ResponseEntity.ok(videoJuegoService.buscarPorCategoria(categoriaId));
    }

    @Operation(
            summary = "Buscar videojuegos por título",
            description = "Busca videojuegos cuyo título contenga el texto ingresado."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Videojuegos encontrados correctamente por coincidencia de título",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = VideoJuegoResponseDTO.class)))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Error interno del servidor",
                    content = @Content(schema = @Schema(implementation = Map.class))
            )
    })
    @GetMapping("/buscar/{titulo}")
    public ResponseEntity<List<VideoJuegoResponseDTO>> buscarPorTitulo(
            @Parameter(description = "Texto o título del videojuego", example = "mario")
            @PathVariable String titulo) {

        log.info("Petición GET para buscar videojuegos por título: {}", titulo);

        return ResponseEntity.ok(videoJuegoService.buscarPorTitulo(titulo));
    }

    @Operation(
            summary = "Buscar videojuegos por estado",
            description = "Obtiene videojuegos filtrados por estado. Estados permitidos: DISPONIBLE, NO_DISPONIBLE o INACTIVO."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Videojuegos encontrados correctamente por estado",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = VideoJuegoResponseDTO.class)))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Estado inválido. Los valores permitidos son DISPONIBLE, NO_DISPONIBLE o INACTIVO",
                    content = @Content(schema = @Schema(implementation = Map.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Error interno del servidor",
                    content = @Content(schema = @Schema(implementation = Map.class))
            )
    })
    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<VideoJuegoResponseDTO>> buscarPorEstado(
            @Parameter(description = "Estado del videojuego", example = "DISPONIBLE")
            @PathVariable String estado) {

        log.info("Petición GET para buscar videojuegos por estado: {}", estado);

        return ResponseEntity.ok(videoJuegoService.buscarPorEstado(estado));
    }

    @Operation(
            summary = "Buscar videojuegos por plataforma",
            description = "Obtiene videojuegos filtrados por plataforma."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Videojuegos encontrados correctamente por plataforma",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = VideoJuegoResponseDTO.class)))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Plataforma inválida o no informada",
                    content = @Content(schema = @Schema(implementation = Map.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Error interno del servidor",
                    content = @Content(schema = @Schema(implementation = Map.class))
            )
    })
    @GetMapping("/plataforma/{plataforma}")
    public ResponseEntity<List<VideoJuegoResponseDTO>> buscarPorPlataforma(
            @Parameter(description = "Plataforma del videojuego", example = "PC")
            @PathVariable String plataforma) {

        log.info("Petición GET para buscar videojuegos por plataforma: {}", plataforma);

        return ResponseEntity.ok(videoJuegoService.buscarPorPlataforma(plataforma));
    }
}
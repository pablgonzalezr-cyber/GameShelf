package GameShelf.ms_videojuego.controller;

import GameShelf.ms_videojuego.dto.VideoJuegoRequestDTO;
import GameShelf.ms_videojuego.dto.VideoJuegoResponseDTO;
import GameShelf.ms_videojuego.service.VideoJuegoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    @GetMapping
    public ResponseEntity<List<VideoJuegoResponseDTO>> listarVideoJuegos() {

        log.info("Petición GET para listar videojuegos");

        return ResponseEntity.ok(videoJuegoService.listarVideoJuegos());
    }

    @Operation(
            summary = "Buscar videojuego por ID",
            description = "Obtiene un videojuego específico mediante su identificador."
    )
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
    @GetMapping("/plataforma/{plataforma}")
    public ResponseEntity<List<VideoJuegoResponseDTO>> buscarPorPlataforma(
            @Parameter(description = "Plataforma del videojuego", example = "PC")
            @PathVariable String plataforma) {

        log.info("Petición GET para buscar videojuegos por plataforma: {}", plataforma);

        return ResponseEntity.ok(videoJuegoService.buscarPorPlataforma(plataforma));
    }
}

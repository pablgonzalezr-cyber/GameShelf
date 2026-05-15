package GameShelf.ms_videojuego.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import GameShelf.ms_videojuego.dto.VideoJuegoRequestDTO;
import GameShelf.ms_videojuego.dto.VideoJuegoResponseDTO;
import GameShelf.ms_videojuego.service.VideoJuegoService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/videojuegos")
public class VideoJuegoController {

    private final VideoJuegoService videoJuegoService;

    public VideoJuegoController(VideoJuegoService videoJuegoService) {
        this.videoJuegoService = videoJuegoService;
    }

    @PostMapping
    public ResponseEntity<VideoJuegoResponseDTO> crearVideoJuego(
            @Valid @RequestBody VideoJuegoRequestDTO videoJuegoRequestDTO) {

        log.info("Petición POST para crear videojuego: {}", videoJuegoRequestDTO.getTitulo());

        VideoJuegoResponseDTO videojuegoCreado = videoJuegoService.crearVideoJuego(videoJuegoRequestDTO);

        return new ResponseEntity<>(videojuegoCreado, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<VideoJuegoResponseDTO>> listarVideoJuegos() {

        log.info("Petición GET para listar videojuegos");

        return ResponseEntity.ok(videoJuegoService.listarVideoJuegos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<VideoJuegoResponseDTO> buscarPorId(@PathVariable Long id) {

        log.info("Petición GET para buscar videojuego ID: {}", id);

        return ResponseEntity.ok(videoJuegoService.buscarPorId(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<VideoJuegoResponseDTO> actualizarVideoJuego(
            @PathVariable Long id,
            @Valid @RequestBody VideoJuegoRequestDTO videoJuegoRequestDTO) {

        log.info("Petición PUT para actualizar videojuego ID: {}", id);

        return ResponseEntity.ok(videoJuegoService.actualizarVideoJuego(id, videoJuegoRequestDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> eliminarVideoJuego(@PathVariable Long id) {

        log.info("Petición DELETE para eliminar videojuego ID: {}", id);

        videoJuegoService.eliminarVideoJuego(id);

        Map<String, String> respuesta = new HashMap<>();
        respuesta.put("mensaje", "Videojuego eliminado correctamente");

        return ResponseEntity.ok(respuesta);
    }

    @GetMapping("/categoria/{categoriaId}")
    public ResponseEntity<List<VideoJuegoResponseDTO>> buscarPorCategoria(@PathVariable Long categoriaId) {

        log.info("Petición GET para buscar videojuegos por categoría ID: {}", categoriaId);

        return ResponseEntity.ok(videoJuegoService.buscarPorCategoria(categoriaId));
    }

    @GetMapping("/buscar/{titulo}")
    public ResponseEntity<List<VideoJuegoResponseDTO>> buscarPorTitulo(@PathVariable String titulo) {

        log.info("Petición GET para buscar videojuegos por título: {}", titulo);

        return ResponseEntity.ok(videoJuegoService.buscarPorTitulo(titulo));
    }

    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<VideoJuegoResponseDTO>> buscarPorEstado(@PathVariable String estado) {

        log.info("Petición GET para buscar videojuegos por estado: {}", estado);

        return ResponseEntity.ok(videoJuegoService.buscarPorEstado(estado));
    }

    @GetMapping("/plataforma/{plataforma}")
    public ResponseEntity<List<VideoJuegoResponseDTO>> buscarPorPlataforma(@PathVariable String plataforma) {

        log.info("Petición GET para buscar videojuegos por plataforma: {}", plataforma);

        return ResponseEntity.ok(videoJuegoService.buscarPorPlataforma(plataforma));
    }
}
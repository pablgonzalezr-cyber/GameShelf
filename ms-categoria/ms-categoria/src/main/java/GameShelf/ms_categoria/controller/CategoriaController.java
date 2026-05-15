package GameShelf.ms_categoria.controller;

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

import GameShelf.ms_categoria.dto.CategoriaRequestDTO;
import GameShelf.ms_categoria.dto.CategoriaResponseDTO;
import GameShelf.ms_categoria.service.CategoriaService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/categorias")
public class CategoriaController {

    private final CategoriaService categoriaService;

    public CategoriaController(CategoriaService categoriaService) {
        this.categoriaService = categoriaService;
    }

    @PostMapping
    public ResponseEntity<CategoriaResponseDTO> crearCategoria(
            @Valid @RequestBody CategoriaRequestDTO categoriaRequestDTO) {

        log.info("Petición POST para crear categoría");

        CategoriaResponseDTO categoriaCreada = categoriaService.crearCategoria(categoriaRequestDTO);

        return new ResponseEntity<>(categoriaCreada, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<CategoriaResponseDTO>> listarCategorias() {

        log.info("Petición GET para listar categorías");

        return ResponseEntity.ok(categoriaService.listarCategorias());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoriaResponseDTO> buscarCategoria(@PathVariable Long id) {

        log.info("Petición GET para buscar categoría ID: {}", id);

        return ResponseEntity.ok(categoriaService.buscarPorId(id));
    }

    @GetMapping("/nombre/{nombre}")
    public ResponseEntity<CategoriaResponseDTO> buscarPorNombreExacto(@PathVariable String nombre) {

        log.info("Petición GET para buscar categoría exacta por nombre: {}", nombre);

        return ResponseEntity.ok(categoriaService.buscarPorNombreExacto(nombre));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoriaResponseDTO> actualizarCategoria(
            @PathVariable Long id,
            @Valid @RequestBody CategoriaRequestDTO categoriaRequestDTO) {

        log.info("Petición PUT para actualizar categoría ID: {}", id);

        return ResponseEntity.ok(categoriaService.actualizarCategoria(id, categoriaRequestDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminarCategoria(@PathVariable Long id) {

        log.info("Petición DELETE para desactivar categoría ID: {}", id);

        categoriaService.eliminarCategoria(id);

        return ResponseEntity.ok("Categoría desactivada correctamente");
    }

    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<CategoriaResponseDTO>> buscarPorEstado(@PathVariable String estado) {

        log.info("Petición GET para buscar categorías por estado: {}", estado);

        return ResponseEntity.ok(categoriaService.buscarPorEstado(estado));
    }

    @GetMapping("/buscar/{nombre}")
    public ResponseEntity<List<CategoriaResponseDTO>> buscarPorNombre(@PathVariable String nombre) {

        log.info("Petición GET para buscar categorías por nombre: {}", nombre);

        return ResponseEntity.ok(categoriaService.buscarPorNombre(nombre));
    }
}
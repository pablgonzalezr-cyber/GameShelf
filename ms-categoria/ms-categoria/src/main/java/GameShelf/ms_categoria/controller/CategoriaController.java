package GameShelf.ms_categoria.controller;

import GameShelf.ms_categoria.dto.CategoriaRequestDTO;
import GameShelf.ms_categoria.dto.CategoriaResponseDTO;
import GameShelf.ms_categoria.service.CategoriaService;
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
    @GetMapping
    public ResponseEntity<List<CategoriaResponseDTO>> listarCategorias() {

        log.info("Petición GET para listar categorías");

        return ResponseEntity.ok(categoriaService.listarCategorias());
    }

    @Operation(
            summary = "Buscar categoría por ID",
            description = "Obtiene una categoría específica mediante su identificador."
    )
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
    @GetMapping("/buscar/{nombre}")
    public ResponseEntity<List<CategoriaResponseDTO>> buscarPorNombre(
            @Parameter(description = "Texto o nombre de categoría a buscar", example = "aventura")
            @PathVariable String nombre) {

        log.info("Petición GET para buscar categorías por nombre: {}", nombre);

        return ResponseEntity.ok(categoriaService.buscarPorNombre(nombre));
    }
}

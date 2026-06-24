package GameShelf.ms_roles.controller;

import GameShelf.ms_roles.dto.RolRequestDTO;
import GameShelf.ms_roles.dto.RolResponseDTO;
import GameShelf.ms_roles.service.RolService;
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
        name = "Roles",
        description = "Endpoints para crear, listar, buscar, actualizar, desactivar y validar roles en GameShelf"
)
@RestController
@RequestMapping("/api/roles")
public class RolController {

    private final RolService rolService;

    public RolController(RolService rolService) {
        this.rolService = rolService;
    }

    @Operation(
            summary = "Crear rol",
            description = "Registra un nuevo rol en el sistema. Valida que el nombre no esté duplicado y asigna estado ACTIVO si no se envía estado."
    )
    @PostMapping
    public ResponseEntity<RolResponseDTO> crearRol(
            @Valid @RequestBody RolRequestDTO rolRequestDTO) {

        log.info("Petición POST para crear rol: {}", rolRequestDTO.getNombre());

        RolResponseDTO rolCreado = rolService.crearRol(rolRequestDTO);

        return new ResponseEntity<>(rolCreado, HttpStatus.CREATED);
    }

    @Operation(
            summary = "Listar roles",
            description = "Obtiene todos los roles registrados en el sistema."
    )
    @GetMapping
    public ResponseEntity<List<RolResponseDTO>> listarRoles() {

        log.info("Petición GET para listar roles");

        return ResponseEntity.ok(rolService.listarRoles());
    }

    @Operation(
            summary = "Buscar rol por ID",
            description = "Obtiene un rol específico mediante su identificador."
    )
    @GetMapping("/{id}")
    public ResponseEntity<RolResponseDTO> buscarPorId(
            @Parameter(description = "ID del rol", example = "1")
            @PathVariable Long id) {

        log.info("Petición GET para buscar rol ID: {}", id);

        return ResponseEntity.ok(rolService.buscarPorId(id));
    }

    @Operation(
            summary = "Actualizar rol",
            description = "Actualiza los datos de un rol existente, incluyendo nombre, descripción y estado."
    )
    @PutMapping("/{id}")
    public ResponseEntity<RolResponseDTO> actualizarRol(
            @Parameter(description = "ID del rol", example = "1")
            @PathVariable Long id,
            @Valid @RequestBody RolRequestDTO rolRequestDTO) {

        log.info("Petición PUT para actualizar rol ID: {}", id);

        RolResponseDTO rolActualizado = rolService.actualizarRol(id, rolRequestDTO);

        return ResponseEntity.ok(rolActualizado);
    }

    @Operation(
            summary = "Desactivar rol",
            description = "Realiza un borrado lógico del rol, cambiando su estado a INACTIVO."
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> eliminarRol(
            @Parameter(description = "ID del rol", example = "1")
            @PathVariable Long id) {

        log.info("Petición DELETE para eliminar rol ID: {}", id);

        rolService.eliminarRol(id);

        Map<String, String> respuesta = new HashMap<>();
        respuesta.put("mensaje", "Rol desactivado correctamente");

        return ResponseEntity.ok(respuesta);
    }

    @Operation(
            summary = "Buscar roles por estado",
            description = "Obtiene roles filtrados por estado. Estados permitidos: ACTIVO o INACTIVO."
    )
    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<RolResponseDTO>> buscarPorEstado(
            @Parameter(description = "Estado del rol", example = "ACTIVO")
            @PathVariable String estado) {

        log.info("Petición GET para buscar roles por estado: {}", estado);

        return ResponseEntity.ok(rolService.buscarPorEstado(estado));
    }

    @Operation(
            summary = "Buscar roles por nombre",
            description = "Busca roles cuyo nombre contenga el texto ingresado."
    )
    @GetMapping("/buscar/{nombre}")
    public ResponseEntity<List<RolResponseDTO>> buscarPorNombre(
            @Parameter(description = "Texto o nombre de rol a buscar", example = "admin")
            @PathVariable String nombre) {

        log.info("Petición GET para buscar roles por nombre: {}", nombre);

        return ResponseEntity.ok(rolService.buscarPorNombre(nombre));
    }

    @Operation(
            summary = "Buscar rol por nombre exacto",
            description = "Busca un rol específico usando el nombre exacto del rol."
    )
    @GetMapping("/nombre/{nombre}")
    public ResponseEntity<RolResponseDTO> buscarPorNombreExacto(
            @Parameter(description = "Nombre exacto del rol", example = "ADMINISTRADOR")
            @PathVariable String nombre) {

        log.info("Petición GET para buscar rol exacto por nombre: {}", nombre);

        return ResponseEntity.ok(rolService.buscarPorNombreExacto(nombre));
    }

    @Operation(
            summary = "Validar rol activo",
            description = "Valida si un rol existe y se encuentra en estado ACTIVO. Este endpoint es usado por otros microservicios como ms-usuario y ms-autorizacion."
    )
    @GetMapping("/validar/{nombre}")
    public ResponseEntity<Boolean> validarRol(
            @Parameter(description = "Nombre del rol a validar", example = "CLIENTE")
            @PathVariable String nombre) {

        log.info("Petición GET para validar rol: {}", nombre);

        return ResponseEntity.ok(rolService.validarRolActivo(nombre));
    }
}

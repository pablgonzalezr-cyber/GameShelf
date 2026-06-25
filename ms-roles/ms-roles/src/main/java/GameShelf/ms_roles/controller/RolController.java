package GameShelf.ms_roles.controller;

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

import GameShelf.ms_roles.dto.RolRequestDTO;
import GameShelf.ms_roles.dto.RolResponseDTO;
import GameShelf.ms_roles.service.RolService;
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
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Rol creado correctamente",
                    content = @Content(schema = @Schema(implementation = RolResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Datos inválidos, validación fallida, estado incorrecto o rol duplicado",
                    content = @Content(schema = @Schema(implementation = Map.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Error interno del servidor",
                    content = @Content(schema = @Schema(implementation = Map.class))
            )
    })
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
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Roles listados correctamente",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = RolResponseDTO.class)))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Error interno del servidor",
                    content = @Content(schema = @Schema(implementation = Map.class))
            )
    })
    @GetMapping
    public ResponseEntity<List<RolResponseDTO>> listarRoles() {

        log.info("Petición GET para listar roles");

        return ResponseEntity.ok(rolService.listarRoles());
    }

    @Operation(
            summary = "Buscar rol por ID",
            description = "Obtiene un rol específico mediante su identificador."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Rol encontrado correctamente",
                    content = @Content(schema = @Schema(implementation = RolResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Rol no encontrado",
                    content = @Content(schema = @Schema(implementation = Map.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Error interno del servidor",
                    content = @Content(schema = @Schema(implementation = Map.class))
            )
    })
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
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Rol actualizado correctamente",
                    content = @Content(schema = @Schema(implementation = RolResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Datos inválidos, validación fallida, estado incorrecto o nombre de rol duplicado",
                    content = @Content(schema = @Schema(implementation = Map.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Rol no encontrado",
                    content = @Content(schema = @Schema(implementation = Map.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Error interno del servidor",
                    content = @Content(schema = @Schema(implementation = Map.class))
            )
    })
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
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Rol desactivado correctamente",
                    content = @Content(schema = @Schema(implementation = Map.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Rol no encontrado",
                    content = @Content(schema = @Schema(implementation = Map.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Error interno del servidor",
                    content = @Content(schema = @Schema(implementation = Map.class))
            )
    })
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
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Roles encontrados correctamente por estado",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = RolResponseDTO.class)))
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
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Roles encontrados correctamente por coincidencia de nombre",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = RolResponseDTO.class)))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Error interno del servidor",
                    content = @Content(schema = @Schema(implementation = Map.class))
            )
    })
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
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Rol encontrado correctamente por nombre exacto",
                    content = @Content(schema = @Schema(implementation = RolResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Rol no encontrado con el nombre indicado",
                    content = @Content(schema = @Schema(implementation = Map.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Error interno del servidor",
                    content = @Content(schema = @Schema(implementation = Map.class))
            )
    })
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
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Validación ejecutada correctamente. Retorna true si el rol existe y está activo, o false en caso contrario",
                    content = @Content(schema = @Schema(implementation = Boolean.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Error interno del servidor",
                    content = @Content(schema = @Schema(implementation = Map.class))
            )
    })
    @GetMapping("/validar/{nombre}")
    public ResponseEntity<Boolean> validarRol(
            @Parameter(description = "Nombre del rol a validar", example = "CLIENTE")
            @PathVariable String nombre) {

        log.info("Petición GET para validar rol: {}", nombre);

        return ResponseEntity.ok(rolService.validarRolActivo(nombre));
    }
}
package GameShelf.ms_autorizacion.controller;

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

import GameShelf.ms_autorizacion.dto.AutorizacionRequestDTO;
import GameShelf.ms_autorizacion.dto.AutorizacionResponseDTO;
import GameShelf.ms_autorizacion.dto.ValidarAutorizacionRequestDTO;
import GameShelf.ms_autorizacion.service.AutorizacionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@Tag(
        name = "Autorizaciones",
        description = "Endpoints para gestionar autorizaciones, módulos y permisos de usuarios en GameShelf"
)
@RestController
@RequestMapping("/api/autorizaciones")
public class AutorizacionController {

    private final AutorizacionService autorizacionService;

    public AutorizacionController(AutorizacionService autorizacionService) {
        this.autorizacionService = autorizacionService;
    }

    @Operation(
            summary = "Crear autorización",
            description = "Registra una nueva autorización para un usuario, asociando usuario, rol, módulo, permiso y estado. Valida usuario mediante ms-usuario y rol mediante ms-roles."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Autorización creada correctamente",
                    content = @Content(schema = @Schema(implementation = AutorizacionResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Datos inválidos, usuario inexistente, usuario inactivo, rol inexistente, módulo inválido, permiso inválido o estado inválido",
                    content = @Content(schema = @Schema(implementation = Map.class))
            ),
            @ApiResponse(
                    responseCode = "503",
                    description = "No se pudo validar usuario o rol porque un microservicio remoto no se encuentra disponible",
                    content = @Content(schema = @Schema(implementation = Map.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Error interno del servidor",
                    content = @Content(schema = @Schema(implementation = Map.class))
            )
    })
    @PostMapping
    public ResponseEntity<AutorizacionResponseDTO> crearAutorizacion(
            @Valid @RequestBody AutorizacionRequestDTO autorizacionRequestDTO) {

        AutorizacionResponseDTO respuesta = autorizacionService.crearAutorizacion(autorizacionRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(respuesta);
    }

    @Operation(
            summary = "Listar autorizaciones",
            description = "Obtiene todas las autorizaciones registradas en el sistema."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Autorizaciones listadas correctamente",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = AutorizacionResponseDTO.class)))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Error interno del servidor",
                    content = @Content(schema = @Schema(implementation = Map.class))
            )
    })
    @GetMapping
    public ResponseEntity<List<AutorizacionResponseDTO>> listarAutorizaciones() {
        return ResponseEntity.ok(autorizacionService.listarAutorizaciones());
    }

    @Operation(
            summary = "Buscar autorización por ID",
            description = "Obtiene una autorización específica mediante su identificador."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Autorización encontrada correctamente",
                    content = @Content(schema = @Schema(implementation = AutorizacionResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Autorización no encontrada",
                    content = @Content(schema = @Schema(implementation = Map.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Error interno del servidor",
                    content = @Content(schema = @Schema(implementation = Map.class))
            )
    })
    @GetMapping("/{id}")
    public ResponseEntity<AutorizacionResponseDTO> obtenerAutorizacionPorId(
            @Parameter(description = "ID de la autorización", example = "1")
            @PathVariable Long id) {

        return ResponseEntity.ok(autorizacionService.obtenerAutorizacionPorId(id));
    }

    @Operation(
            summary = "Listar autorizaciones por usuario",
            description = "Obtiene todas las autorizaciones asociadas a un usuario específico. Valida la existencia y estado del usuario mediante ms-usuario."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Autorizaciones del usuario listadas correctamente",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = AutorizacionResponseDTO.class)))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Usuario inválido, inexistente o inactivo",
                    content = @Content(schema = @Schema(implementation = Map.class))
            ),
            @ApiResponse(
                    responseCode = "503",
                    description = "No se pudo comunicar con ms-usuario",
                    content = @Content(schema = @Schema(implementation = Map.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Error interno del servidor",
                    content = @Content(schema = @Schema(implementation = Map.class))
            )
    })
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<AutorizacionResponseDTO>> listarPorUsuario(
            @Parameter(description = "ID del usuario", example = "1")
            @PathVariable Long usuarioId) {

        return ResponseEntity.ok(autorizacionService.listarPorUsuario(usuarioId));
    }

    @Operation(
            summary = "Actualizar autorización",
            description = "Actualiza los datos de una autorización existente. Valida usuario, rol, módulo, permiso y estado antes de guardar."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Autorización actualizada correctamente",
                    content = @Content(schema = @Schema(implementation = AutorizacionResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Datos inválidos, usuario inexistente, usuario inactivo, rol inexistente, módulo inválido, permiso inválido o estado inválido",
                    content = @Content(schema = @Schema(implementation = Map.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Autorización no encontrada",
                    content = @Content(schema = @Schema(implementation = Map.class))
            ),
            @ApiResponse(
                    responseCode = "503",
                    description = "No se pudo validar usuario o rol porque un microservicio remoto no se encuentra disponible",
                    content = @Content(schema = @Schema(implementation = Map.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Error interno del servidor",
                    content = @Content(schema = @Schema(implementation = Map.class))
            )
    })
    @PutMapping("/{id}")
    public ResponseEntity<AutorizacionResponseDTO> actualizarAutorizacion(
            @Parameter(description = "ID de la autorización", example = "1")
            @PathVariable Long id,
            @Valid @RequestBody AutorizacionRequestDTO autorizacionRequestDTO) {

        return ResponseEntity.ok(autorizacionService.actualizarAutorizacion(id, autorizacionRequestDTO));
    }

    @Operation(
            summary = "Eliminar autorización",
            description = "Realiza un borrado lógico de la autorización, cambiando su estado a INACTIVO."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Autorización eliminada lógicamente correctamente",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "La autorización ya está inactiva",
                    content = @Content(schema = @Schema(implementation = Map.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Autorización no encontrada",
                    content = @Content(schema = @Schema(implementation = Map.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Error interno del servidor",
                    content = @Content(schema = @Schema(implementation = Map.class))
            )
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarAutorizacion(
            @Parameter(description = "ID de la autorización", example = "1")
            @PathVariable Long id) {

        autorizacionService.eliminarAutorizacion(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Validar autorización",
            description = "Valida si un usuario tiene permiso para acceder a un módulo específico. Retorna true si existe permiso exacto, permiso TOTAL o permiso ADMIN en estado ACTIVO."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Validación ejecutada correctamente. Retorna true si el usuario tiene permiso o false si no lo tiene",
                    content = @Content(schema = @Schema(implementation = Boolean.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Usuario inválido, módulo inválido o permiso inválido",
                    content = @Content(schema = @Schema(implementation = Map.class))
            ),
            @ApiResponse(
                    responseCode = "503",
                    description = "No se pudo comunicar con ms-usuario para validar el usuario",
                    content = @Content(schema = @Schema(implementation = Map.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Error interno del servidor",
                    content = @Content(schema = @Schema(implementation = Map.class))
            )
    })
    @PostMapping("/validar")
    public ResponseEntity<Boolean> validarAutorizacion(
            @Valid @RequestBody ValidarAutorizacionRequestDTO validarAutorizacionRequestDTO) {

        return ResponseEntity.ok(autorizacionService.validarAutorizacion(validarAutorizacionRequestDTO));
    }
}
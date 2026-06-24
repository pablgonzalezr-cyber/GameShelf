package GameShelf.ms_autorizacion.controller;

import GameShelf.ms_autorizacion.dto.AutorizacionRequestDTO;
import GameShelf.ms_autorizacion.dto.AutorizacionResponseDTO;
import GameShelf.ms_autorizacion.dto.ValidarAutorizacionRequestDTO;
import GameShelf.ms_autorizacion.service.AutorizacionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
            description = "Registra una nueva autorización para un usuario, asociando rol, módulo, permiso y estado."
    )
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
    @GetMapping
    public ResponseEntity<List<AutorizacionResponseDTO>> listarAutorizaciones() {
        return ResponseEntity.ok(autorizacionService.listarAutorizaciones());
    }

    @Operation(
            summary = "Buscar autorización por ID",
            description = "Obtiene una autorización específica mediante su identificador."
    )
    @GetMapping("/{id}")
    public ResponseEntity<AutorizacionResponseDTO> obtenerAutorizacionPorId(
            @Parameter(description = "ID de la autorización", example = "1")
            @PathVariable Long id) {

        return ResponseEntity.ok(autorizacionService.obtenerAutorizacionPorId(id));
    }

    @Operation(
            summary = "Listar autorizaciones por usuario",
            description = "Obtiene todas las autorizaciones asociadas a un usuario específico."
    )
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<AutorizacionResponseDTO>> listarPorUsuario(
            @Parameter(description = "ID del usuario", example = "1")
            @PathVariable Long usuarioId) {

        return ResponseEntity.ok(autorizacionService.listarPorUsuario(usuarioId));
    }

    @Operation(
            summary = "Actualizar autorización",
            description = "Actualiza los datos de una autorización existente."
    )
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
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarAutorizacion(
            @Parameter(description = "ID de la autorización", example = "1")
            @PathVariable Long id) {

        autorizacionService.eliminarAutorizacion(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Validar autorización",
            description = "Valida si un usuario tiene permiso para acceder a un módulo específico. Considera permisos exactos, TOTAL o ADMIN."
    )
    @PostMapping("/validar")
    public ResponseEntity<Boolean> validarAutorizacion(
            @Valid @RequestBody ValidarAutorizacionRequestDTO validarAutorizacionRequestDTO) {

        return ResponseEntity.ok(autorizacionService.validarAutorizacion(validarAutorizacionRequestDTO));
    }
}
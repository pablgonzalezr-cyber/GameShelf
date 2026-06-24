package GameShelf.ms_usuario.controller;

import GameShelf.ms_usuario.dto.UsuarioRequestDTO;
import GameShelf.ms_usuario.dto.UsuarioResponseDTO;
import GameShelf.ms_usuario.dto.UsuarioUpdateDTO;
import GameShelf.ms_usuario.service.UsuarioService;
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
        name = "Usuarios",
        description = "Endpoints para crear, listar, buscar, actualizar y eliminar usuarios en GameShelf"
)
@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @Operation(
            summary = "Crear usuario",
            description = "Registra un nuevo usuario en el sistema. Valida que el nombre de usuario y correo no estén duplicados, cifra la contraseña y valida el rol mediante ms-roles."
    )
    @PostMapping
    public ResponseEntity<UsuarioResponseDTO> crearUsuario(
            @Valid @RequestBody UsuarioRequestDTO usuarioRequestDTO) {

        log.info("Petición POST para crear usuario");

        UsuarioResponseDTO usuarioCreado = usuarioService.crearUsuario(usuarioRequestDTO);

        return new ResponseEntity<>(usuarioCreado, HttpStatus.CREATED);
    }

    @Operation(
            summary = "Listar usuarios",
            description = "Obtiene todos los usuarios registrados en el sistema."
    )
    @GetMapping
    public ResponseEntity<List<UsuarioResponseDTO>> listarUsuarios() {

        log.info("Petición GET para listar usuarios");

        return ResponseEntity.ok(usuarioService.listarUsuarios());
    }

    @Operation(
            summary = "Buscar usuario por ID",
            description = "Obtiene un usuario específico mediante su identificador."
    )
    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponseDTO> buscarUsuario(
            @Parameter(description = "ID del usuario", example = "1")
            @PathVariable Long id) {

        log.info("Petición GET para buscar usuario ID: {}", id);

        return ResponseEntity.ok(usuarioService.buscarPorId(id));
    }

    @Operation(
            summary = "Actualizar usuario",
            description = "Actualiza los datos principales de un usuario. Permite cambiar usuario, correo, contraseña y rol."
    )
    @PutMapping("/{id}")
    public ResponseEntity<UsuarioResponseDTO> actualizarUsuario(
            @Parameter(description = "ID del usuario", example = "1")
            @PathVariable Long id,
            @Valid @RequestBody UsuarioUpdateDTO usuarioUpdateDTO) {

        log.info("Petición PUT para actualizar usuario ID: {}", id);

        return ResponseEntity.ok(usuarioService.actualizarUsuario(id, usuarioUpdateDTO));
    }

    @Operation(
            summary = "Eliminar usuario",
            description = "Elimina un usuario del sistema mediante su ID."
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> eliminarUsuario(
            @Parameter(description = "ID del usuario", example = "1")
            @PathVariable Long id) {

        log.info("Petición DELETE para eliminar usuario ID: {}", id);

        usuarioService.eliminarUsuario(id);

        Map<String, String> respuesta = new HashMap<>();
        respuesta.put("mensaje", "Usuario eliminado correctamente");

        return ResponseEntity.ok(respuesta);
    }

    @Operation(
            summary = "Buscar usuarios por rol",
            description = "Obtiene una lista de usuarios filtrados por rol."
    )
    @GetMapping("/rol/{rol}")
    public ResponseEntity<List<UsuarioResponseDTO>> buscarPorRol(
            @Parameter(description = "Nombre del rol", example = "CLIENTE")
            @PathVariable String rol) {

        log.info("Petición GET para buscar usuarios por rol: {}", rol);

        return ResponseEntity.ok(usuarioService.buscarPorRol(rol));
    }

    @Operation(
            summary = "Buscar usuarios por nombre",
            description = "Busca usuarios cuyo nombre contenga el texto ingresado."
    )
    @GetMapping("/buscar/{usuario}")
    public ResponseEntity<List<UsuarioResponseDTO>> buscarPorNombre(
            @Parameter(description = "Texto o nombre de usuario a buscar", example = "pablo")
            @PathVariable String usuario) {

        log.info("Petición GET para buscar usuarios por nombre: {}", usuario);

        return ResponseEntity.ok(usuarioService.buscarPorNombre(usuario));
    }
}
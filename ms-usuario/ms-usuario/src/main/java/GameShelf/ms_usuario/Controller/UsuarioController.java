package GameShelf.ms_usuario.Controller;

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

import GameShelf.ms_usuario.Service.UsuarioService;
import GameShelf.ms_usuario.dto.UsuarioRequestDTO;
import GameShelf.ms_usuario.dto.UsuarioResponseDTO;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @PostMapping
    public ResponseEntity<UsuarioResponseDTO> crearUsuario(@Valid @RequestBody UsuarioRequestDTO usuarioRequestDTO) {

        log.info("Petición POST para crear usuario");

        UsuarioResponseDTO usuarioCreado = usuarioService.crearUsuario(usuarioRequestDTO);

        return new ResponseEntity<>(usuarioCreado, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<UsuarioResponseDTO>> listarUsuarios() {

        log.info("Petición GET para listar usuarios");

        return ResponseEntity.ok(usuarioService.listarUsuarios());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponseDTO> buscarUsuario(@PathVariable Long id) {

        log.info("Petición GET para buscar usuario ID: {}", id);

        return ResponseEntity.ok(usuarioService.buscarPorId(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UsuarioResponseDTO> actualizarUsuario(
            @PathVariable Long id,
            @Valid @RequestBody UsuarioRequestDTO usuarioRequestDTO) {

        log.info("Petición PUT para actualizar usuario ID: {}", id);

        return ResponseEntity.ok(usuarioService.actualizarUsuario(id, usuarioRequestDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminarUsuario(@PathVariable Long id) {

        log.info("Petición DELETE para eliminar usuario ID: {}", id);

        usuarioService.eliminarUsuario(id);

        return ResponseEntity.ok("Usuario eliminado correctamente");
    }

    @GetMapping("/rol/{rol}")
    public ResponseEntity<List<UsuarioResponseDTO>> buscarPorRol(@PathVariable String rol) {

        log.info("Petición GET para buscar usuarios por rol: {}", rol);

        return ResponseEntity.ok(usuarioService.buscarPorRol(rol));
    }

    @GetMapping("/buscar/{usuario}")
    public ResponseEntity<List<UsuarioResponseDTO>> buscarPorNombre(@PathVariable String usuario) {

        log.info("Petición GET para buscar usuarios por nombre: {}", usuario);

        return ResponseEntity.ok(usuarioService.buscarPorNombre(usuario));
    }
}
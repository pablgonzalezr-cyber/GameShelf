package GameShelf.ms_usuario.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import GameShelf.ms_usuario.dto.UsuarioRequestDTO;
import GameShelf.ms_usuario.dto.UsuarioResponseDTO;
import GameShelf.ms_usuario.dto.UsuarioUpdateDTO;
import GameShelf.ms_usuario.service.UsuarioService;
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
            @Valid @RequestBody UsuarioUpdateDTO usuarioUpdateDTO) {

        log.info("Petición PUT para actualizar usuario ID: {}", id);

        return ResponseEntity.ok(usuarioService.actualizarUsuario(id, usuarioUpdateDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> eliminarUsuario(@PathVariable Long id) {

        log.info("Petición DELETE para eliminar usuario ID: {}", id);

        usuarioService.eliminarUsuario(id);

        Map<String, String> respuesta = new HashMap<>();
        respuesta.put("mensaje", "Usuario eliminado correctamente");

        return ResponseEntity.ok(respuesta);
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
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
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/roles")
public class RolController {

    private final RolService rolService;

    public RolController(RolService rolService) {
        this.rolService = rolService;
    }

    @PostMapping
    public ResponseEntity<RolResponseDTO> crearRol(@Valid @RequestBody RolRequestDTO rolRequestDTO) {

        log.info("Petición POST para crear rol: {}", rolRequestDTO.getNombre());

        RolResponseDTO rolCreado = rolService.crearRol(rolRequestDTO);

        return new ResponseEntity<>(rolCreado, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<RolResponseDTO>> listarRoles() {

        log.info("Petición GET para listar roles");

        return ResponseEntity.ok(rolService.listarRoles());
    }

    @GetMapping("/{id}")
    public ResponseEntity<RolResponseDTO> buscarPorId(@PathVariable Long id) {

        log.info("Petición GET para buscar rol ID: {}", id);

        return ResponseEntity.ok(rolService.buscarPorId(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<RolResponseDTO> actualizarRol(
            @PathVariable Long id,
            @Valid @RequestBody RolRequestDTO rolRequestDTO) {

        log.info("Petición PUT para actualizar rol ID: {}", id);

        RolResponseDTO rolActualizado = rolService.actualizarRol(id, rolRequestDTO);

        return ResponseEntity.ok(rolActualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> eliminarRol(@PathVariable Long id) {

        log.info("Petición DELETE para eliminar rol ID: {}", id);

        rolService.eliminarRol(id);

        Map<String, String> respuesta = new HashMap<>();
        respuesta.put("mensaje", "Rol desactivado correctamente");

        return ResponseEntity.ok(respuesta);
    }

    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<RolResponseDTO>> buscarPorEstado(@PathVariable String estado) {

        log.info("Petición GET para buscar roles por estado: {}", estado);

        return ResponseEntity.ok(rolService.buscarPorEstado(estado));
    }

    @GetMapping("/buscar/{nombre}")
    public ResponseEntity<List<RolResponseDTO>> buscarPorNombre(@PathVariable String nombre) {

        log.info("Petición GET para buscar roles por nombre: {}", nombre);

        return ResponseEntity.ok(rolService.buscarPorNombre(nombre));
    }

    @GetMapping("/nombre/{nombre}")
    public ResponseEntity<RolResponseDTO> buscarPorNombreExacto(@PathVariable String nombre) {

        log.info("Petición GET para buscar rol exacto por nombre: {}", nombre);

        return ResponseEntity.ok(rolService.buscarPorNombreExacto(nombre));
    }

    @GetMapping("/validar/{nombre}")
    public ResponseEntity<Boolean> validarRol(@PathVariable String nombre) {

        log.info("Petición GET para validar rol: {}", nombre);

        return ResponseEntity.ok(rolService.validarRolActivo(nombre));
    }
}
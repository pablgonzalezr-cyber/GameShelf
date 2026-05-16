package GameShelf.ms_autorizacion.controller;

import GameShelf.ms_autorizacion.dto.AutorizacionRequestDTO;
import GameShelf.ms_autorizacion.dto.AutorizacionResponseDTO;
import GameShelf.ms_autorizacion.dto.ValidarAutorizacionRequestDTO;
import GameShelf.ms_autorizacion.service.AutorizacionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api/autorizaciones")
@RestController
public class AutorizacionController {

    private final AutorizacionService autorizacionService;

    public AutorizacionController(AutorizacionService autorizacionService) {
        this.autorizacionService = autorizacionService;
    }

    @PostMapping
    public ResponseEntity<AutorizacionResponseDTO> crearAutorizacion(@Valid @RequestBody AutorizacionRequestDTO autorizacionRequestDTO) {
        AutorizacionResponseDTO respuesta = autorizacionService.crearAutorizacion(autorizacionRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(respuesta);
    }

    @GetMapping
    public ResponseEntity<List<AutorizacionResponseDTO>> listarAutorizaciones() {
        return ResponseEntity.ok(autorizacionService.listarAutorizaciones());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AutorizacionResponseDTO> obtenerAutorizacionPorId(@PathVariable Long id) {
        return ResponseEntity.ok(autorizacionService.obtenerAutorizacionPorId(id));
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<AutorizacionResponseDTO>> listarPorUsuario(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(autorizacionService.listarPorUsuario(usuarioId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AutorizacionResponseDTO> actualizarAutorizacion(@PathVariable Long id,
                                                                          @Valid @RequestBody AutorizacionRequestDTO autorizacionRequestDTO) {
        return ResponseEntity.ok(autorizacionService.actualizarAutorizacion(id, autorizacionRequestDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarAutorizacion(@PathVariable Long id) {
        autorizacionService.eliminarAutorizacion(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/validar")
    public ResponseEntity<Boolean> validarAutorizacion(@Valid @RequestBody ValidarAutorizacionRequestDTO validarAutorizacionRequestDTO) {
        return ResponseEntity.ok(autorizacionService.validarAutorizacion(validarAutorizacionRequestDTO));
    }
}

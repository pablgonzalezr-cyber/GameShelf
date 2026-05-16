package GameShelf.ms_multa.controller;

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

import GameShelf.ms_multa.dto.MultaRequestDTO;
import GameShelf.ms_multa.dto.MultaResponseDTO;
import GameShelf.ms_multa.service.MultaService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/multas")
public class MultaController {

    private final MultaService multaService;

    public MultaController(MultaService multaService) {
        this.multaService = multaService;
    }

    @PostMapping
    public ResponseEntity<MultaResponseDTO> crearMulta(@Valid @RequestBody MultaRequestDTO multaRequestDTO) {

        log.info("Petición POST para crear multa");

        MultaResponseDTO multa = multaService.crearMulta(multaRequestDTO);

        return ResponseEntity.status(HttpStatus.CREATED).body(multa);
    }

    @GetMapping
    public ResponseEntity<List<MultaResponseDTO>> listarMultas() {

        log.info("Petición GET para listar multas");

        return ResponseEntity.ok(multaService.listarMultas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<MultaResponseDTO> buscarPorId(@PathVariable Long id) {

        log.info("Petición GET para buscar multa ID: {}", id);

        return ResponseEntity.ok(multaService.buscarPorId(id));
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<MultaResponseDTO>> buscarPorUsuario(@PathVariable Long usuarioId) {

        log.info("Petición GET para buscar multas por usuario ID: {}", usuarioId);

        return ResponseEntity.ok(multaService.buscarPorUsuario(usuarioId));
    }

    @GetMapping("/prestamo/{prestamoId}")
    public ResponseEntity<List<MultaResponseDTO>> buscarPorPrestamo(@PathVariable Long prestamoId) {

        log.info("Petición GET para buscar multas por préstamo ID: {}", prestamoId);

        return ResponseEntity.ok(multaService.buscarPorPrestamo(prestamoId));
    }

    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<MultaResponseDTO>> buscarPorEstado(@PathVariable String estado) {

        log.info("Petición GET para buscar multas por estado: {}", estado);

        return ResponseEntity.ok(multaService.buscarPorEstado(estado));
    }

    @PutMapping("/{id}")
    public ResponseEntity<MultaResponseDTO> actualizarMulta(
            @PathVariable Long id,
            @Valid @RequestBody MultaRequestDTO multaRequestDTO) {

        log.info("Petición PUT para actualizar multa ID: {}", id);

        return ResponseEntity.ok(multaService.actualizarMulta(id, multaRequestDTO));
    }

    @PutMapping("/pagar/{id}")
    public ResponseEntity<MultaResponseDTO> pagarMulta(@PathVariable Long id) {

        log.info("Petición PUT para pagar multa ID: {}", id);

        return ResponseEntity.ok(multaService.pagarMulta(id));
    }

    @PutMapping("/anular/{id}")
    public ResponseEntity<MultaResponseDTO> anularMulta(@PathVariable Long id) {

        log.info("Petición PUT para anular multa ID: {}", id);

        return ResponseEntity.ok(multaService.anularMulta(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminarMulta(@PathVariable Long id) {

        log.info("Petición DELETE para eliminar multa ID: {}", id);

        multaService.eliminarMulta(id);

        return ResponseEntity.ok("Multa eliminada correctamente");
    }
}
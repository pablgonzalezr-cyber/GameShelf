package GameShelf.ms_prestamo.controller;

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

import GameShelf.ms_prestamo.dto.PrestamoRequestDTO;
import GameShelf.ms_prestamo.dto.PrestamoResponseDTO;
import GameShelf.ms_prestamo.dto.RenovacionPrestamoRequestDTO;
import GameShelf.ms_prestamo.dto.RenovacionPrestamoResponseDTO;
import GameShelf.ms_prestamo.service.PrestamoService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/prestamos")
public class PrestamoController {

    private final PrestamoService prestamoService;

    public PrestamoController(PrestamoService prestamoService) {
        this.prestamoService = prestamoService;
    }

    @PostMapping
    public ResponseEntity<PrestamoResponseDTO> crearPrestamo(@Valid @RequestBody PrestamoRequestDTO prestamoRequestDTO) {

        log.info("Petición POST para crear préstamo");

        PrestamoResponseDTO prestamo = prestamoService.crearPrestamo(prestamoRequestDTO);

        return ResponseEntity.status(HttpStatus.CREATED).body(prestamo);
    }

    @GetMapping
    public ResponseEntity<List<PrestamoResponseDTO>> listarPrestamos() {

        log.info("Petición GET para listar préstamos");

        return ResponseEntity.ok(prestamoService.listarPrestamos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PrestamoResponseDTO> buscarPorId(@PathVariable Long id) {

        log.info("Petición GET para buscar préstamo ID: {}", id);

        return ResponseEntity.ok(prestamoService.buscarPorId(id));
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<PrestamoResponseDTO>> buscarPorUsuario(@PathVariable Long usuarioId) {

        log.info("Petición GET para buscar préstamos por usuario ID: {}", usuarioId);

        return ResponseEntity.ok(prestamoService.buscarPorUsuario(usuarioId));
    }

    @GetMapping("/videojuego/{videojuegoId}")
    public ResponseEntity<List<PrestamoResponseDTO>> buscarPorVideojuego(@PathVariable Long videojuegoId) {

        log.info("Petición GET para buscar préstamos por videojuego ID: {}", videojuegoId);

        return ResponseEntity.ok(prestamoService.buscarPorVideojuego(videojuegoId));
    }

    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<PrestamoResponseDTO>> buscarPorEstado(@PathVariable String estado) {

        log.info("Petición GET para buscar préstamos por estado: {}", estado);

        return ResponseEntity.ok(prestamoService.buscarPorEstado(estado));
    }

    @PutMapping("/devolver/{id}")
    public ResponseEntity<PrestamoResponseDTO> devolverPrestamo(@PathVariable Long id) {

        log.info("Petición PUT para devolver préstamo ID: {}", id);

        return ResponseEntity.ok(prestamoService.devolverPrestamo(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> cancelarPrestamo(@PathVariable Long id) {

        log.info("Petición DELETE para cancelar préstamo ID: {}", id);

        prestamoService.cancelarPrestamo(id);

        return ResponseEntity.ok("Préstamo cancelado correctamente");
    }

    @PostMapping("/{id}/renovaciones")
    public ResponseEntity<RenovacionPrestamoResponseDTO> renovarPrestamo(
            @PathVariable Long id,
            @Valid @RequestBody RenovacionPrestamoRequestDTO requestDTO) {

        log.info("Petición POST para renovar préstamo ID: {}", id);

        RenovacionPrestamoResponseDTO renovacion = prestamoService.renovarPrestamo(id, requestDTO);

        return ResponseEntity.status(HttpStatus.CREATED).body(renovacion);
    }

    @GetMapping("/{id}/renovaciones")
    public ResponseEntity<List<RenovacionPrestamoResponseDTO>> listarRenovacionesPorPrestamo(@PathVariable Long id) {

        log.info("Petición GET para listar renovaciones del préstamo ID: {}", id);

        return ResponseEntity.ok(prestamoService.listarRenovacionesPorPrestamo(id));
    }
}

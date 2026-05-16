package GameShelf.ms_reserva.controller;

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

import GameShelf.ms_reserva.dto.ReservaRequestDTO;
import GameShelf.ms_reserva.dto.ReservaResponseDTO;
import GameShelf.ms_reserva.service.ReservaService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/reservas")
public class ReservaController {

    private final ReservaService reservaService;

    public ReservaController(ReservaService reservaService) {
        this.reservaService = reservaService;
    }

    @GetMapping
    public ResponseEntity<List<ReservaResponseDTO>> listarReservas() {

        log.info("Petición GET para listar reservas");

        return ResponseEntity.ok(reservaService.listarReservas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReservaResponseDTO> buscarReservaPorId(@PathVariable Long id) {

        log.info("Petición GET para buscar reserva ID: {}", id);

        return ResponseEntity.ok(reservaService.buscarReservaPorId(id));
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<ReservaResponseDTO>> buscarReservasPorUsuario(@PathVariable Long usuarioId) {

        log.info("Petición GET para buscar reservas por usuario ID: {}", usuarioId);

        return ResponseEntity.ok(reservaService.buscarReservasPorUsuario(usuarioId));
    }

    @GetMapping("/videojuego/{videojuegoId}")
    public ResponseEntity<List<ReservaResponseDTO>> buscarReservasPorVideojuego(@PathVariable Long videojuegoId) {

        log.info("Petición GET para buscar reservas por videojuego ID: {}", videojuegoId);

        return ResponseEntity.ok(reservaService.buscarReservasPorVideojuego(videojuegoId));
    }

    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<ReservaResponseDTO>> buscarReservasPorEstado(@PathVariable String estado) {

        log.info("Petición GET para buscar reservas por estado: {}", estado);

        return ResponseEntity.ok(reservaService.buscarReservasPorEstado(estado));
    }

    @PostMapping
    public ResponseEntity<ReservaResponseDTO> crearReserva(@Valid @RequestBody ReservaRequestDTO reservaRequestDTO) {

        log.info("Petición POST para crear reserva");

        ReservaResponseDTO reserva = reservaService.crearReserva(reservaRequestDTO);

        return ResponseEntity.status(HttpStatus.CREATED).body(reserva);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ReservaResponseDTO> actualizarReserva(
            @PathVariable Long id,
            @Valid @RequestBody ReservaRequestDTO reservaRequestDTO) {

        log.info("Petición PUT para actualizar reserva ID: {}", id);

        return ResponseEntity.ok(reservaService.actualizarReserva(id, reservaRequestDTO));
    }

    @PutMapping("/confirmar/{id}")
    public ResponseEntity<ReservaResponseDTO> confirmarReserva(@PathVariable Long id) {

        log.info("Petición PUT para confirmar reserva ID: {}", id);

        return ResponseEntity.ok(reservaService.confirmarReserva(id));
    }

    @PutMapping("/cancelar/{id}")
    public ResponseEntity<ReservaResponseDTO> cancelarReserva(@PathVariable Long id) {

        log.info("Petición PUT para cancelar reserva ID: {}", id);

        return ResponseEntity.ok(reservaService.cancelarReserva(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarReserva(@PathVariable Long id) {

        log.info("Petición DELETE para eliminar/cancelar reserva ID: {}", id);

        reservaService.eliminarReserva(id);

        return ResponseEntity.noContent().build();
    }
}
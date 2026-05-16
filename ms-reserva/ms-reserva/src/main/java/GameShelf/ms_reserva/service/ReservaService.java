package GameShelf.ms_reserva.service;

import GameShelf.ms_reserva.dto.ReservaRequestDTO;
import GameShelf.ms_reserva.dto.ReservaResponseDTO;

import java.util.List;

public interface ReservaService {

    List<ReservaResponseDTO> listarReservas();

    ReservaResponseDTO buscarReservaPorId(Long id);

    List<ReservaResponseDTO> buscarReservasPorUsuario(Long usuarioId);

    List<ReservaResponseDTO> buscarReservasPorVideojuego(Long videojuegoId);

    ReservaResponseDTO crearReserva(ReservaRequestDTO reservaRequestDTO);

    ReservaResponseDTO actualizarReserva(Long id, ReservaRequestDTO reservaRequestDTO);

    ReservaResponseDTO confirmarReserva(Long id);

    ReservaResponseDTO cancelarReserva(Long id);

    void eliminarReserva(Long id);
}


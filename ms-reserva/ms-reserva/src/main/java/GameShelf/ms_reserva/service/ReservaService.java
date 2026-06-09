package GameShelf.ms_reserva.service;

import java.util.List;

import GameShelf.ms_reserva.dto.HistorialReservaResponseDTO;
import GameShelf.ms_reserva.dto.ReservaRequestDTO;
import GameShelf.ms_reserva.dto.ReservaResponseDTO;

public interface ReservaService {

    List<ReservaResponseDTO> listarReservas();

    ReservaResponseDTO buscarReservaPorId(Long id);

    List<ReservaResponseDTO> buscarReservasPorUsuario(Long usuarioId);

    List<ReservaResponseDTO> buscarReservasPorVideojuego(Long videojuegoId);

    List<ReservaResponseDTO> buscarReservasPorEstado(String estado);

    ReservaResponseDTO crearReserva(ReservaRequestDTO reservaRequestDTO);

    ReservaResponseDTO actualizarReserva(Long id, ReservaRequestDTO reservaRequestDTO);

    ReservaResponseDTO confirmarReserva(Long id);

    ReservaResponseDTO cancelarReserva(Long id);

    List<HistorialReservaResponseDTO> listarHistorialPorReserva(Long reservaId);

    void eliminarReserva(Long id);
}

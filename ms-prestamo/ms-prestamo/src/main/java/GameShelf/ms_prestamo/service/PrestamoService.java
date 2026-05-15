package GameShelf.ms_prestamo.service;

import java.util.List;

import GameShelf.ms_prestamo.dto.PrestamoRequestDTO;
import GameShelf.ms_prestamo.dto.PrestamoResponseDTO;

public interface PrestamoService {

    PrestamoResponseDTO crearPrestamo(PrestamoRequestDTO prestamoRequestDTO);

    List<PrestamoResponseDTO> listarPrestamos();

    PrestamoResponseDTO buscarPorId(Long id);

    List<PrestamoResponseDTO> buscarPorUsuario(Long usuarioId);

    List<PrestamoResponseDTO> buscarPorVideojuego(Long videojuegoId);

    List<PrestamoResponseDTO> buscarPorEstado(String estado);

    PrestamoResponseDTO devolverPrestamo(Long id);

    void cancelarPrestamo(Long id);
}
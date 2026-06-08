package GameShelf.ms_prestamo.service;

import java.util.List;

import GameShelf.ms_prestamo.dto.PrestamoRequestDTO;
import GameShelf.ms_prestamo.dto.PrestamoResponseDTO;
import GameShelf.ms_prestamo.dto.RenovacionPrestamoRequestDTO;
import GameShelf.ms_prestamo.dto.RenovacionPrestamoResponseDTO;

public interface PrestamoService {

    PrestamoResponseDTO crearPrestamo(PrestamoRequestDTO prestamoRequestDTO);

    List<PrestamoResponseDTO> listarPrestamos();

    PrestamoResponseDTO buscarPorId(Long id);

    List<PrestamoResponseDTO> buscarPorUsuario(Long usuarioId);

    List<PrestamoResponseDTO> buscarPorVideojuego(Long videojuegoId);

    List<PrestamoResponseDTO> buscarPorEstado(String estado);

    PrestamoResponseDTO devolverPrestamo(Long id);

    RenovacionPrestamoResponseDTO renovarPrestamo(Long id, RenovacionPrestamoRequestDTO requestDTO);

    List<RenovacionPrestamoResponseDTO> listarRenovacionesPorPrestamo(Long id);

    void cancelarPrestamo(Long id);
}
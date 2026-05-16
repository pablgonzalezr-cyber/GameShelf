package GameShelf.ms_multa.service;

import java.util.List;

import GameShelf.ms_multa.dto.MultaRequestDTO;
import GameShelf.ms_multa.dto.MultaResponseDTO;

public interface MultaService {

    MultaResponseDTO crearMulta(MultaRequestDTO multaRequestDTO);

    List<MultaResponseDTO> listarMultas();

    MultaResponseDTO buscarPorId(Long id);

    List<MultaResponseDTO> buscarPorUsuario(Long usuarioId);

    List<MultaResponseDTO> buscarPorPrestamo(Long prestamoId);

    List<MultaResponseDTO> buscarPorEstado(String estado);

    MultaResponseDTO pagarMulta(Long id);

    MultaResponseDTO anularMulta(Long id);

    void eliminarMulta(Long id);
}

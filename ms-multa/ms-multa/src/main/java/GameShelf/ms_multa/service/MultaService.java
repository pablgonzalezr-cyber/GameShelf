package GameShelf.ms_multa.service;

import java.util.List;

import GameShelf.ms_multa.dto.MultaRequestDTO;
import GameShelf.ms_multa.dto.MultaResponseDTO;
import GameShelf.ms_multa.dto.PagoMultaRequestDTO;
import GameShelf.ms_multa.dto.PagoMultaResponseDTO;

public interface MultaService {

    MultaResponseDTO crearMulta(MultaRequestDTO multaRequestDTO);

    List<MultaResponseDTO> listarMultas();

    MultaResponseDTO buscarPorId(Long id);

    List<MultaResponseDTO> buscarPorUsuario(Long usuarioId);

    List<MultaResponseDTO> buscarPorPrestamo(Long prestamoId);

    List<MultaResponseDTO> buscarPorEstado(String estado);

    MultaResponseDTO actualizarMulta(Long id, MultaRequestDTO multaRequestDTO);

    MultaResponseDTO pagarMulta(Long id);

    MultaResponseDTO anularMulta(Long id);

    PagoMultaResponseDTO registrarPago(Long multaId, PagoMultaRequestDTO pagoRequestDTO);

    List<PagoMultaResponseDTO> listarPagosPorMulta(Long multaId);

    void eliminarMulta(Long id);
}
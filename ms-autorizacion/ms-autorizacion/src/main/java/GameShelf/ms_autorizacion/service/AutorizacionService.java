package GameShelf.ms_autorizacion.service;

import GameShelf.ms_autorizacion.dto.AutorizacionRequestDTO;
import GameShelf.ms_autorizacion.dto.AutorizacionResponseDTO;
import GameShelf.ms_autorizacion.dto.ValidarAutorizacionRequestDTO;

import java.util.List;

public interface AutorizacionService {

    AutorizacionResponseDTO crearAutorizacion(AutorizacionRequestDTO autorizacionRequestDTO);

    List<AutorizacionResponseDTO> listarAutorizaciones();

    AutorizacionResponseDTO obtenerAutorizacionPorId(Long id);

    List<AutorizacionResponseDTO> listarPorUsuario(Long usuarioId);

    AutorizacionResponseDTO actualizarAutorizacion(Long id, AutorizacionRequestDTO autorizacionRequestDTO);

    void eliminarAutorizacion(Long id);

    boolean validarAutorizacion(ValidarAutorizacionRequestDTO validarAutorizacionRequestDTO);
}

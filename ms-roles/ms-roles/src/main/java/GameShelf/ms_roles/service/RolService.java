package GameShelf.ms_roles.service;

import java.util.List;

import GameShelf.ms_roles.dto.RolRequestDTO;
import GameShelf.ms_roles.dto.RolResponseDTO;

public interface RolService {

    RolResponseDTO crearRol(RolRequestDTO rolRequestDTO);

    List<RolResponseDTO> listarRoles();

    RolResponseDTO buscarPorId(Long id);

    RolResponseDTO actualizarRol(Long id, RolRequestDTO rolRequestDTO);

    void eliminarRol(Long id);

    List<RolResponseDTO> buscarPorEstado(String estado);

    List<RolResponseDTO> buscarPorNombre(String nombre);

    RolResponseDTO buscarPorNombreExacto(String nombre);
}
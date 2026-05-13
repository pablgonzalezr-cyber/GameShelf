package GameShelf.ms_usuario.Service;

import java.util.List;

import GameShelf.ms_usuario.dto.UsuarioRequestDTO;
import GameShelf.ms_usuario.dto.UsuarioResponseDTO;

public interface UsuarioService {

    UsuarioResponseDTO crearUsuario(UsuarioRequestDTO usuarioRequestDTO);

    List<UsuarioResponseDTO> listarUsuarios();

    UsuarioResponseDTO buscarPorId(Long id);

    UsuarioResponseDTO actualizarUsuario(Long id, UsuarioRequestDTO usuarioRequestDTO);

    void eliminarUsuario(Long id);

    List<UsuarioResponseDTO> buscarPorRol(String rol);

    List<UsuarioResponseDTO> buscarPorNombre(String usuario);
}
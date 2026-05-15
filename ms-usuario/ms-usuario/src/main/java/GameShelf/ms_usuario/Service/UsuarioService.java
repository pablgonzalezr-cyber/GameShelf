package GameShelf.ms_usuario.Service;

import java.util.List;

import GameShelf.ms_usuario.dto.UsuarioRequestDTO;
import GameShelf.ms_usuario.dto.UsuarioResponseDTO;
import GameShelf.ms_usuario.dto.UsuarioUpdateDTO;

public interface UsuarioService {

    UsuarioResponseDTO crearUsuario(UsuarioRequestDTO usuarioRequestDTO);

    List<UsuarioResponseDTO> listarUsuarios();

    UsuarioResponseDTO buscarPorId(Long id);

    UsuarioResponseDTO actualizarUsuario(Long id, UsuarioUpdateDTO usuarioUpdateDTO);

    void eliminarUsuario(Long id);

    List<UsuarioResponseDTO> buscarPorRol(String rol);

    List<UsuarioResponseDTO> buscarPorNombre(String usuario);
}
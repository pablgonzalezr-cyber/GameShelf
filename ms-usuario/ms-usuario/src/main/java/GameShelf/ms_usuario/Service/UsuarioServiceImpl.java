package GameShelf.ms_usuario.Service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import GameShelf.ms_usuario.dto.UsuarioRequestDTO;
import GameShelf.ms_usuario.dto.UsuarioResponseDTO;
import GameShelf.ms_usuario.model.UsuarioModel;
import GameShelf.ms_usuario.repository.UsuarioRepository;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioServiceImpl(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public UsuarioResponseDTO crearUsuario(UsuarioRequestDTO usuarioRequestDTO) {

        log.info("Creando usuario: {}", usuarioRequestDTO.getUsuario());

        if (usuarioRepository.existsByUsuario(usuarioRequestDTO.getUsuario())) {
            throw new RuntimeException("El nombre de usuario ya existe");
        }

        if (usuarioRepository.existsByCorreo(usuarioRequestDTO.getCorreo())) {
            throw new RuntimeException("El correo ya está registrado");
        }

        UsuarioModel usuario = new UsuarioModel();
        usuario.setUsuario(usuarioRequestDTO.getUsuario());
        usuario.setContrasena(usuarioRequestDTO.getContrasena());
        usuario.setCorreo(usuarioRequestDTO.getCorreo());

        if (usuarioRequestDTO.getRol() == null || usuarioRequestDTO.getRol().isEmpty()) {
            usuario.setRol("CLIENTE");
        } else {
            usuario.setRol(usuarioRequestDTO.getRol());
        }

        UsuarioModel usuarioGuardado = usuarioRepository.save(usuario);

        log.info("Usuario creado con ID: {}", usuarioGuardado.getId());

        return convertirAResponseDTO(usuarioGuardado);
    }

    @Override
    public List<UsuarioResponseDTO> listarUsuarios() {

        log.info("Listando usuarios");

        List<UsuarioModel> usuarios = usuarioRepository.findAll();
        List<UsuarioResponseDTO> respuesta = new ArrayList<>();

        for (UsuarioModel usuario : usuarios) {
            respuesta.add(convertirAResponseDTO(usuario));
        }

        return respuesta;
    }

    @Override
    public UsuarioResponseDTO buscarPorId(Long id) {

        log.info("Buscando usuario con ID: {}", id);

        UsuarioModel usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        return convertirAResponseDTO(usuario);
    }

    @Override
    public UsuarioResponseDTO actualizarUsuario(Long id, UsuarioRequestDTO usuarioRequestDTO) {

        log.info("Actualizando usuario con ID: {}", id);

        UsuarioModel usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        usuario.setUsuario(usuarioRequestDTO.getUsuario());
        usuario.setCorreo(usuarioRequestDTO.getCorreo());

        if (usuarioRequestDTO.getContrasena() != null && !usuarioRequestDTO.getContrasena().isEmpty()) {
            usuario.setContrasena(usuarioRequestDTO.getContrasena());
        }

        if (usuarioRequestDTO.getRol() != null && !usuarioRequestDTO.getRol().isEmpty()) {
            usuario.setRol(usuarioRequestDTO.getRol());
        }

        UsuarioModel usuarioActualizado = usuarioRepository.save(usuario);

        log.info("Usuario actualizado con ID: {}", usuarioActualizado.getId());

        return convertirAResponseDTO(usuarioActualizado);
    }

    @Override
    public void eliminarUsuario(Long id) {

        log.info("Eliminando usuario con ID: {}", id);

        UsuarioModel usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        usuarioRepository.delete(usuario);

        log.info("Usuario eliminado con ID: {}", id);
    }

    @Override
    public List<UsuarioResponseDTO> buscarPorRol(String rol) {

        log.info("Buscando usuarios por rol: {}", rol);

        List<UsuarioModel> usuarios = usuarioRepository.findByRol(rol);
        List<UsuarioResponseDTO> respuesta = new ArrayList<>();

        for (UsuarioModel usuario : usuarios) {
            respuesta.add(convertirAResponseDTO(usuario));
        }

        return respuesta;
    }

    @Override
    public List<UsuarioResponseDTO> buscarPorNombre(String usuario) {

        log.info("Buscando usuarios por nombre: {}", usuario);

        List<UsuarioModel> usuarios = usuarioRepository.findByUsuarioContainingIgnoreCase(usuario);
        List<UsuarioResponseDTO> respuesta = new ArrayList<>();

        for (UsuarioModel user : usuarios) {
            respuesta.add(convertirAResponseDTO(user));
        }

        return respuesta;
    }

    private UsuarioResponseDTO convertirAResponseDTO(UsuarioModel usuario) {
        return new UsuarioResponseDTO(
                usuario.getId(),
                usuario.getUsuario(),
                usuario.getCorreo(),
                usuario.getRol()
        );
    }
}
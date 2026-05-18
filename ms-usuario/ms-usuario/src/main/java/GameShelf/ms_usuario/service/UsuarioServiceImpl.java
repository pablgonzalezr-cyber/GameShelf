package GameShelf.ms_usuario.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import GameShelf.ms_usuario.client.RolClient;
import GameShelf.ms_usuario.dto.RolResponseDTO;
import GameShelf.ms_usuario.dto.UsuarioRequestDTO;
import GameShelf.ms_usuario.dto.UsuarioResponseDTO;
import GameShelf.ms_usuario.dto.UsuarioUpdateDTO;
import GameShelf.ms_usuario.exception.ComunicacionRolException;
import GameShelf.ms_usuario.exception.DatoDuplicadoException;
import GameShelf.ms_usuario.exception.UsuarioNoEncontradoException;
import GameShelf.ms_usuario.model.UsuarioModel;
import GameShelf.ms_usuario.repository.UsuarioRepository;
import feign.FeignException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final RolClient rolClient;
    private final PasswordEncoder passwordEncoder;

    public UsuarioServiceImpl(UsuarioRepository usuarioRepository, RolClient rolClient, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.rolClient = rolClient;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UsuarioResponseDTO crearUsuario(UsuarioRequestDTO usuarioRequestDTO) {

        log.info("Creando usuario: {}", usuarioRequestDTO.getUsuario());

        if (usuarioRepository.existsByUsuario(usuarioRequestDTO.getUsuario())) {
            log.warn("Intento de crear usuario duplicado: {}", usuarioRequestDTO.getUsuario());
            throw new DatoDuplicadoException("El nombre de usuario ya existe");
        }

        if (usuarioRepository.existsByCorreo(usuarioRequestDTO.getCorreo())) {
            log.warn("Intento de crear correo duplicado: {}", usuarioRequestDTO.getCorreo());
            throw new DatoDuplicadoException("El correo ya está registrado");
        }

        UsuarioModel usuario = new UsuarioModel();
        usuario.setUsuario(usuarioRequestDTO.getUsuario());
        usuario.setContrasena(passwordEncoder.encode(usuarioRequestDTO.getContrasena()));
        usuario.setCorreo(usuarioRequestDTO.getCorreo());
        usuario.setRol(validarRol(usuarioRequestDTO.getRol()));

        UsuarioModel usuarioGuardado = usuarioRepository.save(usuario);

        log.info("Usuario creado correctamente con ID: {}", usuarioGuardado.getId());

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

        log.info("Total de usuarios encontrados: {}", respuesta.size());

        return respuesta;
    }

    @Override
    public UsuarioResponseDTO buscarPorId(Long id) {

        log.info("Buscando usuario con ID: {}", id);

        UsuarioModel usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new UsuarioNoEncontradoException("Usuario no encontrado con ID: " + id));

        return convertirAResponseDTO(usuario);
    }

    @Override
    public UsuarioResponseDTO actualizarUsuario(Long id, UsuarioUpdateDTO usuarioUpdateDTO) {

        log.info("Actualizando usuario con ID: {}", id);

        UsuarioModel usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new UsuarioNoEncontradoException("Usuario no encontrado con ID: " + id));

        if (usuarioRepository.existsByUsuarioAndIdNot(usuarioUpdateDTO.getUsuario(), id)) {
            log.warn("Intento de actualizar con usuario duplicado: {}", usuarioUpdateDTO.getUsuario());
            throw new DatoDuplicadoException("El nombre de usuario ya existe");
        }

        if (usuarioRepository.existsByCorreoAndIdNot(usuarioUpdateDTO.getCorreo(), id)) {
            log.warn("Intento de actualizar con correo duplicado: {}", usuarioUpdateDTO.getCorreo());
            throw new DatoDuplicadoException("El correo ya está registrado");
        }

        usuario.setUsuario(usuarioUpdateDTO.getUsuario());
        usuario.setCorreo(usuarioUpdateDTO.getCorreo());

        if (usuarioUpdateDTO.getContrasena() != null && !usuarioUpdateDTO.getContrasena().isBlank()) {
            usuario.setContrasena(passwordEncoder.encode(usuarioUpdateDTO.getContrasena()));
        }

        if (usuarioUpdateDTO.getRol() != null && !usuarioUpdateDTO.getRol().isBlank()) {
            usuario.setRol(validarRol(usuarioUpdateDTO.getRol()));
        }

        UsuarioModel usuarioActualizado = usuarioRepository.save(usuario);

        log.info("Usuario actualizado correctamente con ID: {}", usuarioActualizado.getId());

        return convertirAResponseDTO(usuarioActualizado);
    }

    @Override
    public void eliminarUsuario(Long id) {

        log.info("Eliminando usuario con ID: {}", id);

        UsuarioModel usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new UsuarioNoEncontradoException("Usuario no encontrado con ID: " + id));

        usuarioRepository.delete(usuario);

        log.info("Usuario eliminado correctamente con ID: {}", id);
    }

    @Override
    public List<UsuarioResponseDTO> buscarPorRol(String rol) {

        log.info("Buscando usuarios por rol: {}", rol);

        List<UsuarioModel> usuarios = usuarioRepository.findByRol(rol);
        List<UsuarioResponseDTO> respuesta = new ArrayList<>();

        for (UsuarioModel usuario : usuarios) {
            respuesta.add(convertirAResponseDTO(usuario));
        }

        log.info("Usuarios encontrados con rol {}: {}", rol, respuesta.size());

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

        log.info("Usuarios encontrados con búsqueda {}: {}", usuario, respuesta.size());

        return respuesta;
    }

    private String validarRol(String nombreRol) {

        if (nombreRol == null || nombreRol.isBlank()) {
        nombreRol = "CLIENTE";
        }

        try {
            log.info("Validando rol con ms-roles: {}", nombreRol);

            RolResponseDTO rol = rolClient.buscarRolPorNombre(nombreRol);

            if (rol == null) {
            throw new ComunicacionRolException("El microservicio de roles no devolvió información");
            }

            if (rol.getEstado() == null || !rol.getEstado().equalsIgnoreCase("ACTIVO")) {
            throw new DatoDuplicadoException("El rol no está activo");
            }

            log.info("Rol validado correctamente: {}", rol.getNombre());

            return rol.getNombre();

        } catch (FeignException.NotFound e) {
            log.warn("Rol no encontrado en ms-roles: {}", nombreRol);
            throw new DatoDuplicadoException("El rol ingresado no existe");

        } catch (FeignException e) {
            log.error("Error de comunicación con ms-roles al validar rol: {}", nombreRol);
            throw new ComunicacionRolException("No se pudo validar el rol con ms-roles");
        }
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
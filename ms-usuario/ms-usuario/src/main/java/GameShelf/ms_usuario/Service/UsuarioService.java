package GameShelf.ms_usuario.Service;

import  java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import GameShelf.ms_usuario.model.Usuario;
import GameShelf.ms_usuario.repository.UsuarioRepository;

@Service
public class UsuarioService {
    @Autowired
    private UsuarioRepository usuarioRepository;

    public Usuario crearUsuario(Usuario usuario) {

        if (usuarioRepository.existsByUsuario(usuario.getUsuario())) {
            throw new RuntimeException("El nombre de usuario ya existe.");
        }

        if (usuarioRepository.existsByCorreo(usuario.getCorreo())) {
            throw new RuntimeException("El correo ya está registrado.");
        }

        if (usuario.getRol() == null || usuario.getRol().isEmpty()) {
            usuario.setRol("CLIENTE");
        }

        return usuarioRepository.save(usuario);
    }

    public List<Usuario> listarUsuarios() {
        return usuarioRepository.findAll();
    }

    public Usuario buscarPorId(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado."));
    }

    public Usuario actualizarUsuario(Long id, Usuario datosUsuario) {

        Usuario usuario = buscarPorId(id);

        usuario.setUsuario(datosUsuario.getUsuario());
        usuario.setCorreo(datosUsuario.getCorreo());
        usuario.setRol(datosUsuario.getRol());

        if (datosUsuario.getContrasena() != null && !datosUsuario.getContrasena().isEmpty()) {
            usuario.setContrasena(datosUsuario.getContrasena());
        }

        return usuarioRepository.save(usuario);
    }

    public void eliminarUsuario(Long id) {

        Usuario usuario = buscarPorId(id);

        usuarioRepository.delete(usuario);
    }

    public List<Usuario> buscarPorRol(String rol) {
        return usuarioRepository.findByRol(rol);
    }

    public List<Usuario> buscarPorNombre(String usuario) {
        return usuarioRepository.findByUsuarioContainingIgnoreCase(usuario);
    }
}


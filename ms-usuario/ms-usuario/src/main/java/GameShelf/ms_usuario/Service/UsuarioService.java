package GameShelf.ms_usuario.Service;

import  java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import GameShelf.ms_usuario.model.UsuarioModel;
import GameShelf.ms_usuario.repository.UsuarioRepository;


// Servicio que contiene la lógica principal del microservicio de usuarios.
// Aquí se validan datos antes de guardar, actualizar o eliminar usuarios.
@Service
public class UsuarioService {
    @Autowired
    private UsuarioRepository usuarioRepository;

    public UsuarioModel crearUsuario(UsuarioModel usuario) {

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

    public List<UsuarioModel> listarUsuarios() {
        return usuarioRepository.findAll();
    }

    public UsuarioModel buscarPorId(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado."));
    }

    public UsuarioModel actualizarUsuario(Long id, UsuarioModel datosUsuario) {

        UsuarioModel usuario = buscarPorId(id);

        usuario.setUsuario(datosUsuario.getUsuario());
        usuario.setCorreo(datosUsuario.getCorreo());
        usuario.setRol(datosUsuario.getRol());

        if (datosUsuario.getContrasena() != null && !datosUsuario.getContrasena().isEmpty()) {
            usuario.setContrasena(datosUsuario.getContrasena());
        }

        return usuarioRepository.save(usuario);
    }

    public void eliminarUsuario(Long id) {

        UsuarioModel usuario = buscarPorId(id);

        usuarioRepository.delete(usuario);
    }

    public List<UsuarioModel> buscarPorRol(String rol) {
        return usuarioRepository.findByRol(rol);
    }

    public List<UsuarioModel> buscarPorNombre(String usuario) {
        return usuarioRepository.findByUsuarioContainingIgnoreCase(usuario);
    }
}


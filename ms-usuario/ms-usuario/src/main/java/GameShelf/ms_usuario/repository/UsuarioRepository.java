package GameShelf.ms_usuario.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import GameShelf.ms_usuario.model.UsuarioModel;

@Repository
public interface UsuarioRepository extends JpaRepository<UsuarioModel, Long> {

    Optional<UsuarioModel> findByCorreo(String correo);

    Optional<UsuarioModel> findByUsuario(String usuario);

    boolean existsByCorreo(String correo);

    boolean existsByUsuario(String usuario);

    boolean existsByCorreoAndIdNot(String correo, Long id);

    boolean existsByUsuarioAndIdNot(String usuario, Long id);

    List<UsuarioModel> findByRol(String rol);

    List<UsuarioModel> findByUsuarioContainingIgnoreCase(String usuario);

    void deleteByCorreo(String correo);
}

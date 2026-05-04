package GameShelf.ms_usuario.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import GameShelf.ms_usuario.model.Usuario;


@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    // Buscar por email (Fundamental para login o recuperación de contraseña)
    Optional<Usuario> findByCorreo (String correo);

    // Buscar por nombre de usuario (Para login)
    Optional<Usuario> findByUsuario (String usuario);

    // 2. VALIDACIONES DE EXISTENCIA (Para el proceso de Registro/Sign Up)
    // Estos devuelven true o false. Es más rápido que traer todo el objeto.
    
    boolean existsByCorreo(String correo);
    boolean existsByUsuario(String usuario);


    // 3. BÚSQUEDAS DE LISTAS (Para administración)
    
    // Buscar todos los usuarios que tienen un rol específico (ej: todos los "ADMIN")
    List<Usuario> findByRol(String rol);

    // Buscar usuarios cuyo nombre contenga una palabra (para buscadores con lupa)
    List<Usuario> findByUsuarioContainingIgnoreCase(String username);


    // 4. BORRADO SEGURO
    
    // Borrar por email en lugar de solo por ID
    void deleteByCorreo(String correo);

}

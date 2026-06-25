package GameShelf.ms_usuario.service;

import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

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
import feign.Request;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceImplTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private RolClient rolClient;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UsuarioServiceImpl usuarioService;

    private UsuarioRequestDTO requestDTO;
    private UsuarioUpdateDTO updateDTO;
    private UsuarioModel usuarioModel;
    private RolResponseDTO rolActivo;

    @BeforeEach
    void setUp() {
        requestDTO = new UsuarioRequestDTO();
        requestDTO.setUsuario("pablo");
        requestDTO.setContrasena("1234");
        requestDTO.setCorreo("pablo@gmail.com");
        requestDTO.setRol("CLIENTE");

        updateDTO = new UsuarioUpdateDTO();
        updateDTO.setUsuario("pablo_actualizado");
        updateDTO.setContrasena("12345");
        updateDTO.setCorreo("pablo.actualizado@gmail.com");
        updateDTO.setRol("ADMINISTRADOR");

        usuarioModel = new UsuarioModel();
        usuarioModel.setId(1L);
        usuarioModel.setUsuario("pablo");
        usuarioModel.setContrasena("contrasena-cifrada");
        usuarioModel.setCorreo("pablo@gmail.com");
        usuarioModel.setRol("CLIENTE");

        rolActivo = new RolResponseDTO();
        rolActivo.setId(1L);
        rolActivo.setNombre("CLIENTE");
        rolActivo.setDescripcion("Usuario cliente");
        rolActivo.setEstado("ACTIVO");
    }

    @Test
    void crearUsuario_CuandoDatosValidos_GuardaUsuarioConContrasenaCifradaYRolValidado() {
        // Given
        when(usuarioRepository.existsByUsuario("pablo")).thenReturn(false);
        when(usuarioRepository.existsByCorreo("pablo@gmail.com")).thenReturn(false);
        when(rolClient.buscarRolPorNombre("CLIENTE")).thenReturn(rolActivo);
        when(passwordEncoder.encode("1234")).thenReturn("hash-1234");
        when(usuarioRepository.save(any(UsuarioModel.class))).thenAnswer(invocation -> {
            UsuarioModel usuarioGuardado = invocation.getArgument(0);
            usuarioGuardado.setId(1L);
            return usuarioGuardado;
        });

        // When
        UsuarioResponseDTO respuesta = usuarioService.crearUsuario(requestDTO);

        // Then
        assertNotNull(respuesta);
        assertEquals(1L, respuesta.getId());
        assertEquals("pablo", respuesta.getUsuario());
        assertEquals("pablo@gmail.com", respuesta.getCorreo());
        assertEquals("CLIENTE", respuesta.getRol());

        ArgumentCaptor<UsuarioModel> captor = ArgumentCaptor.forClass(UsuarioModel.class);
        verify(usuarioRepository).save(captor.capture());

        UsuarioModel usuarioGuardado = captor.getValue();
        assertEquals("pablo", usuarioGuardado.getUsuario());
        assertEquals("pablo@gmail.com", usuarioGuardado.getCorreo());
        assertEquals("CLIENTE", usuarioGuardado.getRol());
        assertEquals("hash-1234", usuarioGuardado.getContrasena());
        assertNotEquals("1234", usuarioGuardado.getContrasena());
    }

    @Test
    void crearUsuario_CuandoRolNoSeEnvia_AsignaClientePorDefecto() {
        // Given
        requestDTO.setRol(null);

        when(usuarioRepository.existsByUsuario("pablo")).thenReturn(false);
        when(usuarioRepository.existsByCorreo("pablo@gmail.com")).thenReturn(false);
        when(rolClient.buscarRolPorNombre("CLIENTE")).thenReturn(rolActivo);
        when(passwordEncoder.encode("1234")).thenReturn("hash-1234");
        when(usuarioRepository.save(any(UsuarioModel.class))).thenAnswer(invocation -> {
            UsuarioModel usuarioGuardado = invocation.getArgument(0);
            usuarioGuardado.setId(1L);
            return usuarioGuardado;
        });

        // When
        UsuarioResponseDTO respuesta = usuarioService.crearUsuario(requestDTO);

        // Then
        assertEquals("CLIENTE", respuesta.getRol());
        verify(rolClient).buscarRolPorNombre("CLIENTE");
    }

    @Test
    void crearUsuario_CuandoUsuarioYaExiste_LanzaDatoDuplicadoException() {
        // Given
        when(usuarioRepository.existsByUsuario("pablo")).thenReturn(true);

        // When / Then
        DatoDuplicadoException exception = assertThrows(
                DatoDuplicadoException.class,
                () -> usuarioService.crearUsuario(requestDTO)
        );

        assertEquals("El nombre de usuario ya existe", exception.getMessage());
        verify(usuarioRepository, never()).save(any(UsuarioModel.class));
        verify(rolClient, never()).buscarRolPorNombre(any());
    }

    @Test
    void crearUsuario_CuandoCorreoYaExiste_LanzaDatoDuplicadoException() {
        // Given
        when(usuarioRepository.existsByUsuario("pablo")).thenReturn(false);
        when(usuarioRepository.existsByCorreo("pablo@gmail.com")).thenReturn(true);

        // When / Then
        DatoDuplicadoException exception = assertThrows(
                DatoDuplicadoException.class,
                () -> usuarioService.crearUsuario(requestDTO)
        );

        assertEquals("El correo ya está registrado", exception.getMessage());
        verify(usuarioRepository, never()).save(any(UsuarioModel.class));
        verify(rolClient, never()).buscarRolPorNombre(any());
    }

    @Test
    void crearUsuario_CuandoRolNoExiste_LanzaDatoDuplicadoException() {
        // Given
        when(usuarioRepository.existsByUsuario("pablo")).thenReturn(false);
        when(usuarioRepository.existsByCorreo("pablo@gmail.com")).thenReturn(false);
        when(rolClient.buscarRolPorNombre("CLIENTE")).thenThrow(crearFeignNotFound());

        // When / Then
        DatoDuplicadoException exception = assertThrows(
                DatoDuplicadoException.class,
                () -> usuarioService.crearUsuario(requestDTO)
        );

        assertEquals("El rol ingresado no existe", exception.getMessage());
        verify(usuarioRepository, never()).save(any(UsuarioModel.class));
    }

    @Test
    void crearUsuario_CuandoRolEstaInactivo_LanzaDatoDuplicadoException() {
        // Given
        RolResponseDTO rolInactivo = new RolResponseDTO(1L, "CLIENTE", "Usuario cliente", "INACTIVO");

        when(usuarioRepository.existsByUsuario("pablo")).thenReturn(false);
        when(usuarioRepository.existsByCorreo("pablo@gmail.com")).thenReturn(false);
        when(rolClient.buscarRolPorNombre("CLIENTE")).thenReturn(rolInactivo);

        // When / Then
        DatoDuplicadoException exception = assertThrows(
                DatoDuplicadoException.class,
                () -> usuarioService.crearUsuario(requestDTO)
        );

        assertEquals("El rol no está activo", exception.getMessage());
        verify(usuarioRepository, never()).save(any(UsuarioModel.class));
    }

    @Test
    void crearUsuario_CuandoRolClientFalla_LanzaComunicacionRolException() {
        // Given
        when(usuarioRepository.existsByUsuario("pablo")).thenReturn(false);
        when(usuarioRepository.existsByCorreo("pablo@gmail.com")).thenReturn(false);
        when(rolClient.buscarRolPorNombre("CLIENTE")).thenThrow(crearFeignServiceUnavailable());

        // When / Then
        ComunicacionRolException exception = assertThrows(
                ComunicacionRolException.class,
                () -> usuarioService.crearUsuario(requestDTO)
        );

        assertEquals("No se pudo validar el rol con ms-roles", exception.getMessage());
        verify(usuarioRepository, never()).save(any(UsuarioModel.class));
    }

    @Test
    void listarUsuarios_CuandoExistenUsuarios_RetornaLista() {
        // Given
        UsuarioModel usuarioDos = new UsuarioModel(2L, "gabriel", "hash", "gabriel@gmail.com", "ADMINISTRADOR");
        when(usuarioRepository.findAll()).thenReturn(List.of(usuarioModel, usuarioDos));

        // When
        List<UsuarioResponseDTO> respuesta = usuarioService.listarUsuarios();

        // Then
        assertEquals(2, respuesta.size());
        assertEquals("pablo", respuesta.get(0).getUsuario());
        assertEquals("gabriel", respuesta.get(1).getUsuario());
        verify(usuarioRepository).findAll();
    }

    @Test
    void buscarPorId_CuandoUsuarioExiste_RetornaUsuario() {
        // Given
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioModel));

        // When
        UsuarioResponseDTO respuesta = usuarioService.buscarPorId(1L);

        // Then
        assertEquals(1L, respuesta.getId());
        assertEquals("pablo", respuesta.getUsuario());
        assertEquals("CLIENTE", respuesta.getRol());
        verify(usuarioRepository).findById(1L);
    }

    @Test
    void buscarPorId_CuandoUsuarioNoExiste_LanzaUsuarioNoEncontradoException() {
        // Given
        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());

        // When / Then
        UsuarioNoEncontradoException exception = assertThrows(
                UsuarioNoEncontradoException.class,
                () -> usuarioService.buscarPorId(99L)
        );

        assertEquals("Usuario no encontrado con ID: 99", exception.getMessage());
        verify(usuarioRepository).findById(99L);
    }

    @Test
    void actualizarUsuario_CuandoDatosValidos_ActualizaUsuarioYCifraNuevaContrasena() {
        // Given
        RolResponseDTO rolAdministrador = new RolResponseDTO(2L, "ADMINISTRADOR", "Administrador", "ACTIVO");

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioModel));
        when(usuarioRepository.existsByUsuarioAndIdNot("pablo_actualizado", 1L)).thenReturn(false);
        when(usuarioRepository.existsByCorreoAndIdNot("pablo.actualizado@gmail.com", 1L)).thenReturn(false);
        when(passwordEncoder.encode("12345")).thenReturn("hash-12345");
        when(rolClient.buscarRolPorNombre("ADMINISTRADOR")).thenReturn(rolAdministrador);
        when(usuarioRepository.save(any(UsuarioModel.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        UsuarioResponseDTO respuesta = usuarioService.actualizarUsuario(1L, updateDTO);

        // Then
        assertEquals(1L, respuesta.getId());
        assertEquals("pablo_actualizado", respuesta.getUsuario());
        assertEquals("pablo.actualizado@gmail.com", respuesta.getCorreo());
        assertEquals("ADMINISTRADOR", respuesta.getRol());
        assertEquals("hash-12345", usuarioModel.getContrasena());

        verify(usuarioRepository).save(usuarioModel);
    }

    @Test
    void actualizarUsuario_CuandoContrasenaVacia_MantieneContrasenaActual() {
        // Given
        updateDTO.setContrasena(" ");

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioModel));
        when(usuarioRepository.existsByUsuarioAndIdNot("pablo_actualizado", 1L)).thenReturn(false);
        when(usuarioRepository.existsByCorreoAndIdNot("pablo.actualizado@gmail.com", 1L)).thenReturn(false);
        when(rolClient.buscarRolPorNombre("ADMINISTRADOR"))
                .thenReturn(new RolResponseDTO(2L, "ADMINISTRADOR", "Administrador", "ACTIVO"));
        when(usuarioRepository.save(any(UsuarioModel.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        UsuarioResponseDTO respuesta = usuarioService.actualizarUsuario(1L, updateDTO);

        // Then
        assertEquals("ADMINISTRADOR", respuesta.getRol());
        assertEquals("contrasena-cifrada", usuarioModel.getContrasena());
        verify(passwordEncoder, never()).encode(any());
    }

    @Test
    void actualizarUsuario_CuandoUsuarioNoExiste_LanzaUsuarioNoEncontradoException() {
        // Given
        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());

        // When / Then
        UsuarioNoEncontradoException exception = assertThrows(
                UsuarioNoEncontradoException.class,
                () -> usuarioService.actualizarUsuario(99L, updateDTO)
        );

        assertEquals("Usuario no encontrado con ID: 99", exception.getMessage());
        verify(usuarioRepository, never()).save(any(UsuarioModel.class));
    }

    @Test
    void actualizarUsuario_CuandoNombreUsuarioDuplicado_LanzaDatoDuplicadoException() {
        // Given
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioModel));
        when(usuarioRepository.existsByUsuarioAndIdNot("pablo_actualizado", 1L)).thenReturn(true);

        // When / Then
        DatoDuplicadoException exception = assertThrows(
                DatoDuplicadoException.class,
                () -> usuarioService.actualizarUsuario(1L, updateDTO)
        );

        assertEquals("El nombre de usuario ya existe", exception.getMessage());
        verify(usuarioRepository, never()).save(any(UsuarioModel.class));
    }

    @Test
    void actualizarUsuario_CuandoCorreoDuplicado_LanzaDatoDuplicadoException() {
        // Given
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioModel));
        when(usuarioRepository.existsByUsuarioAndIdNot("pablo_actualizado", 1L)).thenReturn(false);
        when(usuarioRepository.existsByCorreoAndIdNot("pablo.actualizado@gmail.com", 1L)).thenReturn(true);

        // When / Then
        DatoDuplicadoException exception = assertThrows(
                DatoDuplicadoException.class,
                () -> usuarioService.actualizarUsuario(1L, updateDTO)
        );

        assertEquals("El correo ya está registrado", exception.getMessage());
        verify(usuarioRepository, never()).save(any(UsuarioModel.class));
    }

    @Test
    void eliminarUsuario_CuandoUsuarioExiste_EliminaUsuario() {
        // Given
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioModel));

        // When
        usuarioService.eliminarUsuario(1L);

        // Then
        verify(usuarioRepository).findById(1L);
        verify(usuarioRepository).delete(usuarioModel);
    }

    @Test
    void buscarPorRol_CuandoExistenUsuarios_RetornaLista() {
        // Given
        when(usuarioRepository.findByRol("CLIENTE")).thenReturn(List.of(usuarioModel));

        // When
        List<UsuarioResponseDTO> respuesta = usuarioService.buscarPorRol("CLIENTE");

        // Then
        assertEquals(1, respuesta.size());
        assertEquals("CLIENTE", respuesta.get(0).getRol());
        verify(usuarioRepository).findByRol("CLIENTE");
    }

    @Test
    void buscarPorNombre_CuandoExistenCoincidencias_RetornaLista() {
        // Given
        when(usuarioRepository.findByUsuarioContainingIgnoreCase("pab")).thenReturn(List.of(usuarioModel));

        // When
        List<UsuarioResponseDTO> respuesta = usuarioService.buscarPorNombre("pab");

        // Then
        assertEquals(1, respuesta.size());
        assertEquals("pablo", respuesta.get(0).getUsuario());
        verify(usuarioRepository).findByUsuarioContainingIgnoreCase("pab");
    }

    private FeignException.NotFound crearFeignNotFound() {
        Request request = crearRequestFeign();
        Map<String, Collection<String>> headers = Collections.emptyMap();

        return new FeignException.NotFound(
                "Rol no encontrado",
                request,
                null,
                headers
        );
    }

    private FeignException.ServiceUnavailable crearFeignServiceUnavailable() {
        Request request = crearRequestFeign();
        Map<String, Collection<String>> headers = Collections.emptyMap();

        return new FeignException.ServiceUnavailable(
                "Servicio no disponible",
                request,
                null,
                headers
        );
    }

    private Request crearRequestFeign() {
        return Request.create(
                Request.HttpMethod.GET,
                "/api/roles/nombre/CLIENTE",
                Map.of(),
                null,
                StandardCharsets.UTF_8,
                null
        );
    }
}
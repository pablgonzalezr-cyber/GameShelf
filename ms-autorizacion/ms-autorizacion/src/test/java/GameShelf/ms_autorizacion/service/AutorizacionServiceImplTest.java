package GameShelf.ms_autorizacion.service;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
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

import GameShelf.ms_autorizacion.client.RolClient;
import GameShelf.ms_autorizacion.client.UsuarioClient;
import GameShelf.ms_autorizacion.dto.AutorizacionRequestDTO;
import GameShelf.ms_autorizacion.dto.AutorizacionResponseDTO;
import GameShelf.ms_autorizacion.dto.UsuarioResponseDTO;
import GameShelf.ms_autorizacion.dto.ValidarAutorizacionRequestDTO;
import GameShelf.ms_autorizacion.exception.DatoInvalidoException;
import GameShelf.ms_autorizacion.exception.RecursoNoEncontradoException;
import GameShelf.ms_autorizacion.model.AutorizacionModel;
import GameShelf.ms_autorizacion.repository.AutorizacionRepository;

@ExtendWith(MockitoExtension.class)
class AutorizacionServiceImplTest {

    @Mock
    private AutorizacionRepository autorizacionRepository;

    @Mock
    private UsuarioClient usuarioClient;

    @Mock
    private RolClient rolClient;

    @InjectMocks
    private AutorizacionServiceImpl autorizacionService;

    private AutorizacionRequestDTO requestDTO;
    private AutorizacionModel autorizacionModel;
    private UsuarioResponseDTO usuarioActivo;

    @BeforeEach
    void setUp() {
        requestDTO = new AutorizacionRequestDTO();
        requestDTO.setUsuarioId(1L);
        requestDTO.setRol(" administrador ");
        requestDTO.setModulo(" prestamos ");
        requestDTO.setPermiso(" gestionar_prestamos ");
        requestDTO.setEstado(" activo ");

        autorizacionModel = new AutorizacionModel();
        autorizacionModel.setId(1L);
        autorizacionModel.setUsuarioId(1L);
        autorizacionModel.setRol("ADMINISTRADOR");
        autorizacionModel.setModulo("PRESTAMOS");
        autorizacionModel.setPermiso("GESTIONAR_PRESTAMOS");
        autorizacionModel.setEstado("ACTIVO");

        usuarioActivo = new UsuarioResponseDTO();
        usuarioActivo.setId(1L);
        usuarioActivo.setUsuario("pablo");
        usuarioActivo.setCorreo("pablo@gmail.com");
        usuarioActivo.setRol("ADMINISTRADOR");
        usuarioActivo.setEstado("ACTIVO");
    }

    @Test
    void crearAutorizacion_CuandoDatosValidos_GuardaAutorizacionNormalizada() {
        // Given
        when(usuarioClient.obtenerUsuarioPorId(1L)).thenReturn(usuarioActivo);
        when(rolClient.validarRol("ADMINISTRADOR")).thenReturn(true);
        when(autorizacionRepository.save(any(AutorizacionModel.class))).thenAnswer(invocation -> {
            AutorizacionModel autorizacionGuardada = invocation.getArgument(0);
            autorizacionGuardada.setId(1L);
            return autorizacionGuardada;
        });

        // When
        AutorizacionResponseDTO respuesta = autorizacionService.crearAutorizacion(requestDTO);

        // Then
        assertNotNull(respuesta);
        assertEquals(1L, respuesta.getId());
        assertEquals(1L, respuesta.getUsuarioId());
        assertEquals("ADMINISTRADOR", respuesta.getRol());
        assertEquals("PRESTAMOS", respuesta.getModulo());
        assertEquals("GESTIONAR_PRESTAMOS", respuesta.getPermiso());
        assertEquals("ACTIVO", respuesta.getEstado());

        ArgumentCaptor<AutorizacionModel> captor = ArgumentCaptor.forClass(AutorizacionModel.class);
        verify(autorizacionRepository).save(captor.capture());

        AutorizacionModel autorizacionEnviadaAGuardar = captor.getValue();
        assertEquals("ADMINISTRADOR", autorizacionEnviadaAGuardar.getRol());
        assertEquals("PRESTAMOS", autorizacionEnviadaAGuardar.getModulo());
        assertEquals("GESTIONAR_PRESTAMOS", autorizacionEnviadaAGuardar.getPermiso());
        assertEquals("ACTIVO", autorizacionEnviadaAGuardar.getEstado());
    }

    @Test
    void crearAutorizacion_CuandoUsuarioNoExiste_LanzaDatoInvalidoException() {
        // Given
        when(usuarioClient.obtenerUsuarioPorId(1L)).thenReturn(null);

        // When / Then
        DatoInvalidoException exception = assertThrows(
                DatoInvalidoException.class,
                () -> autorizacionService.crearAutorizacion(requestDTO)
        );

        assertEquals("El usuario no existe", exception.getMessage());
        verify(rolClient, never()).validarRol(any());
        verify(autorizacionRepository, never()).save(any(AutorizacionModel.class));
    }

    @Test
    void crearAutorizacion_CuandoUsuarioClientFalla_LanzaDatoInvalidoException() {
        // Given
        when(usuarioClient.obtenerUsuarioPorId(1L)).thenThrow(new RuntimeException("ms-usuario caído"));

        // When / Then
        DatoInvalidoException exception = assertThrows(
                DatoInvalidoException.class,
                () -> autorizacionService.crearAutorizacion(requestDTO)
        );

        assertEquals("El usuario no existe", exception.getMessage());
        verify(rolClient, never()).validarRol(any());
        verify(autorizacionRepository, never()).save(any(AutorizacionModel.class));
    }

    @Test
    void crearAutorizacion_CuandoUsuarioEstaInactivo_LanzaDatoInvalidoException() {
        // Given
        usuarioActivo.setEstado("INACTIVO");
        when(usuarioClient.obtenerUsuarioPorId(1L)).thenReturn(usuarioActivo);

        // When / Then
        DatoInvalidoException exception = assertThrows(
                DatoInvalidoException.class,
                () -> autorizacionService.crearAutorizacion(requestDTO)
        );

        assertEquals("El usuario no está activo", exception.getMessage());
        verify(rolClient, never()).validarRol(any());
        verify(autorizacionRepository, never()).save(any(AutorizacionModel.class));
    }

    @Test
    void crearAutorizacion_CuandoRolNoExiste_LanzaDatoInvalidoException() {
        // Given
        when(usuarioClient.obtenerUsuarioPorId(1L)).thenReturn(usuarioActivo);
        when(rolClient.validarRol("ADMINISTRADOR")).thenReturn(false);

        // When / Then
        DatoInvalidoException exception = assertThrows(
                DatoInvalidoException.class,
                () -> autorizacionService.crearAutorizacion(requestDTO)
        );

        assertEquals("El rol no existe", exception.getMessage());
        verify(autorizacionRepository, never()).save(any(AutorizacionModel.class));
    }

    @Test
    void crearAutorizacion_CuandoRolClientFalla_LanzaDatoInvalidoException() {
        // Given
        when(usuarioClient.obtenerUsuarioPorId(1L)).thenReturn(usuarioActivo);
        when(rolClient.validarRol("ADMINISTRADOR")).thenThrow(new RuntimeException("ms-roles caído"));

        // When / Then
        DatoInvalidoException exception = assertThrows(
                DatoInvalidoException.class,
                () -> autorizacionService.crearAutorizacion(requestDTO)
        );

        assertEquals("No se pudo validar el rol en ms-roles", exception.getMessage());
        verify(autorizacionRepository, never()).save(any(AutorizacionModel.class));
    }

    @Test
    void crearAutorizacion_CuandoModuloInvalido_LanzaDatoInvalidoException() {
        // Given
        requestDTO.setModulo("BODEGA");
        when(usuarioClient.obtenerUsuarioPorId(1L)).thenReturn(usuarioActivo);
        when(rolClient.validarRol("ADMINISTRADOR")).thenReturn(true);

        // When / Then
        DatoInvalidoException exception = assertThrows(
                DatoInvalidoException.class,
                () -> autorizacionService.crearAutorizacion(requestDTO)
        );

        assertEquals("El módulo debe ser CATALOGO, PRESTAMOS, RESERVAS, MULTAS o SISTEMA", exception.getMessage());
        verify(autorizacionRepository, never()).save(any(AutorizacionModel.class));
    }

    @Test
    void crearAutorizacion_CuandoPermisoInvalido_LanzaDatoInvalidoException() {
        // Given
        requestDTO.setPermiso("BORRAR_TODO");
        when(usuarioClient.obtenerUsuarioPorId(1L)).thenReturn(usuarioActivo);
        when(rolClient.validarRol("ADMINISTRADOR")).thenReturn(true);

        // When / Then
        DatoInvalidoException exception = assertThrows(
                DatoInvalidoException.class,
                () -> autorizacionService.crearAutorizacion(requestDTO)
        );

        assertEquals("El permiso debe ser TOTAL, ADMIN, VER_CATALOGO, GESTIONAR_MULTAS, GESTIONAR_RESERVAS, GESTIONAR_PRESTAMOS o TESTEADOR", exception.getMessage());
        verify(autorizacionRepository, never()).save(any(AutorizacionModel.class));
    }

    @Test
    void crearAutorizacion_CuandoEstadoInvalido_LanzaDatoInvalidoException() {
        // Given
        requestDTO.setEstado("BLOQUEADO");
        when(usuarioClient.obtenerUsuarioPorId(1L)).thenReturn(usuarioActivo);
        when(rolClient.validarRol("ADMINISTRADOR")).thenReturn(true);

        // When / Then
        DatoInvalidoException exception = assertThrows(
                DatoInvalidoException.class,
                () -> autorizacionService.crearAutorizacion(requestDTO)
        );

        assertEquals("El estado debe ser ACTIVO o INACTIVO", exception.getMessage());
        verify(autorizacionRepository, never()).save(any(AutorizacionModel.class));
    }

    @Test
    void listarAutorizaciones_CuandoExistenAutorizaciones_RetornaLista() {
        // Given
        AutorizacionModel autorizacionDos = new AutorizacionModel(
                2L,
                3L,
                "CLIENTE",
                "CATALOGO",
                "VER_CATALOGO",
                "ACTIVO"
        );

        when(autorizacionRepository.findAll()).thenReturn(List.of(autorizacionModel, autorizacionDos));

        // When
        List<AutorizacionResponseDTO> respuesta = autorizacionService.listarAutorizaciones();

        // Then
        assertEquals(2, respuesta.size());
        assertEquals("ADMINISTRADOR", respuesta.get(0).getRol());
        assertEquals("CLIENTE", respuesta.get(1).getRol());
        verify(autorizacionRepository).findAll();
    }

    @Test
    void obtenerAutorizacionPorId_CuandoExiste_RetornaAutorizacion() {
        // Given
        when(autorizacionRepository.findById(1L)).thenReturn(Optional.of(autorizacionModel));

        // When
        AutorizacionResponseDTO respuesta = autorizacionService.obtenerAutorizacionPorId(1L);

        // Then
        assertEquals(1L, respuesta.getId());
        assertEquals("PRESTAMOS", respuesta.getModulo());
        verify(autorizacionRepository).findById(1L);
    }

    @Test
    void obtenerAutorizacionPorId_CuandoNoExiste_LanzaRecursoNoEncontradoException() {
        // Given
        when(autorizacionRepository.findById(99L)).thenReturn(Optional.empty());

        // When / Then
        RecursoNoEncontradoException exception = assertThrows(
                RecursoNoEncontradoException.class,
                () -> autorizacionService.obtenerAutorizacionPorId(99L)
        );

        assertEquals("Autorización no encontrada", exception.getMessage());
        verify(autorizacionRepository).findById(99L);
    }

    @Test
    void listarPorUsuario_CuandoUsuarioValido_RetornaLista() {
        // Given
        when(usuarioClient.obtenerUsuarioPorId(1L)).thenReturn(usuarioActivo);
        when(autorizacionRepository.findByUsuarioId(1L)).thenReturn(List.of(autorizacionModel));

        // When
        List<AutorizacionResponseDTO> respuesta = autorizacionService.listarPorUsuario(1L);

        // Then
        assertEquals(1, respuesta.size());
        assertEquals(1L, respuesta.get(0).getUsuarioId());
        verify(autorizacionRepository).findByUsuarioId(1L);
    }

    @Test
    void actualizarAutorizacion_CuandoDatosValidos_ActualizaAutorizacion() {
        // Given
        AutorizacionRequestDTO actualizarDTO = new AutorizacionRequestDTO();
        actualizarDTO.setUsuarioId(1L);
        actualizarDTO.setRol("cliente");
        actualizarDTO.setModulo("catalogo");
        actualizarDTO.setPermiso("ver_catalogo");
        actualizarDTO.setEstado("inactivo");

        when(autorizacionRepository.findById(1L)).thenReturn(Optional.of(autorizacionModel));
        when(usuarioClient.obtenerUsuarioPorId(1L)).thenReturn(usuarioActivo);
        when(rolClient.validarRol("CLIENTE")).thenReturn(true);
        when(autorizacionRepository.save(any(AutorizacionModel.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        AutorizacionResponseDTO respuesta = autorizacionService.actualizarAutorizacion(1L, actualizarDTO);

        // Then
        assertEquals(1L, respuesta.getId());
        assertEquals("CLIENTE", respuesta.getRol());
        assertEquals("CATALOGO", respuesta.getModulo());
        assertEquals("VER_CATALOGO", respuesta.getPermiso());
        assertEquals("INACTIVO", respuesta.getEstado());
        verify(autorizacionRepository).save(autorizacionModel);
    }

    @Test
    void eliminarAutorizacion_CuandoEstaActiva_CambiaEstadoAInactivo() {
        // Given
        when(autorizacionRepository.findById(1L)).thenReturn(Optional.of(autorizacionModel));
        when(autorizacionRepository.save(any(AutorizacionModel.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        autorizacionService.eliminarAutorizacion(1L);

        // Then
        assertEquals("INACTIVO", autorizacionModel.getEstado());
        verify(autorizacionRepository).save(autorizacionModel);
    }

    @Test
    void eliminarAutorizacion_CuandoYaEstaInactiva_LanzaDatoInvalidoException() {
        // Given
        autorizacionModel.setEstado("INACTIVO");
        when(autorizacionRepository.findById(1L)).thenReturn(Optional.of(autorizacionModel));

        // When / Then
        DatoInvalidoException exception = assertThrows(
                DatoInvalidoException.class,
                () -> autorizacionService.eliminarAutorizacion(1L)
        );

        assertEquals("La autorización ya está inactiva", exception.getMessage());
        verify(autorizacionRepository, never()).save(any(AutorizacionModel.class));
    }

    @Test
    void validarAutorizacion_CuandoTienePermisoExacto_RetornaTrue() {
        // Given
        ValidarAutorizacionRequestDTO validarDTO = crearValidarDTO("prestamos", "gestionar_prestamos");

        when(usuarioClient.obtenerUsuarioPorId(1L)).thenReturn(usuarioActivo);
        when(autorizacionRepository.existsByUsuarioIdAndModuloIgnoreCaseAndPermisoIgnoreCaseAndEstadoIgnoreCase(
                1L,
                "PRESTAMOS",
                "GESTIONAR_PRESTAMOS",
                "ACTIVO"
        )).thenReturn(true);

        // When
        boolean respuesta = autorizacionService.validarAutorizacion(validarDTO);

        // Then
        assertTrue(respuesta);
    }

    @Test
    void validarAutorizacion_CuandoTienePermisoTotal_RetornaTrue() {
        // Given
        ValidarAutorizacionRequestDTO validarDTO = new ValidarAutorizacionRequestDTO();
        validarDTO.setUsuarioId(1L);
        validarDTO.setModulo("PRESTAMOS");
        validarDTO.setPermiso("GESTIONAR_PRESTAMOS");

        when(usuarioClient.obtenerUsuarioPorId(1L)).thenReturn(usuarioActivo);

        when(autorizacionRepository.existsByUsuarioIdAndModuloIgnoreCaseAndPermisoIgnoreCaseAndEstadoIgnoreCase(
                1L,
                "PRESTAMOS",
                "GESTIONAR_PRESTAMOS",
                "ACTIVO"
        )).thenReturn(false);

        when(autorizacionRepository.existsByUsuarioIdAndModuloIgnoreCaseAndPermisoIgnoreCaseAndEstadoIgnoreCase(
                1L,
                "PRESTAMOS",
                "TOTAL",
                "ACTIVO"
        )).thenReturn(true);

        when(autorizacionRepository.existsByUsuarioIdAndModuloIgnoreCaseAndPermisoIgnoreCaseAndEstadoIgnoreCase(
                1L,
                "PRESTAMOS",
                "ADMIN",
                "ACTIVO"
        )).thenReturn(false);

        // When
        boolean respuesta = autorizacionService.validarAutorizacion(validarDTO);

        // Then
        assertTrue(respuesta);
    }

    @Test
    void validarAutorizacion_CuandoNoTienePermisos_RetornaFalse() {
        // Given
        ValidarAutorizacionRequestDTO validarDTO = crearValidarDTO("prestamos", "gestionar_prestamos");
        when(usuarioClient.obtenerUsuarioPorId(1L)).thenReturn(usuarioActivo);

        // When
        boolean respuesta = autorizacionService.validarAutorizacion(validarDTO);

        // Then
        assertFalse(respuesta);
    }

    @Test
    void validarAutorizacion_CuandoModuloInvalido_LanzaDatoInvalidoException() {
        // Given
        ValidarAutorizacionRequestDTO validarDTO = crearValidarDTO("BODEGA", "GESTIONAR_PRESTAMOS");
        when(usuarioClient.obtenerUsuarioPorId(1L)).thenReturn(usuarioActivo);

        // When / Then
        DatoInvalidoException exception = assertThrows(
                DatoInvalidoException.class,
                () -> autorizacionService.validarAutorizacion(validarDTO)
        );

        assertEquals("El módulo debe ser CATALOGO, PRESTAMOS, RESERVAS, MULTAS o SISTEMA", exception.getMessage());
        verify(autorizacionRepository, never()).existsByUsuarioIdAndModuloIgnoreCaseAndPermisoIgnoreCaseAndEstadoIgnoreCase(
                any(), any(), any(), any()
        );
    }

    private ValidarAutorizacionRequestDTO crearValidarDTO(String modulo, String permiso) {
        ValidarAutorizacionRequestDTO validarDTO = new ValidarAutorizacionRequestDTO();
        validarDTO.setUsuarioId(1L);
        validarDTO.setModulo(modulo);
        validarDTO.setPermiso(permiso);
        return validarDTO;
    }
}
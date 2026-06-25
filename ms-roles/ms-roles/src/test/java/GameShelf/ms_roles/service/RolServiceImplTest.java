package GameShelf.ms_roles.service;

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

import GameShelf.ms_roles.dto.RolRequestDTO;
import GameShelf.ms_roles.dto.RolResponseDTO;
import GameShelf.ms_roles.exception.DatoDuplicadoException;
import GameShelf.ms_roles.exception.RolNoEncontradoException;
import GameShelf.ms_roles.model.Rol;
import GameShelf.ms_roles.repository.RolRepository;

@ExtendWith(MockitoExtension.class)
class RolServiceImplTest {

    @Mock
    private RolRepository rolRepository;

    @InjectMocks
    private RolServiceImpl rolService;

    private RolRequestDTO requestDTO;
    private Rol rolModel;

    @BeforeEach
    void setUp() {
        requestDTO = new RolRequestDTO();
        requestDTO.setNombre(" cliente ");
        requestDTO.setDescripcion("Usuario cliente del sistema");
        requestDTO.setEstado(null);

        rolModel = new Rol();
        rolModel.setId(1L);
        rolModel.setNombre("CLIENTE");
        rolModel.setDescripcion("Usuario cliente del sistema");
        rolModel.setEstado("ACTIVO");
    }

    @Test
    void crearRol_CuandoDatosValidos_GuardaRolConNombreMayusculaYEstadoActivo() {
        // Given
        when(rolRepository.existsByNombre("CLIENTE")).thenReturn(false);
        when(rolRepository.save(any(Rol.class))).thenAnswer(invocation -> {
            Rol rolGuardado = invocation.getArgument(0);
            rolGuardado.setId(1L);
            return rolGuardado;
        });

        // When
        RolResponseDTO respuesta = rolService.crearRol(requestDTO);

        // Then
        assertNotNull(respuesta);
        assertEquals(1L, respuesta.getId());
        assertEquals("CLIENTE", respuesta.getNombre());
        assertEquals("Usuario cliente del sistema", respuesta.getDescripcion());
        assertEquals("ACTIVO", respuesta.getEstado());

        ArgumentCaptor<Rol> captor = ArgumentCaptor.forClass(Rol.class);
        verify(rolRepository).save(captor.capture());

        Rol rolEnviadoAGuardar = captor.getValue();
        assertEquals("CLIENTE", rolEnviadoAGuardar.getNombre());
        assertEquals("Usuario cliente del sistema", rolEnviadoAGuardar.getDescripcion());
        assertEquals("ACTIVO", rolEnviadoAGuardar.getEstado());
    }

    @Test
    void crearRol_CuandoNombreYaExiste_LanzaDatoDuplicadoException() {
        // Given
        when(rolRepository.existsByNombre("CLIENTE")).thenReturn(true);

        // When / Then
        DatoDuplicadoException exception = assertThrows(
                DatoDuplicadoException.class,
                () -> rolService.crearRol(requestDTO)
        );

        assertEquals("El rol ya existe", exception.getMessage());
        verify(rolRepository, never()).save(any(Rol.class));
    }

    @Test
    void crearRol_CuandoEstadoEsInvalido_LanzaDatoDuplicadoException() {
        // Given
        requestDTO.setEstado("BLOQUEADO");
        when(rolRepository.existsByNombre("CLIENTE")).thenReturn(false);

        // When / Then
        DatoDuplicadoException exception = assertThrows(
                DatoDuplicadoException.class,
                () -> rolService.crearRol(requestDTO)
        );

        assertEquals("El estado debe ser ACTIVO o INACTIVO", exception.getMessage());
        verify(rolRepository, never()).save(any(Rol.class));
    }

    @Test
    void listarRoles_CuandoExistenRoles_RetornaListaDeRoles() {
        // Given
        Rol rolDos = new Rol(2L, "ADMINISTRADOR", "Administrador del sistema", "ACTIVO");
        when(rolRepository.findAll()).thenReturn(List.of(rolModel, rolDos));

        // When
        List<RolResponseDTO> respuesta = rolService.listarRoles();

        // Then
        assertEquals(2, respuesta.size());
        assertEquals("CLIENTE", respuesta.get(0).getNombre());
        assertEquals("ADMINISTRADOR", respuesta.get(1).getNombre());
        verify(rolRepository).findAll();
    }

    @Test
    void buscarPorId_CuandoRolExiste_RetornaRol() {
        // Given
        when(rolRepository.findById(1L)).thenReturn(Optional.of(rolModel));

        // When
        RolResponseDTO respuesta = rolService.buscarPorId(1L);

        // Then
        assertEquals(1L, respuesta.getId());
        assertEquals("CLIENTE", respuesta.getNombre());
        assertEquals("ACTIVO", respuesta.getEstado());
        verify(rolRepository).findById(1L);
    }

    @Test
    void buscarPorId_CuandoRolNoExiste_LanzaRolNoEncontradoException() {
        // Given
        when(rolRepository.findById(99L)).thenReturn(Optional.empty());

        // When / Then
        RolNoEncontradoException exception = assertThrows(
                RolNoEncontradoException.class,
                () -> rolService.buscarPorId(99L)
        );

        assertEquals("Rol no encontrado con ID: 99", exception.getMessage());
        verify(rolRepository).findById(99L);
    }

    @Test
    void actualizarRol_CuandoDatosValidos_ActualizaRol() {
        // Given
        RolRequestDTO actualizarDTO = new RolRequestDTO();
        actualizarDTO.setNombre("administrador");
        actualizarDTO.setDescripcion("Administrador general del sistema");
        actualizarDTO.setEstado("INACTIVO");

        when(rolRepository.findById(1L)).thenReturn(Optional.of(rolModel));
        when(rolRepository.existsByNombreAndIdNot("ADMINISTRADOR", 1L)).thenReturn(false);
        when(rolRepository.save(any(Rol.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        RolResponseDTO respuesta = rolService.actualizarRol(1L, actualizarDTO);

        // Then
        assertEquals(1L, respuesta.getId());
        assertEquals("ADMINISTRADOR", respuesta.getNombre());
        assertEquals("Administrador general del sistema", respuesta.getDescripcion());
        assertEquals("INACTIVO", respuesta.getEstado());

        verify(rolRepository).findById(1L);
        verify(rolRepository).existsByNombreAndIdNot("ADMINISTRADOR", 1L);
        verify(rolRepository).save(rolModel);
    }

    @Test
    void actualizarRol_CuandoNuevoNombreYaExiste_LanzaDatoDuplicadoException() {
        // Given
        RolRequestDTO actualizarDTO = new RolRequestDTO();
        actualizarDTO.setNombre("ADMINISTRADOR");
        actualizarDTO.setDescripcion("Administrador general del sistema");
        actualizarDTO.setEstado("ACTIVO");

        when(rolRepository.findById(1L)).thenReturn(Optional.of(rolModel));
        when(rolRepository.existsByNombreAndIdNot("ADMINISTRADOR", 1L)).thenReturn(true);

        // When / Then
        DatoDuplicadoException exception = assertThrows(
                DatoDuplicadoException.class,
                () -> rolService.actualizarRol(1L, actualizarDTO)
        );

        assertEquals("El nombre del rol ya existe", exception.getMessage());
        verify(rolRepository, never()).save(any(Rol.class));
    }

    @Test
    void eliminarRol_CuandoRolExiste_CambiaEstadoAInactivo() {
        // Given
        when(rolRepository.findById(1L)).thenReturn(Optional.of(rolModel));
        when(rolRepository.save(any(Rol.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        rolService.eliminarRol(1L);

        // Then
        assertEquals("INACTIVO", rolModel.getEstado());
        verify(rolRepository).findById(1L);
        verify(rolRepository).save(rolModel);
    }

    @Test
    void buscarPorEstado_CuandoEstadoValido_RetornaLista() {
        // Given
        when(rolRepository.findByEstado("ACTIVO")).thenReturn(List.of(rolModel));

        // When
        List<RolResponseDTO> respuesta = rolService.buscarPorEstado("activo");

        // Then
        assertEquals(1, respuesta.size());
        assertEquals("ACTIVO", respuesta.get(0).getEstado());
        verify(rolRepository).findByEstado("ACTIVO");
    }

    @Test
    void buscarPorEstado_CuandoEstadoInvalido_LanzaDatoDuplicadoException() {
        // When / Then
        DatoDuplicadoException exception = assertThrows(
                DatoDuplicadoException.class,
                () -> rolService.buscarPorEstado("BLOQUEADO")
        );

        assertEquals("El estado debe ser ACTIVO o INACTIVO", exception.getMessage());
        verify(rolRepository, never()).findByEstado("BLOQUEADO");
    }

    @Test
    void buscarPorNombre_CuandoExistenCoincidencias_RetornaLista() {
        // Given
        when(rolRepository.findByNombreContainingIgnoreCase("cli"))
                .thenReturn(List.of(rolModel));

        // When
        List<RolResponseDTO> respuesta = rolService.buscarPorNombre("cli");

        // Then
        assertEquals(1, respuesta.size());
        assertEquals("CLIENTE", respuesta.get(0).getNombre());
        verify(rolRepository).findByNombreContainingIgnoreCase("cli");
    }

    @Test
    void buscarPorNombreExacto_CuandoExiste_NormalizaNombreYRetornaRol() {
        // Given
        when(rolRepository.findByNombre("CLIENTE")).thenReturn(Optional.of(rolModel));

        // When
        RolResponseDTO respuesta = rolService.buscarPorNombreExacto(" cliente ");

        // Then
        assertEquals("CLIENTE", respuesta.getNombre());
        verify(rolRepository).findByNombre("CLIENTE");
    }

    @Test
    void validarRolActivo_CuandoNombreEsNulo_RetornaFalse() {
        // When
        boolean respuesta = rolService.validarRolActivo(null);

        // Then
        assertFalse(respuesta);
        verify(rolRepository, never()).existsByNombreAndEstado(any(), any());
    }

    @Test
    void validarRolActivo_CuandoRolExisteYEstaActivo_RetornaTrue() {
        // Given
        when(rolRepository.existsByNombreAndEstado("CLIENTE", "ACTIVO")).thenReturn(true);

        // When
        boolean respuesta = rolService.validarRolActivo(" cliente ");

        // Then
        assertTrue(respuesta);
        verify(rolRepository).existsByNombreAndEstado("CLIENTE", "ACTIVO");
    }
}
package GameShelf.ms_categoria.service;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
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

import GameShelf.ms_categoria.dto.CategoriaRequestDTO;
import GameShelf.ms_categoria.dto.CategoriaResponseDTO;
import GameShelf.ms_categoria.exception.CategoriaNoEncontradaException;
import GameShelf.ms_categoria.exception.DatoDuplicadoException;
import GameShelf.ms_categoria.exception.DatoInvalidoException;
import GameShelf.ms_categoria.model.CategoriaModel;
import GameShelf.ms_categoria.repository.CategoriaRepository;

@ExtendWith(MockitoExtension.class)
class CategoriaServiceImplTest {

    @Mock
    private CategoriaRepository categoriaRepository;

    @InjectMocks
    private CategoriaServiceImpl categoriaService;

    private CategoriaRequestDTO requestDTO;
    private CategoriaModel categoriaModel;

    @BeforeEach
    void setUp() {
        requestDTO = new CategoriaRequestDTO();
        requestDTO.setNombre(" aventura ");
        requestDTO.setDescripcion(" Juegos de aventura ");
        requestDTO.setEstado(null);

        categoriaModel = new CategoriaModel();
        categoriaModel.setId(1L);
        categoriaModel.setNombre("AVENTURA");
        categoriaModel.setDescripcion("Juegos de aventura");
        categoriaModel.setEstado("ACTIVO");
    }

    @Test
    void crearCategoria_CuandoDatosValidos_GuardaCategoriaConNombreMayusculaYEstadoActivo() {
        // Given
        when(categoriaRepository.existsByNombre("AVENTURA")).thenReturn(false);
        when(categoriaRepository.save(any(CategoriaModel.class))).thenAnswer(invocation -> {
            CategoriaModel categoriaGuardada = invocation.getArgument(0);
            categoriaGuardada.setId(1L);
            return categoriaGuardada;
        });

        // When
        CategoriaResponseDTO respuesta = categoriaService.crearCategoria(requestDTO);

        // Then
        assertNotNull(respuesta);
        assertEquals(1L, respuesta.getId());
        assertEquals("AVENTURA", respuesta.getNombre());
        assertEquals("Juegos de aventura", respuesta.getDescripcion());
        assertEquals("ACTIVO", respuesta.getEstado());

        ArgumentCaptor<CategoriaModel> captor = ArgumentCaptor.forClass(CategoriaModel.class);
        verify(categoriaRepository).save(captor.capture());

        CategoriaModel categoriaEnviadaAGuardar = captor.getValue();
        assertEquals("AVENTURA", categoriaEnviadaAGuardar.getNombre());
        assertEquals("Juegos de aventura", categoriaEnviadaAGuardar.getDescripcion());
        assertEquals("ACTIVO", categoriaEnviadaAGuardar.getEstado());
    }

    @Test
    void crearCategoria_CuandoNombreYaExiste_LanzaDatoDuplicadoException() {
        // Given
        when(categoriaRepository.existsByNombre("AVENTURA")).thenReturn(true);

        // When / Then
        DatoDuplicadoException exception = assertThrows(
                DatoDuplicadoException.class,
                () -> categoriaService.crearCategoria(requestDTO)
        );

        assertEquals("La categoría ya existe", exception.getMessage());
        verify(categoriaRepository, never()).save(any(CategoriaModel.class));
    }

    @Test
    void crearCategoria_CuandoEstadoEsInvalido_LanzaDatoInvalidoException() {
        // Given
        requestDTO.setEstado("BLOQUEADO");
        when(categoriaRepository.existsByNombre("AVENTURA")).thenReturn(false);

        // When / Then
        DatoInvalidoException exception = assertThrows(
                DatoInvalidoException.class,
                () -> categoriaService.crearCategoria(requestDTO)
        );

        assertEquals("El estado debe ser ACTIVO o INACTIVO", exception.getMessage());
        verify(categoriaRepository, never()).save(any(CategoriaModel.class));
    }

    @Test
    void listarCategorias_CuandoExistenCategorias_RetornaListaDeCategorias() {
        // Given
        CategoriaModel categoriaDos = new CategoriaModel(2L, "ACCION", "Juegos de acción", "ACTIVO");
        when(categoriaRepository.findAll()).thenReturn(List.of(categoriaModel, categoriaDos));

        // When
        List<CategoriaResponseDTO> respuesta = categoriaService.listarCategorias();

        // Then
        assertEquals(2, respuesta.size());
        assertEquals("AVENTURA", respuesta.get(0).getNombre());
        assertEquals("ACCION", respuesta.get(1).getNombre());
        verify(categoriaRepository).findAll();
    }

    @Test
    void buscarPorId_CuandoCategoriaExiste_RetornaCategoria() {
        // Given
        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoriaModel));

        // When
        CategoriaResponseDTO respuesta = categoriaService.buscarPorId(1L);

        // Then
        assertEquals(1L, respuesta.getId());
        assertEquals("AVENTURA", respuesta.getNombre());
        assertEquals("ACTIVO", respuesta.getEstado());
        verify(categoriaRepository).findById(1L);
    }

    @Test
    void buscarPorId_CuandoCategoriaNoExiste_LanzaCategoriaNoEncontradaException() {
        // Given
        when(categoriaRepository.findById(99L)).thenReturn(Optional.empty());

        // When / Then
        CategoriaNoEncontradaException exception = assertThrows(
                CategoriaNoEncontradaException.class,
                () -> categoriaService.buscarPorId(99L)
        );

        assertEquals("Categoría no encontrada", exception.getMessage());
        verify(categoriaRepository).findById(99L);
    }

    @Test
    void buscarPorNombreExacto_CuandoExiste_NormalizaNombreYRetornaCategoria() {
        // Given
        when(categoriaRepository.findByNombre("AVENTURA")).thenReturn(Optional.of(categoriaModel));

        // When
        CategoriaResponseDTO respuesta = categoriaService.buscarPorNombreExacto(" aventura ");

        // Then
        assertEquals("AVENTURA", respuesta.getNombre());
        verify(categoriaRepository).findByNombre("AVENTURA");
    }

    @Test
    void actualizarCategoria_CuandoDatosValidos_ActualizaCategoria() {
        // Given
        CategoriaRequestDTO actualizarDTO = new CategoriaRequestDTO();
        actualizarDTO.setNombre(" rpg ");
        actualizarDTO.setDescripcion(" Juegos de rol ");
        actualizarDTO.setEstado("inactivo");

        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoriaModel));
        when(categoriaRepository.existsByNombre("RPG")).thenReturn(false);
        when(categoriaRepository.save(any(CategoriaModel.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        CategoriaResponseDTO respuesta = categoriaService.actualizarCategoria(1L, actualizarDTO);

        // Then
        assertEquals(1L, respuesta.getId());
        assertEquals("RPG", respuesta.getNombre());
        assertEquals("Juegos de rol", respuesta.getDescripcion());
        assertEquals("INACTIVO", respuesta.getEstado());

        verify(categoriaRepository).findById(1L);
        verify(categoriaRepository).existsByNombre("RPG");
        verify(categoriaRepository).save(categoriaModel);
    }

    @Test
    void actualizarCategoria_CuandoNuevoNombreYaExiste_LanzaDatoDuplicadoException() {
        // Given
        CategoriaRequestDTO actualizarDTO = new CategoriaRequestDTO();
        actualizarDTO.setNombre(" RPG ");
        actualizarDTO.setDescripcion(" Juegos de rol ");
        actualizarDTO.setEstado("ACTIVO");

        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoriaModel));
        when(categoriaRepository.existsByNombre("RPG")).thenReturn(true);

        // When / Then
        DatoDuplicadoException exception = assertThrows(
                DatoDuplicadoException.class,
                () -> categoriaService.actualizarCategoria(1L, actualizarDTO)
        );

        assertEquals("Ya existe otra categoría con ese nombre", exception.getMessage());
        verify(categoriaRepository, never()).save(any(CategoriaModel.class));
    }

    @Test
    void eliminarCategoria_CuandoCategoriaExiste_CambiaEstadoAInactivo() {
        // Given
        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoriaModel));
        when(categoriaRepository.save(any(CategoriaModel.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        categoriaService.eliminarCategoria(1L);

        // Then
        assertEquals("INACTIVO", categoriaModel.getEstado());
        verify(categoriaRepository).findById(1L);
        verify(categoriaRepository).save(categoriaModel);
    }

    @Test
    void buscarPorEstado_CuandoEstadoValidoEnMinuscula_NormalizaYRetornaLista() {
        // Given
        when(categoriaRepository.findByEstado("ACTIVO")).thenReturn(List.of(categoriaModel));

        // When
        List<CategoriaResponseDTO> respuesta = categoriaService.buscarPorEstado(" activo ");

        // Then
        assertEquals(1, respuesta.size());
        assertEquals("ACTIVO", respuesta.get(0).getEstado());
        verify(categoriaRepository).findByEstado("ACTIVO");
    }

    @Test
    void buscarPorNombre_CuandoExistenCoincidencias_RetornaLista() {
        // Given
        when(categoriaRepository.findByNombreContainingIgnoreCase("ven"))
                .thenReturn(List.of(categoriaModel));

        // When
        List<CategoriaResponseDTO> respuesta = categoriaService.buscarPorNombre("ven");

        // Then
        assertEquals(1, respuesta.size());
        assertEquals("AVENTURA", respuesta.get(0).getNombre());
        verify(categoriaRepository).findByNombreContainingIgnoreCase("ven");
    }
}
package GameShelf.ms_videojuego.service;

import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
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

import GameShelf.ms_videojuego.client.CategoriaClient;
import GameShelf.ms_videojuego.dto.CategoriaResponseDTO;
import GameShelf.ms_videojuego.dto.VideoJuegoRequestDTO;
import GameShelf.ms_videojuego.dto.VideoJuegoResponseDTO;
import GameShelf.ms_videojuego.exception.ComunicacionCategoriaException;
import GameShelf.ms_videojuego.exception.DatoDuplicadoException;
import GameShelf.ms_videojuego.exception.VideoJuegoNoEncontradoException;
import GameShelf.ms_videojuego.model.VideoJuegoModel;
import GameShelf.ms_videojuego.repository.VideoJuegoRepository;
import feign.FeignException;
import feign.Request;

@ExtendWith(MockitoExtension.class)
class VideoJuegoServiceImplTest {

    @Mock
    private VideoJuegoRepository videoJuegoRepository;

    @Mock
    private CategoriaClient categoriaClient;

    @InjectMocks
    private VideoJuegoServiceImpl videoJuegoService;

    private VideoJuegoRequestDTO requestDTO;
    private VideoJuegoModel videoJuegoModel;
    private CategoriaResponseDTO categoriaActiva;

    @BeforeEach
    void setUp() {
        requestDTO = new VideoJuegoRequestDTO();
        requestDTO.setTitulo(" Zelda ");
        requestDTO.setDescripcion("Videojuego de aventura");
        requestDTO.setPrecio(49990.0);
        requestDTO.setCategoriaId(1L);
        requestDTO.setPlataforma(" pc ");
        requestDTO.setEstado(null);

        videoJuegoModel = new VideoJuegoModel();
        videoJuegoModel.setId(1L);
        videoJuegoModel.setTitulo("Zelda");
        videoJuegoModel.setDescripcion("Videojuego de aventura");
        videoJuegoModel.setPrecio(49990.0);
        videoJuegoModel.setCategoriaId(1L);
        videoJuegoModel.setNombreCategoria("AVENTURA");
        videoJuegoModel.setPlataforma("PC");
        videoJuegoModel.setEstado("DISPONIBLE");

        categoriaActiva = new CategoriaResponseDTO();
        categoriaActiva.setId(1L);
        categoriaActiva.setNombre("AVENTURA");
        categoriaActiva.setDescripcion("Videojuegos de aventura");
        categoriaActiva.setEstado("ACTIVO");
    }

    @Test
    void crearVideoJuego_CuandoDatosValidos_GuardaVideojuegoConPlataformaMayusculaYEstadoDisponible() {
        // Given
        when(videoJuegoRepository.existsByTituloIgnoreCaseAndPlataformaIgnoreCase("Zelda", "PC")).thenReturn(false);
        when(categoriaClient.buscarCategoriaPorId(1L)).thenReturn(categoriaActiva);
        when(videoJuegoRepository.save(any(VideoJuegoModel.class))).thenAnswer(invocation -> {
            VideoJuegoModel videojuegoGuardado = invocation.getArgument(0);
            videojuegoGuardado.setId(1L);
            return videojuegoGuardado;
        });

        // When
        VideoJuegoResponseDTO respuesta = videoJuegoService.crearVideoJuego(requestDTO);

        // Then
        assertNotNull(respuesta);
        assertEquals(1L, respuesta.getId());
        assertEquals("Zelda", respuesta.getTitulo());
        assertEquals("AVENTURA", respuesta.getNombreCategoria());
        assertEquals("PC", respuesta.getPlataforma());
        assertEquals("DISPONIBLE", respuesta.getEstado());

        ArgumentCaptor<VideoJuegoModel> captor = ArgumentCaptor.forClass(VideoJuegoModel.class);
        verify(videoJuegoRepository).save(captor.capture());

        VideoJuegoModel videojuegoEnviadoAGuardar = captor.getValue();
        assertEquals("Zelda", videojuegoEnviadoAGuardar.getTitulo());
        assertEquals("PC", videojuegoEnviadoAGuardar.getPlataforma());
        assertEquals("DISPONIBLE", videojuegoEnviadoAGuardar.getEstado());
        assertEquals("AVENTURA", videojuegoEnviadoAGuardar.getNombreCategoria());
    }

    @Test
    void crearVideoJuego_CuandoVideojuegoYaExiste_LanzaDatoDuplicadoException() {
        // Given
        when(videoJuegoRepository.existsByTituloIgnoreCaseAndPlataformaIgnoreCase("Zelda", "PC")).thenReturn(true);

        // When / Then
        DatoDuplicadoException exception = assertThrows(
                DatoDuplicadoException.class,
                () -> videoJuegoService.crearVideoJuego(requestDTO)
        );

        assertEquals("El videojuego ya existe para esa plataforma", exception.getMessage());
        verify(categoriaClient, never()).buscarCategoriaPorId(any());
        verify(videoJuegoRepository, never()).save(any(VideoJuegoModel.class));
    }

    @Test
    void crearVideoJuego_CuandoCategoriaNoExiste_LanzaDatoDuplicadoException() {
        // Given
        when(videoJuegoRepository.existsByTituloIgnoreCaseAndPlataformaIgnoreCase("Zelda", "PC")).thenReturn(false);
        when(categoriaClient.buscarCategoriaPorId(1L)).thenThrow(crearFeignNotFound());

        // When / Then
        DatoDuplicadoException exception = assertThrows(
                DatoDuplicadoException.class,
                () -> videoJuegoService.crearVideoJuego(requestDTO)
        );

        assertEquals("La categoría ingresada no existe", exception.getMessage());
        verify(videoJuegoRepository, never()).save(any(VideoJuegoModel.class));
    }

    @Test
    void crearVideoJuego_CuandoCategoriaEstaInactiva_LanzaDatoDuplicadoException() {
        // Given
        categoriaActiva.setEstado("INACTIVO");
        when(videoJuegoRepository.existsByTituloIgnoreCaseAndPlataformaIgnoreCase("Zelda", "PC")).thenReturn(false);
        when(categoriaClient.buscarCategoriaPorId(1L)).thenReturn(categoriaActiva);

        // When / Then
        DatoDuplicadoException exception = assertThrows(
                DatoDuplicadoException.class,
                () -> videoJuegoService.crearVideoJuego(requestDTO)
        );

        assertEquals("La categoría no está activa", exception.getMessage());
        verify(videoJuegoRepository, never()).save(any(VideoJuegoModel.class));
    }

    @Test
    void crearVideoJuego_CuandoCategoriaClientFalla_LanzaComunicacionCategoriaException() {
        // Given
        when(videoJuegoRepository.existsByTituloIgnoreCaseAndPlataformaIgnoreCase("Zelda", "PC")).thenReturn(false);
        when(categoriaClient.buscarCategoriaPorId(1L)).thenThrow(crearFeignServiceUnavailable());

        // When / Then
        ComunicacionCategoriaException exception = assertThrows(
                ComunicacionCategoriaException.class,
                () -> videoJuegoService.crearVideoJuego(requestDTO)
        );

        assertEquals("No se pudo comunicar con ms-categoria", exception.getMessage());
        verify(videoJuegoRepository, never()).save(any(VideoJuegoModel.class));
    }

    @Test
    void crearVideoJuego_CuandoEstadoEsInvalido_LanzaDatoDuplicadoException() {
        // Given
        requestDTO.setEstado("BLOQUEADO");
        when(videoJuegoRepository.existsByTituloIgnoreCaseAndPlataformaIgnoreCase("Zelda", "PC")).thenReturn(false);
        when(categoriaClient.buscarCategoriaPorId(1L)).thenReturn(categoriaActiva);

        // When / Then
        DatoDuplicadoException exception = assertThrows(
                DatoDuplicadoException.class,
                () -> videoJuegoService.crearVideoJuego(requestDTO)
        );

        assertEquals("El estado debe ser DISPONIBLE, NO_DISPONIBLE o INACTIVO", exception.getMessage());
        verify(videoJuegoRepository, never()).save(any(VideoJuegoModel.class));
    }

    @Test
    void listarVideoJuegos_CuandoExistenVideojuegos_RetornaLista() {
        // Given
        VideoJuegoModel videojuegoDos = new VideoJuegoModel(
                2L,
                "Mario",
                "Juego de plataformas",
                39990.0,
                1L,
                "AVENTURA",
                "SWITCH",
                "DISPONIBLE"
        );

        when(videoJuegoRepository.findAll()).thenReturn(List.of(videoJuegoModel, videojuegoDos));

        // When
        List<VideoJuegoResponseDTO> respuesta = videoJuegoService.listarVideoJuegos();

        // Then
        assertEquals(2, respuesta.size());
        assertEquals("Zelda", respuesta.get(0).getTitulo());
        assertEquals("Mario", respuesta.get(1).getTitulo());
        verify(videoJuegoRepository).findAll();
    }

    @Test
    void buscarPorId_CuandoVideojuegoExiste_RetornaVideojuego() {
        // Given
        when(videoJuegoRepository.findById(1L)).thenReturn(Optional.of(videoJuegoModel));

        // When
        VideoJuegoResponseDTO respuesta = videoJuegoService.buscarPorId(1L);

        // Then
        assertEquals(1L, respuesta.getId());
        assertEquals("Zelda", respuesta.getTitulo());
        assertEquals("DISPONIBLE", respuesta.getEstado());
        verify(videoJuegoRepository).findById(1L);
    }

    @Test
    void buscarPorId_CuandoVideojuegoNoExiste_LanzaVideoJuegoNoEncontradoException() {
        // Given
        when(videoJuegoRepository.findById(99L)).thenReturn(Optional.empty());

        // When / Then
        VideoJuegoNoEncontradoException exception = assertThrows(
                VideoJuegoNoEncontradoException.class,
                () -> videoJuegoService.buscarPorId(99L)
        );

        assertEquals("Videojuego no encontrado con ID: 99", exception.getMessage());
        verify(videoJuegoRepository).findById(99L);
    }

    @Test
    void actualizarVideoJuego_CuandoDatosValidos_ActualizaVideojuego() {
        // Given
        VideoJuegoRequestDTO actualizarDTO = new VideoJuegoRequestDTO();
        actualizarDTO.setTitulo("Mario");
        actualizarDTO.setDescripcion("Juego de plataformas");
        actualizarDTO.setPrecio(39990.0);
        actualizarDTO.setCategoriaId(1L);
        actualizarDTO.setPlataforma("switch");
        actualizarDTO.setEstado("NO_DISPONIBLE");

        when(videoJuegoRepository.findById(1L)).thenReturn(Optional.of(videoJuegoModel));
        when(videoJuegoRepository.existsByTituloIgnoreCaseAndPlataformaIgnoreCaseAndIdNot("Mario", "SWITCH", 1L)).thenReturn(false);
        when(categoriaClient.buscarCategoriaPorId(1L)).thenReturn(categoriaActiva);
        when(videoJuegoRepository.save(any(VideoJuegoModel.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        VideoJuegoResponseDTO respuesta = videoJuegoService.actualizarVideoJuego(1L, actualizarDTO);

        // Then
        assertEquals(1L, respuesta.getId());
        assertEquals("Mario", respuesta.getTitulo());
        assertEquals("SWITCH", respuesta.getPlataforma());
        assertEquals("NO_DISPONIBLE", respuesta.getEstado());
        assertEquals("AVENTURA", respuesta.getNombreCategoria());

        verify(videoJuegoRepository).findById(1L);
        verify(videoJuegoRepository).save(videoJuegoModel);
    }

    @Test
    void actualizarVideoJuego_CuandoNuevoTituloYPlataformaYaExiste_LanzaDatoDuplicadoException() {
        // Given
        VideoJuegoRequestDTO actualizarDTO = new VideoJuegoRequestDTO();
        actualizarDTO.setTitulo("Mario");
        actualizarDTO.setDescripcion("Juego de plataformas");
        actualizarDTO.setPrecio(39990.0);
        actualizarDTO.setCategoriaId(1L);
        actualizarDTO.setPlataforma("switch");
        actualizarDTO.setEstado("DISPONIBLE");

        when(videoJuegoRepository.findById(1L)).thenReturn(Optional.of(videoJuegoModel));
        when(videoJuegoRepository.existsByTituloIgnoreCaseAndPlataformaIgnoreCaseAndIdNot("Mario", "SWITCH", 1L)).thenReturn(true);

        // When / Then
        DatoDuplicadoException exception = assertThrows(
                DatoDuplicadoException.class,
                () -> videoJuegoService.actualizarVideoJuego(1L, actualizarDTO)
        );

        assertEquals("El videojuego ya existe para esa plataforma", exception.getMessage());
        verify(videoJuegoRepository, never()).save(any(VideoJuegoModel.class));
    }

    @Test
    void eliminarVideoJuego_CuandoVideojuegoExiste_CambiaEstadoAInactivo() {
        // Given
        when(videoJuegoRepository.findById(1L)).thenReturn(Optional.of(videoJuegoModel));
        when(videoJuegoRepository.save(any(VideoJuegoModel.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        videoJuegoService.eliminarVideoJuego(1L);

        // Then
        assertEquals("INACTIVO", videoJuegoModel.getEstado());
        verify(videoJuegoRepository).findById(1L);
        verify(videoJuegoRepository).save(videoJuegoModel);
    }

    @Test
    void buscarPorCategoria_CuandoExistenVideojuegos_RetornaLista() {
        // Given
        when(videoJuegoRepository.findByCategoriaId(1L)).thenReturn(List.of(videoJuegoModel));

        // When
        List<VideoJuegoResponseDTO> respuesta = videoJuegoService.buscarPorCategoria(1L);

        // Then
        assertEquals(1, respuesta.size());
        assertEquals(1L, respuesta.get(0).getCategoriaId());
        verify(videoJuegoRepository).findByCategoriaId(1L);
    }

    @Test
    void buscarPorTitulo_CuandoExistenCoincidencias_RetornaLista() {
        // Given
        when(videoJuegoRepository.findByTituloContainingIgnoreCase("Zel")).thenReturn(List.of(videoJuegoModel));

        // When
        List<VideoJuegoResponseDTO> respuesta = videoJuegoService.buscarPorTitulo("Zel");

        // Then
        assertEquals(1, respuesta.size());
        assertEquals("Zelda", respuesta.get(0).getTitulo());
        verify(videoJuegoRepository).findByTituloContainingIgnoreCase("Zel");
    }

    @Test
    void buscarPorEstado_CuandoEstadoValidoEnMinuscula_NormalizaYRetornaLista() {
        // Given
        when(videoJuegoRepository.findByEstado("DISPONIBLE")).thenReturn(List.of(videoJuegoModel));

        // When
        List<VideoJuegoResponseDTO> respuesta = videoJuegoService.buscarPorEstado(" disponible ");

        // Then
        assertEquals(1, respuesta.size());
        assertEquals("DISPONIBLE", respuesta.get(0).getEstado());
        verify(videoJuegoRepository).findByEstado("DISPONIBLE");
    }

    @Test
    void buscarPorEstado_CuandoEstadoInvalido_LanzaDatoDuplicadoException() {
        // When / Then
        DatoDuplicadoException exception = assertThrows(
                DatoDuplicadoException.class,
                () -> videoJuegoService.buscarPorEstado("BLOQUEADO")
        );

        assertEquals("El estado debe ser DISPONIBLE, NO_DISPONIBLE o INACTIVO", exception.getMessage());
        verify(videoJuegoRepository, never()).findByEstado(any());
    }

    @Test
    void buscarPorPlataforma_CuandoPlataformaValida_NormalizaYRetornaLista() {
        // Given
        when(videoJuegoRepository.findByPlataforma("PC")).thenReturn(List.of(videoJuegoModel));

        // When
        List<VideoJuegoResponseDTO> respuesta = videoJuegoService.buscarPorPlataforma(" pc ");

        // Then
        assertEquals(1, respuesta.size());
        assertEquals("PC", respuesta.get(0).getPlataforma());
        verify(videoJuegoRepository).findByPlataforma("PC");
    }

    @Test
    void buscarPorPlataforma_CuandoPlataformaVacia_LanzaDatoDuplicadoException() {
        // When / Then
        DatoDuplicadoException exception = assertThrows(
                DatoDuplicadoException.class,
                () -> videoJuegoService.buscarPorPlataforma(" ")
        );

        assertEquals("La plataforma es obligatoria", exception.getMessage());
        verify(videoJuegoRepository, never()).findByPlataforma(any());
    }

    private FeignException.NotFound crearFeignNotFound() {
        Request request = crearRequestFeign();
        Map<String, Collection<String>> headers = Collections.emptyMap();

        return new FeignException.NotFound(
                "Categoría no encontrada",
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
                "/api/categorias/1",
                Map.of(),
                null,
                StandardCharsets.UTF_8,
                null
        );
    }
}
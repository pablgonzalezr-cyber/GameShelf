package GameShelf.ms_stock.service;

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

import GameShelf.ms_stock.client.VideoJuegoClient;
import GameShelf.ms_stock.dto.StockRequestDTO;
import GameShelf.ms_stock.dto.StockResponseDTO;
import GameShelf.ms_stock.dto.VideoJuegoResponseDTO;
import GameShelf.ms_stock.exception.ComunicacionVideojuegoException;
import GameShelf.ms_stock.exception.DatoDuplicadoException;
import GameShelf.ms_stock.exception.DatoInvalidoException;
import GameShelf.ms_stock.exception.StockNoEncontradoException;
import GameShelf.ms_stock.model.StockModel;
import GameShelf.ms_stock.repository.StockRepository;
import feign.FeignException;
import feign.Request;

@ExtendWith(MockitoExtension.class)
class StockServiceImplTest {

    @Mock
    private StockRepository stockRepository;

    @Mock
    private VideoJuegoClient videoJuegoClient;

    @InjectMocks
    private StockServiceImpl stockService;

    private StockRequestDTO requestDTO;
    private StockModel stockModel;
    private VideoJuegoResponseDTO videojuegoDisponible;

    @BeforeEach
    void setUp() {
        requestDTO = new StockRequestDTO();
        requestDTO.setVideojuegoId(2L);
        requestDTO.setCantidadTotal(10);
        requestDTO.setCantidadDisponible(8);
        requestDTO.setEstado(null);

        stockModel = new StockModel();
        stockModel.setId(1L);
        stockModel.setVideojuegoId(2L);
        stockModel.setCantidadTotal(10);
        stockModel.setCantidadDisponible(8);
        stockModel.setEstado("ACTIVO");

        videojuegoDisponible = new VideoJuegoResponseDTO(
                2L,
                "Zelda",
                "Videojuego de aventura",
                49990.0,
                1L,
                "AVENTURA",
                "PC",
                "DISPONIBLE"
        );
    }

    @Test
    void crearStock_CuandoDatosValidos_GuardaStockConEstadoActivo() {
        // Given
        when(videoJuegoClient.buscarVideojuegoPorId(2L)).thenReturn(videojuegoDisponible);
        when(stockRepository.existsByVideojuegoId(2L)).thenReturn(false);
        when(stockRepository.save(any(StockModel.class))).thenAnswer(invocation -> {
            StockModel stockGuardado = invocation.getArgument(0);
            stockGuardado.setId(1L);
            return stockGuardado;
        });

        // When
        StockResponseDTO respuesta = stockService.crearStock(requestDTO);

        // Then
        assertNotNull(respuesta);
        assertEquals(1L, respuesta.getId());
        assertEquals(2L, respuesta.getVideojuegoId());
        assertEquals(10, respuesta.getCantidadTotal());
        assertEquals(8, respuesta.getCantidadDisponible());
        assertEquals("ACTIVO", respuesta.getEstado());

        ArgumentCaptor<StockModel> captor = ArgumentCaptor.forClass(StockModel.class);
        verify(stockRepository).save(captor.capture());

        StockModel stockEnviadoAGuardar = captor.getValue();
        assertEquals(2L, stockEnviadoAGuardar.getVideojuegoId());
        assertEquals(10, stockEnviadoAGuardar.getCantidadTotal());
        assertEquals(8, stockEnviadoAGuardar.getCantidadDisponible());
        assertEquals("ACTIVO", stockEnviadoAGuardar.getEstado());
    }

    @Test
    void crearStock_CuandoYaExisteStockParaVideojuego_LanzaDatoDuplicadoException() {
        // Given
        when(videoJuegoClient.buscarVideojuegoPorId(2L)).thenReturn(videojuegoDisponible);
        when(stockRepository.existsByVideojuegoId(2L)).thenReturn(true);

        // When / Then
        DatoDuplicadoException exception = assertThrows(
                DatoDuplicadoException.class,
                () -> stockService.crearStock(requestDTO)
        );

        assertEquals("Ya existe stock para este videojuego", exception.getMessage());
        verify(stockRepository, never()).save(any(StockModel.class));
    }

    @Test
    void crearStock_CuandoVideojuegoNoExiste_LanzaDatoInvalidoException() {
        // Given
        when(videoJuegoClient.buscarVideojuegoPorId(2L)).thenThrow(crearFeignNotFound());

        // When / Then
        DatoInvalidoException exception = assertThrows(
                DatoInvalidoException.class,
                () -> stockService.crearStock(requestDTO)
        );

        assertEquals("El videojuego ingresado no existe", exception.getMessage());
        verify(stockRepository, never()).save(any(StockModel.class));
    }

    @Test
    void crearStock_CuandoVideojuegoNoEstaDisponible_LanzaDatoInvalidoException() {
        // Given
        videojuegoDisponible.setEstado("INACTIVO");
        when(videoJuegoClient.buscarVideojuegoPorId(2L)).thenReturn(videojuegoDisponible);

        // When / Then
        DatoInvalidoException exception = assertThrows(
                DatoInvalidoException.class,
                () -> stockService.crearStock(requestDTO)
        );

        assertEquals("El videojuego no está disponible", exception.getMessage());
        verify(stockRepository, never()).save(any(StockModel.class));
    }

    @Test
    void crearStock_CuandoVideojuegoClientFalla_LanzaComunicacionVideojuegoException() {
        // Given
        when(videoJuegoClient.buscarVideojuegoPorId(2L)).thenThrow(crearFeignServiceUnavailable());

        // When / Then
        ComunicacionVideojuegoException exception = assertThrows(
                ComunicacionVideojuegoException.class,
                () -> stockService.crearStock(requestDTO)
        );

        assertEquals("No se pudo comunicar con ms-videojuego", exception.getMessage());
        verify(stockRepository, never()).save(any(StockModel.class));
    }

    @Test
    void crearStock_CuandoCantidadDisponibleMayorQueTotal_LanzaDatoInvalidoException() {
        // Given
        requestDTO.setCantidadTotal(5);
        requestDTO.setCantidadDisponible(8);
        when(videoJuegoClient.buscarVideojuegoPorId(2L)).thenReturn(videojuegoDisponible);
        when(stockRepository.existsByVideojuegoId(2L)).thenReturn(false);

        // When / Then
        DatoInvalidoException exception = assertThrows(
                DatoInvalidoException.class,
                () -> stockService.crearStock(requestDTO)
        );

        assertEquals("La cantidad disponible no puede ser mayor que la cantidad total", exception.getMessage());
        verify(stockRepository, never()).save(any(StockModel.class));
    }

    @Test
    void crearStock_CuandoEstadoInvalido_LanzaDatoInvalidoException() {
        // Given
        requestDTO.setEstado("BLOQUEADO");
        when(videoJuegoClient.buscarVideojuegoPorId(2L)).thenReturn(videojuegoDisponible);
        when(stockRepository.existsByVideojuegoId(2L)).thenReturn(false);

        // When / Then
        DatoInvalidoException exception = assertThrows(
                DatoInvalidoException.class,
                () -> stockService.crearStock(requestDTO)
        );

        assertEquals("El estado debe ser ACTIVO o INACTIVO", exception.getMessage());
        verify(stockRepository, never()).save(any(StockModel.class));
    }

    @Test
    void listarStocks_CuandoExistenStocks_RetornaLista() {
        // Given
        StockModel stockDos = new StockModel(2L, 3L, 5, 5, "ACTIVO");
        when(stockRepository.findAll()).thenReturn(List.of(stockModel, stockDos));

        // When
        List<StockResponseDTO> respuesta = stockService.listarStocks();

        // Then
        assertEquals(2, respuesta.size());
        assertEquals(2L, respuesta.get(0).getVideojuegoId());
        assertEquals(3L, respuesta.get(1).getVideojuegoId());
        verify(stockRepository).findAll();
    }

    @Test
    void buscarPorId_CuandoStockExiste_RetornaStock() {
        // Given
        when(stockRepository.findById(1L)).thenReturn(Optional.of(stockModel));

        // When
        StockResponseDTO respuesta = stockService.buscarPorId(1L);

        // Then
        assertEquals(1L, respuesta.getId());
        assertEquals(2L, respuesta.getVideojuegoId());
        assertEquals("ACTIVO", respuesta.getEstado());
        verify(stockRepository).findById(1L);
    }

    @Test
    void buscarPorId_CuandoStockNoExiste_LanzaStockNoEncontradoException() {
        // Given
        when(stockRepository.findById(99L)).thenReturn(Optional.empty());

        // When / Then
        StockNoEncontradoException exception = assertThrows(
                StockNoEncontradoException.class,
                () -> stockService.buscarPorId(99L)
        );

        assertEquals("Stock no encontrado", exception.getMessage());
        verify(stockRepository).findById(99L);
    }

    @Test
    void buscarPorVideojuego_CuandoExisteStock_RetornaStock() {
        // Given
        when(stockRepository.findByVideojuegoId(2L)).thenReturn(Optional.of(stockModel));

        // When
        StockResponseDTO respuesta = stockService.buscarPorVideojuego(2L);

        // Then
        assertEquals(2L, respuesta.getVideojuegoId());
        verify(stockRepository).findByVideojuegoId(2L);
    }

    @Test
    void buscarPorVideojuego_CuandoNoExisteStock_LanzaStockNoEncontradoException() {
        // Given
        when(stockRepository.findByVideojuegoId(99L)).thenReturn(Optional.empty());

        // When / Then
        StockNoEncontradoException exception = assertThrows(
                StockNoEncontradoException.class,
                () -> stockService.buscarPorVideojuego(99L)
        );

        assertEquals("No hay stock para este videojuego", exception.getMessage());
        verify(stockRepository).findByVideojuegoId(99L);
    }

    @Test
    void actualizarStock_CuandoDatosValidos_ActualizaStock() {
        // Given
        StockRequestDTO actualizarDTO = new StockRequestDTO();
        actualizarDTO.setVideojuegoId(3L);
        actualizarDTO.setCantidadTotal(12);
        actualizarDTO.setCantidadDisponible(6);
        actualizarDTO.setEstado("INACTIVO");

        VideoJuegoResponseDTO videojuegoTres = new VideoJuegoResponseDTO(
                3L,
                "Mario",
                "Juego de plataformas",
                39990.0,
                1L,
                "AVENTURA",
                "SWITCH",
                "DISPONIBLE"
        );

        when(videoJuegoClient.buscarVideojuegoPorId(3L)).thenReturn(videojuegoTres);
        when(stockRepository.findById(1L)).thenReturn(Optional.of(stockModel));
        when(stockRepository.existsByVideojuegoId(3L)).thenReturn(false);
        when(stockRepository.save(any(StockModel.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        StockResponseDTO respuesta = stockService.actualizarStock(1L, actualizarDTO);

        // Then
        assertEquals(1L, respuesta.getId());
        assertEquals(3L, respuesta.getVideojuegoId());
        assertEquals(12, respuesta.getCantidadTotal());
        assertEquals(6, respuesta.getCantidadDisponible());
        assertEquals("INACTIVO", respuesta.getEstado());
        verify(stockRepository).save(stockModel);
    }

    @Test
    void actualizarStock_CuandoNuevoVideojuegoYaTieneStock_LanzaDatoDuplicadoException() {
        // Given
        StockRequestDTO actualizarDTO = new StockRequestDTO();
        actualizarDTO.setVideojuegoId(3L);
        actualizarDTO.setCantidadTotal(12);
        actualizarDTO.setCantidadDisponible(6);
        actualizarDTO.setEstado("ACTIVO");

        VideoJuegoResponseDTO videojuegoTres = new VideoJuegoResponseDTO(
                3L,
                "Mario",
                "Juego de plataformas",
                39990.0,
                1L,
                "AVENTURA",
                "SWITCH",
                "DISPONIBLE"
        );

        when(videoJuegoClient.buscarVideojuegoPorId(3L)).thenReturn(videojuegoTres);
        when(stockRepository.findById(1L)).thenReturn(Optional.of(stockModel));
        when(stockRepository.existsByVideojuegoId(3L)).thenReturn(true);

        // When / Then
        DatoDuplicadoException exception = assertThrows(
                DatoDuplicadoException.class,
                () -> stockService.actualizarStock(1L, actualizarDTO)
        );

        assertEquals("Ya existe stock para este videojuego", exception.getMessage());
        verify(stockRepository, never()).save(any(StockModel.class));
    }

    @Test
    void reducirStock_CuandoStockActivoYDisponible_DisminuyeCantidadDisponible() {
        // Given
        when(stockRepository.findByVideojuegoId(2L)).thenReturn(Optional.of(stockModel));
        when(stockRepository.save(any(StockModel.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        StockResponseDTO respuesta = stockService.reducirStock(2L);

        // Then
        assertEquals(7, respuesta.getCantidadDisponible());
        verify(stockRepository).save(stockModel);
    }

    @Test
    void reducirStock_CuandoStockInactivo_LanzaDatoInvalidoException() {
        // Given
        stockModel.setEstado("INACTIVO");
        when(stockRepository.findByVideojuegoId(2L)).thenReturn(Optional.of(stockModel));

        // When / Then
        DatoInvalidoException exception = assertThrows(
                DatoInvalidoException.class,
                () -> stockService.reducirStock(2L)
        );

        assertEquals("El stock no está activo", exception.getMessage());
        verify(stockRepository, never()).save(any(StockModel.class));
    }

    @Test
    void reducirStock_CuandoNoHayCopiasDisponibles_LanzaDatoInvalidoException() {
        // Given
        stockModel.setCantidadDisponible(0);
        when(stockRepository.findByVideojuegoId(2L)).thenReturn(Optional.of(stockModel));

        // When / Then
        DatoInvalidoException exception = assertThrows(
                DatoInvalidoException.class,
                () -> stockService.reducirStock(2L)
        );

        assertEquals("No hay copias disponibles para préstamo", exception.getMessage());
        verify(stockRepository, never()).save(any(StockModel.class));
    }

    @Test
    void aumentarStock_CuandoStockActivoYAunNoEstaCompleto_AumentaCantidadDisponible() {
        // Given
        when(stockRepository.findByVideojuegoId(2L)).thenReturn(Optional.of(stockModel));
        when(stockRepository.save(any(StockModel.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        StockResponseDTO respuesta = stockService.aumentarStock(2L);

        // Then
        assertEquals(9, respuesta.getCantidadDisponible());
        verify(stockRepository).save(stockModel);
    }

    @Test
    void aumentarStock_CuandoCantidadDisponibleIgualATotal_LanzaDatoInvalidoException() {
        // Given
        stockModel.setCantidadDisponible(10);
        when(stockRepository.findByVideojuegoId(2L)).thenReturn(Optional.of(stockModel));

        // When / Then
        DatoInvalidoException exception = assertThrows(
                DatoInvalidoException.class,
                () -> stockService.aumentarStock(2L)
        );

        assertEquals("La cantidad disponible no puede superar la cantidad total", exception.getMessage());
        verify(stockRepository, never()).save(any(StockModel.class));
    }

    @Test
    void listarPorEstado_CuandoEstadoValidoEnMinuscula_NormalizaYRetornaLista() {
        // Given
        when(stockRepository.findByEstado("ACTIVO")).thenReturn(List.of(stockModel));

        // When
        List<StockResponseDTO> respuesta = stockService.listarPorEstado(" activo ");

        // Then
        assertEquals(1, respuesta.size());
        assertEquals("ACTIVO", respuesta.get(0).getEstado());
        verify(stockRepository).findByEstado("ACTIVO");
    }

    @Test
    void listarPorEstado_CuandoEstadoInvalido_LanzaDatoInvalidoException() {
        // When / Then
        DatoInvalidoException exception = assertThrows(
                DatoInvalidoException.class,
                () -> stockService.listarPorEstado("BLOQUEADO")
        );

        assertEquals("El estado debe ser ACTIVO o INACTIVO", exception.getMessage());
        verify(stockRepository, never()).findByEstado(any());
    }

    @Test
    void eliminarStock_CuandoStockExiste_CambiaEstadoAInactivo() {
        // Given
        when(stockRepository.findById(1L)).thenReturn(Optional.of(stockModel));
        when(stockRepository.save(any(StockModel.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        stockService.eliminarStock(1L);

        // Then
        assertEquals("INACTIVO", stockModel.getEstado());
        verify(stockRepository).save(stockModel);
    }

    private FeignException.NotFound crearFeignNotFound() {
        Request request = crearRequestFeign();
        Map<String, Collection<String>> headers = Collections.emptyMap();

        return new FeignException.NotFound(
                "Videojuego no encontrado",
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
                "/api/videojuegos/2",
                Map.of(),
                null,
                StandardCharsets.UTF_8,
                null
        );
    }
} 
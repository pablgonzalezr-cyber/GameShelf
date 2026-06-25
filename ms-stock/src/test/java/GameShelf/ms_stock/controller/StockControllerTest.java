package GameShelf.ms_stock.controller;

import java.util.List;

import static org.hamcrest.Matchers.hasKey;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.Mock;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import com.fasterxml.jackson.databind.ObjectMapper;

import GameShelf.ms_stock.dto.StockRequestDTO;
import GameShelf.ms_stock.dto.StockResponseDTO;
import GameShelf.ms_stock.exception.ComunicacionVideojuegoException;
import GameShelf.ms_stock.exception.DatoDuplicadoException;
import GameShelf.ms_stock.exception.DatoInvalidoException;
import GameShelf.ms_stock.exception.GlobalExceptionHandler;
import GameShelf.ms_stock.exception.StockNoEncontradoException;
import GameShelf.ms_stock.service.StockService;

@ExtendWith(MockitoExtension.class)
class StockControllerTest {

    @Mock
    private StockService stockService;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        StockController stockController = new StockController(stockService);

        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();

        mockMvc = MockMvcBuilders
                .standaloneSetup(stockController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .setValidator(validator)
                .build();

        objectMapper = new ObjectMapper();
    }

    @Test
    void crearStock_CuandoDatosValidos_Retorna201() throws Exception {
        // Given
        StockRequestDTO requestDTO = crearRequestValido();

        StockResponseDTO responseDTO = new StockResponseDTO(1L, 2L, 10, 8, "ACTIVO");

        when(stockService.crearStock(any(StockRequestDTO.class))).thenReturn(responseDTO);

        // When / Then
        mockMvc.perform(post("/api/stocks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.videojuegoId").value(2))
                .andExpect(jsonPath("$.cantidadTotal").value(10))
                .andExpect(jsonPath("$.cantidadDisponible").value(8))
                .andExpect(jsonPath("$.estado").value("ACTIVO"));

        verify(stockService).crearStock(any(StockRequestDTO.class));
    }

    @Test
    void crearStock_CuandoDatosInvalidos_Retorna400() throws Exception {
        // Given
        StockRequestDTO requestDTO = new StockRequestDTO();
        requestDTO.setVideojuegoId(null);
        requestDTO.setCantidadTotal(-1);
        requestDTO.setCantidadDisponible(-2);
        requestDTO.setEstado("ACTIVO");

        // When / Then
        mockMvc.perform(post("/api/stocks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.estado").value(400))
                .andExpect(jsonPath("$.error").value("Error de validación"))
                .andExpect(jsonPath("$.mensajes", hasKey("videojuegoId")))
                .andExpect(jsonPath("$.mensajes", hasKey("cantidadTotal")))
                .andExpect(jsonPath("$.mensajes", hasKey("cantidadDisponible")));

        verify(stockService, never()).crearStock(any(StockRequestDTO.class));
    }

    @Test
    void crearStock_CuandoStockDuplicado_Retorna400() throws Exception {
        // Given
        StockRequestDTO requestDTO = crearRequestValido();

        when(stockService.crearStock(any(StockRequestDTO.class)))
                .thenThrow(new DatoDuplicadoException("Ya existe stock para este videojuego"));

        // When / Then
        mockMvc.perform(post("/api/stocks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.estado").value(400))
                .andExpect(jsonPath("$.error").value("Dato duplicado"))
                .andExpect(jsonPath("$.mensaje").value("Ya existe stock para este videojuego"));

        verify(stockService).crearStock(any(StockRequestDTO.class));
    }

    @Test
    void crearStock_CuandoVideojuegoNoDisponible_Retorna503() throws Exception {
        // Given
        StockRequestDTO requestDTO = crearRequestValido();

        when(stockService.crearStock(any(StockRequestDTO.class)))
                .thenThrow(new ComunicacionVideojuegoException("No se pudo comunicar con ms-videojuego"));

        // When / Then
        mockMvc.perform(post("/api/stocks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.estado").value(503))
                .andExpect(jsonPath("$.error").value("Error de comunicación con ms-videojuego"))
                .andExpect(jsonPath("$.mensaje").value("No se pudo comunicar con ms-videojuego"));

        verify(stockService).crearStock(any(StockRequestDTO.class));
    }

    @Test
    void listarStocks_CuandoExistenStocks_Retorna200() throws Exception {
        // Given
        List<StockResponseDTO> stocks = List.of(
                new StockResponseDTO(1L, 2L, 10, 8, "ACTIVO"),
                new StockResponseDTO(2L, 3L, 5, 5, "ACTIVO")
        );

        when(stockService.listarStocks()).thenReturn(stocks);

        // When / Then
        mockMvc.perform(get("/api/stocks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].videojuegoId").value(2))
                .andExpect(jsonPath("$[1].videojuegoId").value(3));

        verify(stockService).listarStocks();
    }

    @Test
    void buscarStock_CuandoExiste_Retorna200() throws Exception {
        // Given
        StockResponseDTO responseDTO = new StockResponseDTO(1L, 2L, 10, 8, "ACTIVO");

        when(stockService.buscarPorId(1L)).thenReturn(responseDTO);

        // When / Then
        mockMvc.perform(get("/api/stocks/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.videojuegoId").value(2))
                .andExpect(jsonPath("$.estado").value("ACTIVO"));

        verify(stockService).buscarPorId(1L);
    }

    @Test
    void buscarStock_CuandoNoExiste_Retorna404() throws Exception {
        // Given
        when(stockService.buscarPorId(99L))
                .thenThrow(new StockNoEncontradoException("Stock no encontrado"));

        // When / Then
        mockMvc.perform(get("/api/stocks/{id}", 99L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.estado").value(404))
                .andExpect(jsonPath("$.error").value("Stock no encontrado"))
                .andExpect(jsonPath("$.mensaje").value("Stock no encontrado"));

        verify(stockService).buscarPorId(99L);
    }

    @Test
    void buscarPorVideojuego_CuandoExiste_Retorna200() throws Exception {
        // Given
        StockResponseDTO responseDTO = new StockResponseDTO(1L, 2L, 10, 8, "ACTIVO");

        when(stockService.buscarPorVideojuego(2L)).thenReturn(responseDTO);

        // When / Then
        mockMvc.perform(get("/api/stocks/videojuego/{videojuegoId}", 2L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.videojuegoId").value(2));

        verify(stockService).buscarPorVideojuego(2L);
    }

    @Test
    void actualizarStock_CuandoDatosValidos_Retorna200() throws Exception {
        // Given
        StockRequestDTO requestDTO = crearRequestValido();
        StockResponseDTO responseDTO = new StockResponseDTO(1L, 2L, 10, 8, "ACTIVO");

        when(stockService.actualizarStock(eq(1L), any(StockRequestDTO.class))).thenReturn(responseDTO);

        // When / Then
        mockMvc.perform(put("/api/stocks/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.videojuegoId").value(2))
                .andExpect(jsonPath("$.cantidadDisponible").value(8));

        verify(stockService).actualizarStock(eq(1L), any(StockRequestDTO.class));
    }

    @Test
    void actualizarStock_CuandoCantidadDisponibleMayorQueTotal_Retorna400() throws Exception {
        // Given
        StockRequestDTO requestDTO = crearRequestValido();
        requestDTO.setCantidadTotal(5);
        requestDTO.setCantidadDisponible(8);

        when(stockService.actualizarStock(eq(1L), any(StockRequestDTO.class)))
                .thenThrow(new DatoInvalidoException("La cantidad disponible no puede ser mayor que la cantidad total"));

        // When / Then
        mockMvc.perform(put("/api/stocks/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.estado").value(400))
                .andExpect(jsonPath("$.error").value("Dato inválido"))
                .andExpect(jsonPath("$.mensaje").value("La cantidad disponible no puede ser mayor que la cantidad total"));

        verify(stockService).actualizarStock(eq(1L), any(StockRequestDTO.class));
    }

    @Test
    void reducirStock_CuandoExiste_Retorna200() throws Exception {
        // Given
        StockResponseDTO responseDTO = new StockResponseDTO(1L, 2L, 10, 7, "ACTIVO");
        when(stockService.reducirStock(2L)).thenReturn(responseDTO);

        // When / Then
        mockMvc.perform(put("/api/stocks/reducir/{videojuegoId}", 2L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cantidadDisponible").value(7));

        verify(stockService).reducirStock(2L);
    }

    @Test
    void reducirStock_CuandoSinCopias_Retorna400() throws Exception {
        // Given
        when(stockService.reducirStock(2L))
                .thenThrow(new DatoInvalidoException("No hay copias disponibles para préstamo"));

        // When / Then
        mockMvc.perform(put("/api/stocks/reducir/{videojuegoId}", 2L))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.estado").value(400))
                .andExpect(jsonPath("$.error").value("Dato inválido"))
                .andExpect(jsonPath("$.mensaje").value("No hay copias disponibles para préstamo"));

        verify(stockService).reducirStock(2L);
    }

    @Test
    void aumentarStock_CuandoExiste_Retorna200() throws Exception {
        // Given
        StockResponseDTO responseDTO = new StockResponseDTO(1L, 2L, 10, 9, "ACTIVO");
        when(stockService.aumentarStock(2L)).thenReturn(responseDTO);

        // When / Then
        mockMvc.perform(put("/api/stocks/aumentar/{videojuegoId}", 2L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cantidadDisponible").value(9));

        verify(stockService).aumentarStock(2L);
    }

    @Test
    void listarPorEstado_CuandoEstadoValido_Retorna200() throws Exception {
        // Given
        List<StockResponseDTO> stocks = List.of(
                new StockResponseDTO(1L, 2L, 10, 8, "ACTIVO")
        );
        when(stockService.listarPorEstado("ACTIVO")).thenReturn(stocks);

        // When / Then
        mockMvc.perform(get("/api/stocks/estado/{estado}", "ACTIVO"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].estado").value("ACTIVO"));

        verify(stockService).listarPorEstado("ACTIVO");
    }

    @Test
    void eliminarStock_CuandoExiste_Retorna200() throws Exception {
        // When / Then
        mockMvc.perform(delete("/api/stocks/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mensaje").value("Stock desactivado correctamente"));

        verify(stockService).eliminarStock(1L);
    }

    @Test
    void eliminarStock_CuandoNoExiste_Retorna404() throws Exception {
        // Given
        doThrow(new StockNoEncontradoException("Stock no encontrado"))
                .when(stockService).eliminarStock(99L);

        // When / Then
        mockMvc.perform(delete("/api/stocks/{id}", 99L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.estado").value(404))
                .andExpect(jsonPath("$.error").value("Stock no encontrado"))
                .andExpect(jsonPath("$.mensaje").value("Stock no encontrado"));

        verify(stockService).eliminarStock(99L);
    }

    private StockRequestDTO crearRequestValido() {
        StockRequestDTO requestDTO = new StockRequestDTO();
        requestDTO.setVideojuegoId(2L);
        requestDTO.setCantidadTotal(10);
        requestDTO.setCantidadDisponible(8);
        requestDTO.setEstado("ACTIVO");
        return requestDTO;
    }
}
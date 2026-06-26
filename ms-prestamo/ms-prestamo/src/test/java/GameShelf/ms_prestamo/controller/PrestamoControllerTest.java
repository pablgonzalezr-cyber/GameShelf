package GameShelf.ms_prestamo.controller;

import GameShelf.ms_prestamo.dto.PrestamoRequestDTO;
import GameShelf.ms_prestamo.dto.PrestamoResponseDTO;
import GameShelf.ms_prestamo.dto.RenovacionPrestamoRequestDTO;
import GameShelf.ms_prestamo.dto.RenovacionPrestamoResponseDTO;
import GameShelf.ms_prestamo.exception.ComunicacionMicroservicioException;
import GameShelf.ms_prestamo.exception.DatoInvalidoException;
import GameShelf.ms_prestamo.exception.GlobalExceptionHandler;
import GameShelf.ms_prestamo.exception.PrestamoNoEncontradoException;
import GameShelf.ms_prestamo.service.PrestamoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.time.LocalDate;
import java.util.List;

import static org.hamcrest.Matchers.hasKey;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class PrestamoControllerTest {

    @Mock
    private PrestamoService prestamoService;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        PrestamoController prestamoController = new PrestamoController(prestamoService);

        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();

        mockMvc = MockMvcBuilders
                .standaloneSetup(prestamoController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .setValidator(validator)
                .build();

        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @Test
    void crearPrestamo_CuandoDatosValidos_Retorna201() throws Exception {
        // Given
        PrestamoRequestDTO requestDTO = crearRequestValido();

        PrestamoResponseDTO responseDTO = new PrestamoResponseDTO(
                1L,
                1L,
                2L,
                LocalDate.now(),
                LocalDate.now().plusDays(7),
                "PRESTADO"
        );

        when(prestamoService.crearPrestamo(any(PrestamoRequestDTO.class))).thenReturn(responseDTO);

        // When / Then
        mockMvc.perform(post("/api/prestamos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.usuarioId").value(1))
                .andExpect(jsonPath("$.videojuegoId").value(2))
                .andExpect(jsonPath("$.estado").value("PRESTADO"));

        verify(prestamoService).crearPrestamo(any(PrestamoRequestDTO.class));
    }

    @Test
    void crearPrestamo_CuandoDatosInvalidos_Retorna400() throws Exception {
        // Given
        PrestamoRequestDTO requestDTO = new PrestamoRequestDTO();
        requestDTO.setUsuarioId(null);
        requestDTO.setVideojuegoId(null);
        requestDTO.setFechaDevolucion(null);

        // When / Then
        mockMvc.perform(post("/api/prestamos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.estado").value(400))
                .andExpect(jsonPath("$.error").value("Error de validación"))
                .andExpect(jsonPath("$.mensajes", hasKey("usuarioId")))
                .andExpect(jsonPath("$.mensajes", hasKey("videojuegoId")))
                .andExpect(jsonPath("$.mensajes", hasKey("fechaDevolucion")));

        verify(prestamoService, never()).crearPrestamo(any(PrestamoRequestDTO.class));
    }

    @Test
    void crearPrestamo_CuandoReglaNegocioFalla_Retorna400() throws Exception {
        // Given
        PrestamoRequestDTO requestDTO = crearRequestValido();

        when(prestamoService.crearPrestamo(any(PrestamoRequestDTO.class)))
                .thenThrow(new DatoInvalidoException("No hay stock disponible para este videojuego"));

        // When / Then
        mockMvc.perform(post("/api/prestamos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.estado").value(400))
                .andExpect(jsonPath("$.error").value("Dato inválido"))
                .andExpect(jsonPath("$.mensaje").value("No hay stock disponible para este videojuego"));

        verify(prestamoService).crearPrestamo(any(PrestamoRequestDTO.class));
    }

    @Test
    void crearPrestamo_CuandoMicroservicioNoDisponible_Retorna503() throws Exception {
        // Given
        PrestamoRequestDTO requestDTO = crearRequestValido();

        when(prestamoService.crearPrestamo(any(PrestamoRequestDTO.class)))
                .thenThrow(new ComunicacionMicroservicioException("No se pudo comunicar con ms-stock"));

        // When / Then
        mockMvc.perform(post("/api/prestamos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.estado").value(503))
                .andExpect(jsonPath("$.error").value("Error de comunicación entre microservicios"))
                .andExpect(jsonPath("$.mensaje").value("No se pudo comunicar con ms-stock"));

        verify(prestamoService).crearPrestamo(any(PrestamoRequestDTO.class));
    }

    @Test
    void listarPrestamos_CuandoExistenPrestamos_Retorna200() throws Exception {
        // Given
        List<PrestamoResponseDTO> prestamos = List.of(
                new PrestamoResponseDTO(1L, 1L, 2L, LocalDate.now(), LocalDate.now().plusDays(7), "PRESTADO"),
                new PrestamoResponseDTO(2L, 3L, 4L, LocalDate.now(), LocalDate.now().plusDays(10), "PRESTADO")
        );

        when(prestamoService.listarPrestamos()).thenReturn(prestamos);

        // When / Then
        mockMvc.perform(get("/api/prestamos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].usuarioId").value(1))
                .andExpect(jsonPath("$[1].usuarioId").value(3));

        verify(prestamoService).listarPrestamos();
    }

    @Test
    void buscarPorId_CuandoPrestamoExiste_Retorna200() throws Exception {
        // Given
        PrestamoResponseDTO responseDTO = new PrestamoResponseDTO(
                1L,
                1L,
                2L,
                LocalDate.now(),
                LocalDate.now().plusDays(7),
                "PRESTADO"
        );

        when(prestamoService.buscarPorId(1L)).thenReturn(responseDTO);

        // When / Then
        mockMvc.perform(get("/api/prestamos/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.usuarioId").value(1))
                .andExpect(jsonPath("$.estado").value("PRESTADO"));

        verify(prestamoService).buscarPorId(1L);
    }

    @Test
    void buscarPorId_CuandoPrestamoNoExiste_Retorna404() throws Exception {
        // Given
        when(prestamoService.buscarPorId(99L))
                .thenThrow(new PrestamoNoEncontradoException("Préstamo no encontrado"));

        // When / Then
        mockMvc.perform(get("/api/prestamos/{id}", 99L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.estado").value(404))
                .andExpect(jsonPath("$.error").value("Préstamo no encontrado"))
                .andExpect(jsonPath("$.mensaje").value("Préstamo no encontrado"));

        verify(prestamoService).buscarPorId(99L);
    }

    @Test
    void buscarPorUsuario_CuandoExistenPrestamos_Retorna200() throws Exception {
        // Given
        List<PrestamoResponseDTO> prestamos = List.of(
                new PrestamoResponseDTO(1L, 1L, 2L, LocalDate.now(), LocalDate.now().plusDays(7), "PRESTADO")
        );

        when(prestamoService.buscarPorUsuario(1L)).thenReturn(prestamos);

        // When / Then
        mockMvc.perform(get("/api/prestamos/usuario/{usuarioId}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].usuarioId").value(1));

        verify(prestamoService).buscarPorUsuario(1L);
    }

    @Test
    void buscarPorVideojuego_CuandoExistenPrestamos_Retorna200() throws Exception {
        // Given
        List<PrestamoResponseDTO> prestamos = List.of(
                new PrestamoResponseDTO(1L, 1L, 2L, LocalDate.now(), LocalDate.now().plusDays(7), "PRESTADO")
        );

        when(prestamoService.buscarPorVideojuego(2L)).thenReturn(prestamos);

        // When / Then
        mockMvc.perform(get("/api/prestamos/videojuego/{videojuegoId}", 2L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].videojuegoId").value(2));

        verify(prestamoService).buscarPorVideojuego(2L);
    }

    @Test
    void buscarPorEstado_CuandoEstadoValido_Retorna200() throws Exception {
        // Given
        List<PrestamoResponseDTO> prestamos = List.of(
                new PrestamoResponseDTO(1L, 1L, 2L, LocalDate.now(), LocalDate.now().plusDays(7), "PRESTADO")
        );

        when(prestamoService.buscarPorEstado("PRESTADO")).thenReturn(prestamos);

        // When / Then
        mockMvc.perform(get("/api/prestamos/estado/{estado}", "PRESTADO"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].estado").value("PRESTADO"));

        verify(prestamoService).buscarPorEstado("PRESTADO");
    }

    @Test
    void devolverPrestamo_CuandoExiste_Retorna200() throws Exception {
        // Given
        PrestamoResponseDTO responseDTO = new PrestamoResponseDTO(
                1L,
                1L,
                2L,
                LocalDate.now(),
                LocalDate.now(),
                "DEVUELTO"
        );

        when(prestamoService.devolverPrestamo(1L)).thenReturn(responseDTO);

        // When / Then
        mockMvc.perform(put("/api/prestamos/devolver/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.estado").value("DEVUELTO"));

        verify(prestamoService).devolverPrestamo(1L);
    }

    @Test
    void devolverPrestamo_CuandoNoEstaActivo_Retorna400() throws Exception {
        // Given
        when(prestamoService.devolverPrestamo(1L))
                .thenThrow(new DatoInvalidoException("El préstamo no está activo"));

        // When / Then
        mockMvc.perform(put("/api/prestamos/devolver/{id}", 1L))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.estado").value(400))
                .andExpect(jsonPath("$.error").value("Dato inválido"))
                .andExpect(jsonPath("$.mensaje").value("El préstamo no está activo"));

        verify(prestamoService).devolverPrestamo(1L);
    }

    @Test
    void cancelarPrestamo_CuandoExiste_Retorna200() throws Exception {
        // When / Then
        mockMvc.perform(delete("/api/prestamos/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mensaje").value("Préstamo cancelado correctamente"));

        verify(prestamoService).cancelarPrestamo(1L);
    }

    @Test
    void cancelarPrestamo_CuandoNoExiste_Retorna404() throws Exception {
        // Given
        doThrow(new PrestamoNoEncontradoException("Préstamo no encontrado"))
                .when(prestamoService).cancelarPrestamo(99L);

        // When / Then
        mockMvc.perform(delete("/api/prestamos/{id}", 99L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.estado").value(404))
                .andExpect(jsonPath("$.error").value("Préstamo no encontrado"))
                .andExpect(jsonPath("$.mensaje").value("Préstamo no encontrado"));

        verify(prestamoService).cancelarPrestamo(99L);
    }

    @Test
    void renovarPrestamo_CuandoDatosValidos_Retorna201() throws Exception {
        // Given
        RenovacionPrestamoRequestDTO requestDTO = crearRenovacionRequestValida();

        RenovacionPrestamoResponseDTO responseDTO = new RenovacionPrestamoResponseDTO(
                1L,
                1L,
                LocalDate.now().plusDays(7),
                LocalDate.now().plusDays(14),
                "El usuario solicita más tiempo",
                LocalDate.now()
        );

        when(prestamoService.renovarPrestamo(eq(1L), any(RenovacionPrestamoRequestDTO.class))).thenReturn(responseDTO);

        // When / Then
        mockMvc.perform(post("/api/prestamos/{id}/renovaciones", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.prestamoId").value(1))
                .andExpect(jsonPath("$.motivo").value("El usuario solicita más tiempo"));

        verify(prestamoService).renovarPrestamo(eq(1L), any(RenovacionPrestamoRequestDTO.class));
    }

    @Test
    void renovarPrestamo_CuandoDatosInvalidos_Retorna400() throws Exception {
        // Given
        RenovacionPrestamoRequestDTO requestDTO = new RenovacionPrestamoRequestDTO();
        requestDTO.setNuevaFechaDevolucion(null);
        requestDTO.setMotivo("abc");

        // When / Then
        mockMvc.perform(post("/api/prestamos/{id}/renovaciones", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.estado").value(400))
                .andExpect(jsonPath("$.error").value("Error de validación"))
                .andExpect(jsonPath("$.mensajes", hasKey("nuevaFechaDevolucion")))
                .andExpect(jsonPath("$.mensajes", hasKey("motivo")));

        verify(prestamoService, never()).renovarPrestamo(eq(1L), any(RenovacionPrestamoRequestDTO.class));
    }

    @Test
    void listarRenovacionesPorPrestamo_CuandoExiste_Retorna200() throws Exception {
        // Given
        List<RenovacionPrestamoResponseDTO> renovaciones = List.of(
                new RenovacionPrestamoResponseDTO(
                        1L,
                        1L,
                        LocalDate.now().plusDays(7),
                        LocalDate.now().plusDays(14),
                        "El usuario solicita más tiempo",
                        LocalDate.now()
                )
        );

        when(prestamoService.listarRenovacionesPorPrestamo(1L)).thenReturn(renovaciones);

        // When / Then
        mockMvc.perform(get("/api/prestamos/{id}/renovaciones", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].prestamoId").value(1));

        verify(prestamoService).listarRenovacionesPorPrestamo(1L);
    }

    private PrestamoRequestDTO crearRequestValido() {
        PrestamoRequestDTO requestDTO = new PrestamoRequestDTO();
        requestDTO.setUsuarioId(1L);
        requestDTO.setVideojuegoId(2L);
        requestDTO.setFechaDevolucion(LocalDate.now().plusDays(7));
        return requestDTO;
    }

    private RenovacionPrestamoRequestDTO crearRenovacionRequestValida() {
        RenovacionPrestamoRequestDTO requestDTO = new RenovacionPrestamoRequestDTO();
        requestDTO.setNuevaFechaDevolucion(LocalDate.now().plusDays(14));
        requestDTO.setMotivo("El usuario solicita más tiempo");
        return requestDTO;
    }
}
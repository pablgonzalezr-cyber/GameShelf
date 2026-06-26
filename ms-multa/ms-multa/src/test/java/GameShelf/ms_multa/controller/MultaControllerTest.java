package GameShelf.ms_multa.controller;

import GameShelf.ms_multa.dto.MultaRequestDTO;
import GameShelf.ms_multa.dto.MultaResponseDTO;
import GameShelf.ms_multa.dto.PagoMultaRequestDTO;
import GameShelf.ms_multa.dto.PagoMultaResponseDTO;
import GameShelf.ms_multa.exception.ComunicacionMicroservicioException;
import GameShelf.ms_multa.exception.DatoInvalidoException;
import GameShelf.ms_multa.exception.GlobalExceptionHandler;
import GameShelf.ms_multa.exception.MultaNoEncontradaException;
import GameShelf.ms_multa.service.MultaService;
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
class MultaControllerTest {

    @Mock
    private MultaService multaService;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        MultaController multaController = new MultaController(multaService);

        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();

        mockMvc = MockMvcBuilders
                .standaloneSetup(multaController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .setValidator(validator)
                .build();

        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @Test
    void crearMulta_CuandoDatosValidos_Retorna201() throws Exception {
        // Given
        MultaRequestDTO requestDTO = crearRequestValido();

        MultaResponseDTO responseDTO = new MultaResponseDTO(
                1L,
                1L,
                10L,
                5000.0,
                "Atraso en la devolución del videojuego",
                LocalDate.now(),
                "PENDIENTE"
        );

        when(multaService.crearMulta(any(MultaRequestDTO.class))).thenReturn(responseDTO);

        // When / Then
        mockMvc.perform(post("/api/multas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.usuarioId").value(1))
                .andExpect(jsonPath("$.prestamoId").value(10))
                .andExpect(jsonPath("$.monto").value(5000.0))
                .andExpect(jsonPath("$.estado").value("PENDIENTE"));

        verify(multaService).crearMulta(any(MultaRequestDTO.class));
    }

    @Test
    void crearMulta_CuandoDatosInvalidos_Retorna400() throws Exception {
        // Given
        MultaRequestDTO requestDTO = new MultaRequestDTO();
        requestDTO.setUsuarioId(null);
        requestDTO.setPrestamoId(null);
        requestDTO.setMonto(-1.0);
        requestDTO.setMotivo("abc");

        // When / Then
        mockMvc.perform(post("/api/multas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.estado").value(400))
                .andExpect(jsonPath("$.error").value("Error de validación"))
                .andExpect(jsonPath("$.mensajes", hasKey("usuarioId")))
                .andExpect(jsonPath("$.mensajes", hasKey("prestamoId")))
                .andExpect(jsonPath("$.mensajes", hasKey("monto")))
                .andExpect(jsonPath("$.mensajes", hasKey("motivo")));

        verify(multaService, never()).crearMulta(any(MultaRequestDTO.class));
    }

    @Test
    void crearMulta_CuandoReglaNegocioFalla_Retorna400() throws Exception {
        // Given
        MultaRequestDTO requestDTO = crearRequestValido();

        when(multaService.crearMulta(any(MultaRequestDTO.class)))
                .thenThrow(new DatoInvalidoException("El préstamo no pertenece al usuario indicado"));

        // When / Then
        mockMvc.perform(post("/api/multas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.estado").value(400))
                .andExpect(jsonPath("$.error").value("Dato inválido"))
                .andExpect(jsonPath("$.mensaje").value("El préstamo no pertenece al usuario indicado"));

        verify(multaService).crearMulta(any(MultaRequestDTO.class));
    }

    @Test
    void crearMulta_CuandoMicroservicioNoDisponible_Retorna503() throws Exception {
        // Given
        MultaRequestDTO requestDTO = crearRequestValido();

        when(multaService.crearMulta(any(MultaRequestDTO.class)))
                .thenThrow(new ComunicacionMicroservicioException("No se pudo comunicar con ms-prestamo"));

        // When / Then
        mockMvc.perform(post("/api/multas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.estado").value(503))
                .andExpect(jsonPath("$.error").value("Error de comunicación entre microservicios"))
                .andExpect(jsonPath("$.mensaje").value("No se pudo comunicar con ms-prestamo"));

        verify(multaService).crearMulta(any(MultaRequestDTO.class));
    }

    @Test
    void listarMultas_CuandoExistenMultas_Retorna200() throws Exception {
        // Given
        List<MultaResponseDTO> multas = List.of(
                new MultaResponseDTO(1L, 1L, 10L, 5000.0, "Atraso", LocalDate.now(), "PENDIENTE"),
                new MultaResponseDTO(2L, 3L, 20L, 7000.0, "Atraso extendido", LocalDate.now(), "PAGADA")
        );

        when(multaService.listarMultas()).thenReturn(multas);

        // When / Then
        mockMvc.perform(get("/api/multas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].usuarioId").value(1))
                .andExpect(jsonPath("$[1].estado").value("PAGADA"));

        verify(multaService).listarMultas();
    }

    @Test
    void buscarPorId_CuandoMultaExiste_Retorna200() throws Exception {
        // Given
        MultaResponseDTO responseDTO = new MultaResponseDTO(
                1L,
                1L,
                10L,
                5000.0,
                "Atraso en la devolución del videojuego",
                LocalDate.now(),
                "PENDIENTE"
        );

        when(multaService.buscarPorId(1L)).thenReturn(responseDTO);

        // When / Then
        mockMvc.perform(get("/api/multas/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.usuarioId").value(1))
                .andExpect(jsonPath("$.estado").value("PENDIENTE"));

        verify(multaService).buscarPorId(1L);
    }

    @Test
    void buscarPorId_CuandoMultaNoExiste_Retorna404() throws Exception {
        // Given
        when(multaService.buscarPorId(99L))
                .thenThrow(new MultaNoEncontradaException("Multa no encontrada"));

        // When / Then
        mockMvc.perform(get("/api/multas/{id}", 99L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.estado").value(404))
                .andExpect(jsonPath("$.error").value("Multa no encontrada"))
                .andExpect(jsonPath("$.mensaje").value("Multa no encontrada"));

        verify(multaService).buscarPorId(99L);
    }

    @Test
    void buscarPorUsuario_CuandoExistenMultas_Retorna200() throws Exception {
        // Given
        List<MultaResponseDTO> multas = List.of(
                new MultaResponseDTO(1L, 1L, 10L, 5000.0, "Atraso", LocalDate.now(), "PENDIENTE")
        );

        when(multaService.buscarPorUsuario(1L)).thenReturn(multas);

        // When / Then
        mockMvc.perform(get("/api/multas/usuario/{usuarioId}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].usuarioId").value(1));

        verify(multaService).buscarPorUsuario(1L);
    }

    @Test
    void buscarPorPrestamo_CuandoExistenMultas_Retorna200() throws Exception {
        // Given
        List<MultaResponseDTO> multas = List.of(
                new MultaResponseDTO(1L, 1L, 10L, 5000.0, "Atraso", LocalDate.now(), "PENDIENTE")
        );

        when(multaService.buscarPorPrestamo(10L)).thenReturn(multas);

        // When / Then
        mockMvc.perform(get("/api/multas/prestamo/{prestamoId}", 10L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].prestamoId").value(10));

        verify(multaService).buscarPorPrestamo(10L);
    }

    @Test
    void buscarPorEstado_CuandoEstadoValido_Retorna200() throws Exception {
        // Given
        List<MultaResponseDTO> multas = List.of(
                new MultaResponseDTO(1L, 1L, 10L, 5000.0, "Atraso", LocalDate.now(), "PENDIENTE")
        );

        when(multaService.buscarPorEstado("PENDIENTE")).thenReturn(multas);

        // When / Then
        mockMvc.perform(get("/api/multas/estado/{estado}", "PENDIENTE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].estado").value("PENDIENTE"));

        verify(multaService).buscarPorEstado("PENDIENTE");
    }

    @Test
    void actualizarMulta_CuandoDatosValidos_Retorna200() throws Exception {
        // Given
        MultaRequestDTO requestDTO = crearRequestValido();
        requestDTO.setMonto(7000.0);
        requestDTO.setEstado("PAGADA");

        MultaResponseDTO responseDTO = new MultaResponseDTO(
                1L,
                1L,
                10L,
                7000.0,
                "Atraso en la devolución del videojuego",
                LocalDate.now(),
                "PAGADA"
        );

        when(multaService.actualizarMulta(eq(1L), any(MultaRequestDTO.class))).thenReturn(responseDTO);

        // When / Then
        mockMvc.perform(put("/api/multas/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.monto").value(7000.0))
                .andExpect(jsonPath("$.estado").value("PAGADA"));

        verify(multaService).actualizarMulta(eq(1L), any(MultaRequestDTO.class));
    }

    @Test
    void actualizarMulta_CuandoEstadoInvalido_Retorna400() throws Exception {
        // Given
        MultaRequestDTO requestDTO = crearRequestValido();
        requestDTO.setEstado("BLOQUEADO");

        when(multaService.actualizarMulta(eq(1L), any(MultaRequestDTO.class)))
                .thenThrow(new DatoInvalidoException("El estado debe ser PENDIENTE, PAGADA o ANULADA"));

        // When / Then
        mockMvc.perform(put("/api/multas/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.estado").value(400))
                .andExpect(jsonPath("$.error").value("Dato inválido"))
                .andExpect(jsonPath("$.mensaje").value("El estado debe ser PENDIENTE, PAGADA o ANULADA"));

        verify(multaService).actualizarMulta(eq(1L), any(MultaRequestDTO.class));
    }

    @Test
    void pagarMulta_CuandoExiste_Retorna200() throws Exception {
        // Given
        MultaResponseDTO responseDTO = new MultaResponseDTO(
                1L,
                1L,
                10L,
                5000.0,
                "Atraso",
                LocalDate.now(),
                "PAGADA"
        );

        when(multaService.pagarMulta(1L)).thenReturn(responseDTO);

        // When / Then
        mockMvc.perform(put("/api/multas/pagar/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.estado").value("PAGADA"));

        verify(multaService).pagarMulta(1L);
    }

    @Test
    void pagarMulta_CuandoNoEstaPendiente_Retorna400() throws Exception {
        // Given
        when(multaService.pagarMulta(1L))
                .thenThrow(new DatoInvalidoException("Solo se pueden pagar multas pendientes"));

        // When / Then
        mockMvc.perform(put("/api/multas/pagar/{id}", 1L))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.estado").value(400))
                .andExpect(jsonPath("$.error").value("Dato inválido"))
                .andExpect(jsonPath("$.mensaje").value("Solo se pueden pagar multas pendientes"));

        verify(multaService).pagarMulta(1L);
    }

    @Test
    void anularMulta_CuandoExiste_Retorna200() throws Exception {
        // Given
        MultaResponseDTO responseDTO = new MultaResponseDTO(
                1L,
                1L,
                10L,
                5000.0,
                "Atraso",
                LocalDate.now(),
                "ANULADA"
        );

        when(multaService.anularMulta(1L)).thenReturn(responseDTO);

        // When / Then
        mockMvc.perform(put("/api/multas/anular/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.estado").value("ANULADA"));

        verify(multaService).anularMulta(1L);
    }

    @Test
    void registrarPago_CuandoDatosValidos_Retorna201() throws Exception {
        // Given
        PagoMultaRequestDTO requestDTO = crearPagoRequestValido();

        PagoMultaResponseDTO responseDTO = new PagoMultaResponseDTO(
                1L,
                1L,
                5000.0,
                LocalDate.now(),
                "EFECTIVO",
                "CONFIRMADO"
        );

        when(multaService.registrarPago(eq(1L), any(PagoMultaRequestDTO.class))).thenReturn(responseDTO);

        // When / Then
        mockMvc.perform(post("/api/multas/{id}/pagos", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.multaId").value(1))
                .andExpect(jsonPath("$.montoPagado").value(5000.0))
                .andExpect(jsonPath("$.estadoPago").value("CONFIRMADO"));

        verify(multaService).registrarPago(eq(1L), any(PagoMultaRequestDTO.class));
    }

    @Test
    void registrarPago_CuandoDatosInvalidos_Retorna400() throws Exception {
        // Given
        PagoMultaRequestDTO requestDTO = new PagoMultaRequestDTO();
        requestDTO.setMontoPagado(-1.0);
        requestDTO.setMetodoPago("ab");

        // When / Then
        mockMvc.perform(post("/api/multas/{id}/pagos", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.estado").value(400))
                .andExpect(jsonPath("$.error").value("Error de validación"))
                .andExpect(jsonPath("$.mensajes", hasKey("montoPagado")))
                .andExpect(jsonPath("$.mensajes", hasKey("metodoPago")));

        verify(multaService, never()).registrarPago(eq(1L), any(PagoMultaRequestDTO.class));
    }

    @Test
    void listarPagosPorMulta_CuandoExistenPagos_Retorna200() throws Exception {
        // Given
        List<PagoMultaResponseDTO> pagos = List.of(
                new PagoMultaResponseDTO(1L, 1L, 5000.0, LocalDate.now(), "EFECTIVO", "CONFIRMADO")
        );

        when(multaService.listarPagosPorMulta(1L)).thenReturn(pagos);

        // When / Then
        mockMvc.perform(get("/api/multas/{id}/pagos", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].multaId").value(1))
                .andExpect(jsonPath("$[0].estadoPago").value("CONFIRMADO"));

        verify(multaService).listarPagosPorMulta(1L);
    }

    @Test
    void eliminarMulta_CuandoExiste_Retorna200() throws Exception {
        // When / Then
        mockMvc.perform(delete("/api/multas/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mensaje").value("Multa eliminada correctamente"));

        verify(multaService).eliminarMulta(1L);
    }

    @Test
    void eliminarMulta_CuandoNoExiste_Retorna404() throws Exception {
        // Given
        doThrow(new MultaNoEncontradaException("Multa no encontrada"))
                .when(multaService).eliminarMulta(99L);

        // When / Then
        mockMvc.perform(delete("/api/multas/{id}", 99L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.estado").value(404))
                .andExpect(jsonPath("$.error").value("Multa no encontrada"))
                .andExpect(jsonPath("$.mensaje").value("Multa no encontrada"));

        verify(multaService).eliminarMulta(99L);
    }

    private MultaRequestDTO crearRequestValido() {
        MultaRequestDTO requestDTO = new MultaRequestDTO();
        requestDTO.setUsuarioId(1L);
        requestDTO.setPrestamoId(10L);
        requestDTO.setMonto(5000.0);
        requestDTO.setMotivo("Atraso en la devolución del videojuego");
        requestDTO.setEstado("PENDIENTE");
        return requestDTO;
    }

    private PagoMultaRequestDTO crearPagoRequestValido() {
        PagoMultaRequestDTO requestDTO = new PagoMultaRequestDTO();
        requestDTO.setMontoPagado(5000.0);
        requestDTO.setMetodoPago("EFECTIVO");
        return requestDTO;
    }
}
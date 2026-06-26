package GameShelf.ms_reserva.controller;

import GameShelf.ms_reserva.dto.HistorialReservaResponseDTO;
import GameShelf.ms_reserva.dto.ReservaRequestDTO;
import GameShelf.ms_reserva.dto.ReservaResponseDTO;
import GameShelf.ms_reserva.exception.DatoDuplicadoException;
import GameShelf.ms_reserva.exception.DatoInvalidoException;
import GameShelf.ms_reserva.exception.GlobalExceptionHandler;
import GameShelf.ms_reserva.exception.RecursoNoEncontradoException;
import GameShelf.ms_reserva.service.ReservaService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import feign.FeignException;
import feign.Request;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

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
class ReservaControllerTest {

    @Mock
    private ReservaService reservaService;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        ReservaController reservaController = new ReservaController(reservaService);

        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();

        mockMvc = MockMvcBuilders
                .standaloneSetup(reservaController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .setValidator(validator)
                .build();

        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @Test
    void crearReserva_CuandoDatosValidos_Retorna201() throws Exception {
        // Given
        ReservaRequestDTO requestDTO = crearRequestValido();

        ReservaResponseDTO responseDTO = new ReservaResponseDTO(
                1L,
                1L,
                2L,
                LocalDate.now(),
                "PENDIENTE"
        );

        when(reservaService.crearReserva(any(ReservaRequestDTO.class))).thenReturn(responseDTO);

        // When / Then
        mockMvc.perform(post("/api/reservas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.usuarioId").value(1))
                .andExpect(jsonPath("$.videojuegoId").value(2))
                .andExpect(jsonPath("$.estado").value("PENDIENTE"));

        verify(reservaService).crearReserva(any(ReservaRequestDTO.class));
    }

    @Test
    void crearReserva_CuandoDatosInvalidos_Retorna400() throws Exception {
        // Given
        ReservaRequestDTO requestDTO = new ReservaRequestDTO();
        requestDTO.setUsuarioId(null);
        requestDTO.setVideojuegoId(null);

        // When / Then
        mockMvc.perform(post("/api/reservas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.estado").value(400))
                .andExpect(jsonPath("$.error").value("Validación"))
                .andExpect(jsonPath("$.mensaje").exists());

        verify(reservaService, never()).crearReserva(any(ReservaRequestDTO.class));
    }

    @Test
    void crearReserva_CuandoReservaDuplicada_Retorna400() throws Exception {
        // Given
        ReservaRequestDTO requestDTO = crearRequestValido();

        when(reservaService.crearReserva(any(ReservaRequestDTO.class)))
                .thenThrow(new DatoDuplicadoException("El usuario ya tiene una reserva activa para este videojuego"));

        // When / Then
        mockMvc.perform(post("/api/reservas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.estado").value(400))
                .andExpect(jsonPath("$.error").value("Dato duplicado"))
                .andExpect(jsonPath("$.mensaje").value("El usuario ya tiene una reserva activa para este videojuego"));

        verify(reservaService).crearReserva(any(ReservaRequestDTO.class));
    }

    @Test
    void crearReserva_CuandoFeignFalla_Retorna503() throws Exception {
        // Given
        ReservaRequestDTO requestDTO = crearRequestValido();

        when(reservaService.crearReserva(any(ReservaRequestDTO.class)))
                .thenThrow(crearFeignServiceUnavailable());

        // When / Then
        mockMvc.perform(post("/api/reservas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.estado").value(503))
                .andExpect(jsonPath("$.error").value("Error de comunicación"))
                .andExpect(jsonPath("$.mensaje").value("Error al comunicarse con otro microservicio"));

        verify(reservaService).crearReserva(any(ReservaRequestDTO.class));
    }

    @Test
    void listarReservas_CuandoExistenReservas_Retorna200() throws Exception {
        // Given
        List<ReservaResponseDTO> reservas = List.of(
                new ReservaResponseDTO(1L, 1L, 2L, LocalDate.now(), "PENDIENTE"),
                new ReservaResponseDTO(2L, 3L, 4L, LocalDate.now(), "CONFIRMADA")
        );

        when(reservaService.listarReservas()).thenReturn(reservas);

        // When / Then
        mockMvc.perform(get("/api/reservas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].usuarioId").value(1))
                .andExpect(jsonPath("$[1].estado").value("CONFIRMADA"));

        verify(reservaService).listarReservas();
    }

    @Test
    void buscarReservaPorId_CuandoExiste_Retorna200() throws Exception {
        // Given
        ReservaResponseDTO responseDTO = new ReservaResponseDTO(
                1L,
                1L,
                2L,
                LocalDate.now(),
                "PENDIENTE"
        );

        when(reservaService.buscarReservaPorId(1L)).thenReturn(responseDTO);

        // When / Then
        mockMvc.perform(get("/api/reservas/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.usuarioId").value(1))
                .andExpect(jsonPath("$.estado").value("PENDIENTE"));

        verify(reservaService).buscarReservaPorId(1L);
    }

    @Test
    void buscarReservaPorId_CuandoNoExiste_Retorna404() throws Exception {
        // Given
        when(reservaService.buscarReservaPorId(99L))
                .thenThrow(new RecursoNoEncontradoException("Reserva no encontrada"));

        // When / Then
        mockMvc.perform(get("/api/reservas/{id}", 99L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.estado").value(404))
                .andExpect(jsonPath("$.error").value("No encontrado"))
                .andExpect(jsonPath("$.mensaje").value("Reserva no encontrada"));

        verify(reservaService).buscarReservaPorId(99L);
    }

    @Test
    void buscarReservasPorUsuario_CuandoExistenReservas_Retorna200() throws Exception {
        // Given
        List<ReservaResponseDTO> reservas = List.of(
                new ReservaResponseDTO(1L, 1L, 2L, LocalDate.now(), "PENDIENTE")
        );

        when(reservaService.buscarReservasPorUsuario(1L)).thenReturn(reservas);

        // When / Then
        mockMvc.perform(get("/api/reservas/usuario/{usuarioId}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].usuarioId").value(1));

        verify(reservaService).buscarReservasPorUsuario(1L);
    }

    @Test
    void buscarReservasPorVideojuego_CuandoExistenReservas_Retorna200() throws Exception {
        // Given
        List<ReservaResponseDTO> reservas = List.of(
                new ReservaResponseDTO(1L, 1L, 2L, LocalDate.now(), "PENDIENTE")
        );

        when(reservaService.buscarReservasPorVideojuego(2L)).thenReturn(reservas);

        // When / Then
        mockMvc.perform(get("/api/reservas/videojuego/{videojuegoId}", 2L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].videojuegoId").value(2));

        verify(reservaService).buscarReservasPorVideojuego(2L);
    }

    @Test
    void buscarReservasPorEstado_CuandoEstadoValido_Retorna200() throws Exception {
        // Given
        List<ReservaResponseDTO> reservas = List.of(
                new ReservaResponseDTO(1L, 1L, 2L, LocalDate.now(), "PENDIENTE")
        );

        when(reservaService.buscarReservasPorEstado("PENDIENTE")).thenReturn(reservas);

        // When / Then
        mockMvc.perform(get("/api/reservas/estado/{estado}", "PENDIENTE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].estado").value("PENDIENTE"));

        verify(reservaService).buscarReservasPorEstado("PENDIENTE");
    }

    @Test
    void actualizarReserva_CuandoDatosValidos_Retorna200() throws Exception {
        // Given
        ReservaRequestDTO requestDTO = crearRequestValido();
        requestDTO.setEstado("CONFIRMADA");

        ReservaResponseDTO responseDTO = new ReservaResponseDTO(
                1L,
                1L,
                2L,
                LocalDate.now(),
                "CONFIRMADA"
        );

        when(reservaService.actualizarReserva(eq(1L), any(ReservaRequestDTO.class))).thenReturn(responseDTO);

        // When / Then
        mockMvc.perform(put("/api/reservas/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.estado").value("CONFIRMADA"));

        verify(reservaService).actualizarReserva(eq(1L), any(ReservaRequestDTO.class));
    }

    @Test
    void actualizarReserva_CuandoEstadoInvalido_Retorna400() throws Exception {
        // Given
        ReservaRequestDTO requestDTO = crearRequestValido();
        requestDTO.setEstado("BLOQUEADO");

        when(reservaService.actualizarReserva(eq(1L), any(ReservaRequestDTO.class)))
                .thenThrow(new DatoInvalidoException("Estado de reserva inválido. Use PENDIENTE, CONFIRMADA, CANCELADA o EXPIRADA"));

        // When / Then
        mockMvc.perform(put("/api/reservas/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.estado").value(400))
                .andExpect(jsonPath("$.error").value("Dato inválido"))
                .andExpect(jsonPath("$.mensaje").value("Estado de reserva inválido. Use PENDIENTE, CONFIRMADA, CANCELADA o EXPIRADA"));

        verify(reservaService).actualizarReserva(eq(1L), any(ReservaRequestDTO.class));
    }

    @Test
    void confirmarReserva_CuandoExiste_Retorna200() throws Exception {
        // Given
        ReservaResponseDTO responseDTO = new ReservaResponseDTO(
                1L,
                1L,
                2L,
                LocalDate.now(),
                "CONFIRMADA"
        );

        when(reservaService.confirmarReserva(1L)).thenReturn(responseDTO);

        // When / Then
        mockMvc.perform(put("/api/reservas/confirmar/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.estado").value("CONFIRMADA"));

        verify(reservaService).confirmarReserva(1L);
    }

    @Test
    void confirmarReserva_CuandoNoEstaPendiente_Retorna400() throws Exception {
        // Given
        when(reservaService.confirmarReserva(1L))
                .thenThrow(new DatoInvalidoException("Solo se pueden confirmar reservas pendientes"));

        // When / Then
        mockMvc.perform(put("/api/reservas/confirmar/{id}", 1L))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.estado").value(400))
                .andExpect(jsonPath("$.error").value("Dato inválido"))
                .andExpect(jsonPath("$.mensaje").value("Solo se pueden confirmar reservas pendientes"));

        verify(reservaService).confirmarReserva(1L);
    }

    @Test
    void cancelarReserva_CuandoExiste_Retorna200() throws Exception {
        // Given
        ReservaResponseDTO responseDTO = new ReservaResponseDTO(
                1L,
                1L,
                2L,
                LocalDate.now(),
                "CANCELADA"
        );

        when(reservaService.cancelarReserva(1L)).thenReturn(responseDTO);

        // When / Then
        mockMvc.perform(put("/api/reservas/cancelar/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.estado").value("CANCELADA"));

        verify(reservaService).cancelarReserva(1L);
    }

    @Test
    void cancelarReserva_CuandoNoExiste_Retorna404() throws Exception {
        // Given
        when(reservaService.cancelarReserva(99L))
                .thenThrow(new RecursoNoEncontradoException("Reserva no encontrada"));

        // When / Then
        mockMvc.perform(put("/api/reservas/cancelar/{id}", 99L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.estado").value(404))
                .andExpect(jsonPath("$.mensaje").value("Reserva no encontrada"));

        verify(reservaService).cancelarReserva(99L);
    }

    @Test
    void listarHistorialPorReserva_CuandoExisteHistorial_Retorna200() throws Exception {
        // Given
        List<HistorialReservaResponseDTO> historial = List.of(
                new HistorialReservaResponseDTO(
                        1L,
                        1L,
                        "PENDIENTE",
                        "CONFIRMADA",
                        LocalDate.now(),
                        "Reserva confirmada correctamente"
                )
        );

        when(reservaService.listarHistorialPorReserva(1L)).thenReturn(historial);

        // When / Then
        mockMvc.perform(get("/api/reservas/{id}/historial", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].reservaId").value(1))
                .andExpect(jsonPath("$[0].estadoNuevo").value("CONFIRMADA"));

        verify(reservaService).listarHistorialPorReserva(1L);
    }

    @Test
    void eliminarReserva_CuandoExiste_Retorna204() throws Exception {
        // When / Then
        mockMvc.perform(delete("/api/reservas/{id}", 1L))
                .andExpect(status().isNoContent());

        verify(reservaService).eliminarReserva(1L);
    }

    @Test
    void eliminarReserva_CuandoYaEstaCancelada_Retorna400() throws Exception {
        // Given
        doThrow(new DatoInvalidoException("La reserva ya está cancelada"))
                .when(reservaService).eliminarReserva(1L);

        // When / Then
        mockMvc.perform(delete("/api/reservas/{id}", 1L))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.estado").value(400))
                .andExpect(jsonPath("$.error").value("Dato inválido"))
                .andExpect(jsonPath("$.mensaje").value("La reserva ya está cancelada"));

        verify(reservaService).eliminarReserva(1L);
    }

    private ReservaRequestDTO crearRequestValido() {
        ReservaRequestDTO requestDTO = new ReservaRequestDTO();
        requestDTO.setUsuarioId(1L);
        requestDTO.setVideojuegoId(2L);
        requestDTO.setEstado("PENDIENTE");
        return requestDTO;
    }

    private FeignException.ServiceUnavailable crearFeignServiceUnavailable() {
        Request request = Request.create(
                Request.HttpMethod.GET,
                "/api/recurso/1",
                Map.of(),
                null,
                StandardCharsets.UTF_8,
                null
        );

        Map<String, Collection<String>> headers = Collections.emptyMap();

        return new FeignException.ServiceUnavailable(
                "Servicio no disponible",
                request,
                null,
                headers
        );
    }
}
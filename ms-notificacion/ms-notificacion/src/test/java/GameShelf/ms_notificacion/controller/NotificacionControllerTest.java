package GameShelf.ms_notificacion.controller;

import GameShelf.ms_notificacion.dto.NotificacionRequestDTO;
import GameShelf.ms_notificacion.dto.NotificacionResponseDTO;
import GameShelf.ms_notificacion.exception.DatoInvalidoException;
import GameShelf.ms_notificacion.exception.GlobalExceptionHandler;
import GameShelf.ms_notificacion.exception.RecursoNoEncontradoException;
import GameShelf.ms_notificacion.service.NotificacionService;
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
import java.time.LocalDateTime;
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
class NotificacionControllerTest {

    @Mock
    private NotificacionService notificacionService;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        NotificacionController notificacionController = new NotificacionController(notificacionService);

        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();

        mockMvc = MockMvcBuilders
                .standaloneSetup(notificacionController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .setValidator(validator)
                .build();

        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @Test
    void listarNotificaciones_CuandoExistenNotificaciones_Retorna200() throws Exception {
        // Given
        List<NotificacionResponseDTO> notificaciones = List.of(
                crearResponsePendiente(),
                new NotificacionResponseDTO(
                        2L,
                        2L,
                        "Multa generada",
                        "Tienes una multa pendiente",
                        "MULTA",
                        "PENDIENTE",
                        LocalDateTime.now(),
                        null,
                        20L,
                        "MULTA"
                )
        );

        when(notificacionService.listarNotificaciones()).thenReturn(notificaciones);

        // When / Then
        mockMvc.perform(get("/api/notificaciones"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].titulo").value("Reserva creada"))
                .andExpect(jsonPath("$[1].tipo").value("MULTA"));

        verify(notificacionService).listarNotificaciones();
    }

    @Test
    void obtenerNotificacionPorId_CuandoExiste_Retorna200() throws Exception {
        // Given
        when(notificacionService.obtenerNotificacionPorId(1L)).thenReturn(crearResponsePendiente());

        // When / Then
        mockMvc.perform(get("/api/notificaciones/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.usuarioId").value(1))
                .andExpect(jsonPath("$.estado").value("PENDIENTE"));

        verify(notificacionService).obtenerNotificacionPorId(1L);
    }

    @Test
    void obtenerNotificacionPorId_CuandoNoExiste_Retorna404() throws Exception {
        // Given
        when(notificacionService.obtenerNotificacionPorId(99L))
                .thenThrow(new RecursoNoEncontradoException("Notificación no encontrada"));

        // When / Then
        mockMvc.perform(get("/api/notificaciones/{id}", 99L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.estado").value(404))
                .andExpect(jsonPath("$.error").value("No encontrado"))
                .andExpect(jsonPath("$.mensaje").value("Notificación no encontrada"));

        verify(notificacionService).obtenerNotificacionPorId(99L);
    }

    @Test
    void listarPorUsuario_CuandoExistenNotificaciones_Retorna200() throws Exception {
        // Given
        when(notificacionService.listarPorUsuario(1L)).thenReturn(List.of(crearResponsePendiente()));

        // When / Then
        mockMvc.perform(get("/api/notificaciones/usuario/{usuarioId}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].usuarioId").value(1));

        verify(notificacionService).listarPorUsuario(1L);
    }

    @Test
    void listarPorUsuario_CuandoFeignUsuarioNoExiste_Retorna404() throws Exception {
        // Given
        when(notificacionService.listarPorUsuario(99L)).thenThrow(crearFeignNotFound());

        // When / Then
        mockMvc.perform(get("/api/notificaciones/usuario/{usuarioId}", 99L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.estado").value(404))
                .andExpect(jsonPath("$.error").value("No encontrado"))
                .andExpect(jsonPath("$.mensaje").value("El usuario asociado no existe"));

        verify(notificacionService).listarPorUsuario(99L);
    }

    @Test
    void listarPendientesPorUsuario_CuandoExistenPendientes_Retorna200() throws Exception {
        // Given
        when(notificacionService.listarPendientesPorUsuario(1L)).thenReturn(List.of(crearResponsePendiente()));

        // When / Then
        mockMvc.perform(get("/api/notificaciones/usuario/{usuarioId}/pendientes", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].estado").value("PENDIENTE"));

        verify(notificacionService).listarPendientesPorUsuario(1L);
    }

    @Test
    void listarPorEstado_CuandoEstadoValido_Retorna200() throws Exception {
        // Given
        when(notificacionService.listarPorEstado("PENDIENTE")).thenReturn(List.of(crearResponsePendiente()));

        // When / Then
        mockMvc.perform(get("/api/notificaciones/estado/{estado}", "PENDIENTE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].estado").value("PENDIENTE"));

        verify(notificacionService).listarPorEstado("PENDIENTE");
    }

    @Test
    void listarPorEstado_CuandoEstadoInvalido_Retorna400() throws Exception {
        // Given
        when(notificacionService.listarPorEstado("BLOQUEADO"))
                .thenThrow(new DatoInvalidoException("Estado inválido. Debe ser PENDIENTE, LEIDA o ELIMINADA"));

        // When / Then
        mockMvc.perform(get("/api/notificaciones/estado/{estado}", "BLOQUEADO"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.estado").value(400))
                .andExpect(jsonPath("$.error").value("Dato inválido"))
                .andExpect(jsonPath("$.mensaje").value("Estado inválido. Debe ser PENDIENTE, LEIDA o ELIMINADA"));

        verify(notificacionService).listarPorEstado("BLOQUEADO");
    }

    @Test
    void crearNotificacion_CuandoDatosValidos_Retorna201() throws Exception {
        // Given
        NotificacionRequestDTO requestDTO = crearRequestValido();

        when(notificacionService.crearNotificacion(any(NotificacionRequestDTO.class))).thenReturn(crearResponsePendiente());

        // When / Then
        mockMvc.perform(post("/api/notificaciones")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.titulo").value("Reserva creada"))
                .andExpect(jsonPath("$.tipo").value("RESERVA"))
                .andExpect(jsonPath("$.estado").value("PENDIENTE"));

        verify(notificacionService).crearNotificacion(any(NotificacionRequestDTO.class));
    }

    @Test
    void crearNotificacion_CuandoDatosInvalidos_Retorna400() throws Exception {
        // Given
        NotificacionRequestDTO requestDTO = new NotificacionRequestDTO();
        requestDTO.setUsuarioId(null);
        requestDTO.setTitulo("A");
        requestDTO.setMensaje("abc");
        requestDTO.setTipo("");

        // When / Then
        mockMvc.perform(post("/api/notificaciones")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.estado").value(400))
                .andExpect(jsonPath("$.error").value("Validación fallida"))
                .andExpect(jsonPath("$.mensaje").exists());

        verify(notificacionService, never()).crearNotificacion(any(NotificacionRequestDTO.class));
    }

    @Test
    void crearNotificacion_CuandoTipoInvalido_Retorna400() throws Exception {
        // Given
        NotificacionRequestDTO requestDTO = crearRequestValido();
        requestDTO.setTipo("PROMOCION");

        when(notificacionService.crearNotificacion(any(NotificacionRequestDTO.class)))
                .thenThrow(new DatoInvalidoException("Tipo inválido. Debe ser RESERVA, PRESTAMO, MULTA o SISTEMA"));

        // When / Then
        mockMvc.perform(post("/api/notificaciones")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.estado").value(400))
                .andExpect(jsonPath("$.error").value("Dato inválido"))
                .andExpect(jsonPath("$.mensaje").value("Tipo inválido. Debe ser RESERVA, PRESTAMO, MULTA o SISTEMA"));

        verify(notificacionService).crearNotificacion(any(NotificacionRequestDTO.class));
    }

    @Test
    void crearNotificacion_CuandoFeignFalla_Retorna503() throws Exception {
        // Given
        NotificacionRequestDTO requestDTO = crearRequestValido();

        when(notificacionService.crearNotificacion(any(NotificacionRequestDTO.class)))
                .thenThrow(crearFeignServiceUnavailable());

        // When / Then
        mockMvc.perform(post("/api/notificaciones")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.estado").value(503))
                .andExpect(jsonPath("$.error").value("Servicio no disponible"))
                .andExpect(jsonPath("$.mensaje").value("No se pudo comunicar con otro microservicio"));

        verify(notificacionService).crearNotificacion(any(NotificacionRequestDTO.class));
    }

    @Test
    void actualizarNotificacion_CuandoDatosValidos_Retorna200() throws Exception {
        // Given
        NotificacionRequestDTO requestDTO = crearRequestValido();
        requestDTO.setEstado("LEIDA");

        NotificacionResponseDTO responseDTO = new NotificacionResponseDTO(
                1L,
                1L,
                "Reserva creada",
                "Tu reserva fue creada correctamente",
                "RESERVA",
                "LEIDA",
                LocalDateTime.now(),
                LocalDateTime.now(),
                10L,
                "RESERVA"
        );

        when(notificacionService.actualizarNotificacion(eq(1L), any(NotificacionRequestDTO.class))).thenReturn(responseDTO);

        // When / Then
        mockMvc.perform(put("/api/notificaciones/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.estado").value("LEIDA"));

        verify(notificacionService).actualizarNotificacion(eq(1L), any(NotificacionRequestDTO.class));
    }

    @Test
    void actualizarNotificacion_CuandoNoExiste_Retorna404() throws Exception {
        // Given
        NotificacionRequestDTO requestDTO = crearRequestValido();

        when(notificacionService.actualizarNotificacion(eq(99L), any(NotificacionRequestDTO.class)))
                .thenThrow(new RecursoNoEncontradoException("Notificación no encontrada"));

        // When / Then
        mockMvc.perform(put("/api/notificaciones/{id}", 99L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.estado").value(404))
                .andExpect(jsonPath("$.mensaje").value("Notificación no encontrada"));

        verify(notificacionService).actualizarNotificacion(eq(99L), any(NotificacionRequestDTO.class));
    }

    @Test
    void marcarComoLeida_CuandoExiste_Retorna200() throws Exception {
        // Given
        NotificacionResponseDTO responseDTO = new NotificacionResponseDTO(
                1L,
                1L,
                "Reserva creada",
                "Tu reserva fue creada correctamente",
                "RESERVA",
                "LEIDA",
                LocalDateTime.now(),
                LocalDateTime.now(),
                10L,
                "RESERVA"
        );

        when(notificacionService.marcarComoLeida(1L)).thenReturn(responseDTO);

        // When / Then
        mockMvc.perform(put("/api/notificaciones/{id}/leer", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.estado").value("LEIDA"));

        verify(notificacionService).marcarComoLeida(1L);
    }

    @Test
    void marcarComoLeida_CuandoYaEstaLeida_Retorna400() throws Exception {
        // Given
        when(notificacionService.marcarComoLeida(1L))
                .thenThrow(new DatoInvalidoException("La notificación ya está leída"));

        // When / Then
        mockMvc.perform(put("/api/notificaciones/{id}/leer", 1L))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.estado").value(400))
                .andExpect(jsonPath("$.error").value("Dato inválido"))
                .andExpect(jsonPath("$.mensaje").value("La notificación ya está leída"));

        verify(notificacionService).marcarComoLeida(1L);
    }

    @Test
    void eliminarNotificacion_CuandoExiste_Retorna204() throws Exception {
        // When / Then
        mockMvc.perform(delete("/api/notificaciones/{id}", 1L))
                .andExpect(status().isNoContent());

        verify(notificacionService).eliminarNotificacion(1L);
    }

    @Test
    void eliminarNotificacion_CuandoYaEstaEliminada_Retorna400() throws Exception {
        // Given
        doThrow(new DatoInvalidoException("La notificación ya está eliminada"))
                .when(notificacionService).eliminarNotificacion(1L);

        // When / Then
        mockMvc.perform(delete("/api/notificaciones/{id}", 1L))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.estado").value(400))
                .andExpect(jsonPath("$.error").value("Dato inválido"))
                .andExpect(jsonPath("$.mensaje").value("La notificación ya está eliminada"));

        verify(notificacionService).eliminarNotificacion(1L);
    }

    private NotificacionRequestDTO crearRequestValido() {
        NotificacionRequestDTO requestDTO = new NotificacionRequestDTO();
        requestDTO.setUsuarioId(1L);
        requestDTO.setTitulo("Reserva creada");
        requestDTO.setMensaje("Tu reserva fue creada correctamente");
        requestDTO.setTipo("RESERVA");
        requestDTO.setEstado("PENDIENTE");
        requestDTO.setReferenciaId(10L);
        requestDTO.setReferenciaTipo("RESERVA");
        return requestDTO;
    }

    private NotificacionResponseDTO crearResponsePendiente() {
        return new NotificacionResponseDTO(
                1L,
                1L,
                "Reserva creada",
                "Tu reserva fue creada correctamente",
                "RESERVA",
                "PENDIENTE",
                LocalDateTime.now(),
                null,
                10L,
                "RESERVA"
        );
    }

    private FeignException.NotFound crearFeignNotFound() {
        Request request = crearRequestFeign();
        Map<String, Collection<String>> headers = Collections.emptyMap();

        return new FeignException.NotFound(
                "Usuario no encontrado",
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
                "/api/usuarios/1",
                Map.of(),
                null,
                StandardCharsets.UTF_8,
                null
        );
    }
}

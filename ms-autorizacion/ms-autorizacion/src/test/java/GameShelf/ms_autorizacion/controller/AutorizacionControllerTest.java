package GameShelf.ms_autorizacion.controller;

import GameShelf.ms_autorizacion.dto.AutorizacionRequestDTO;
import GameShelf.ms_autorizacion.dto.AutorizacionResponseDTO;
import GameShelf.ms_autorizacion.dto.ValidarAutorizacionRequestDTO;
import GameShelf.ms_autorizacion.exception.DatoInvalidoException;
import GameShelf.ms_autorizacion.exception.GlobalExceptionHandler;
import GameShelf.ms_autorizacion.exception.RecursoNoEncontradoException;
import GameShelf.ms_autorizacion.service.AutorizacionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class AutorizacionControllerTest {

    @Mock
    private AutorizacionService autorizacionService;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        AutorizacionController autorizacionController = new AutorizacionController(autorizacionService);

        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();

        mockMvc = MockMvcBuilders
                .standaloneSetup(autorizacionController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .setValidator(validator)
                .build();

        objectMapper = new ObjectMapper();
    }

    @Test
    void crearAutorizacion_CuandoDatosValidos_Retorna201() throws Exception {
        // Given
        AutorizacionRequestDTO requestDTO = crearRequestValido();

        AutorizacionResponseDTO responseDTO = new AutorizacionResponseDTO(
                1L,
                1L,
                "ADMINISTRADOR",
                "PRESTAMOS",
                "GESTIONAR_PRESTAMOS",
                "ACTIVO"
        );

        when(autorizacionService.crearAutorizacion(any(AutorizacionRequestDTO.class))).thenReturn(responseDTO);

        // When / Then
        mockMvc.perform(post("/api/autorizaciones")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.usuarioId").value(1))
                .andExpect(jsonPath("$.rol").value("ADMINISTRADOR"))
                .andExpect(jsonPath("$.modulo").value("PRESTAMOS"))
                .andExpect(jsonPath("$.permiso").value("GESTIONAR_PRESTAMOS"))
                .andExpect(jsonPath("$.estado").value("ACTIVO"));

        verify(autorizacionService).crearAutorizacion(any(AutorizacionRequestDTO.class));
    }

    @Test
    void crearAutorizacion_CuandoDatosInvalidos_Retorna400() throws Exception {
        // Given
        AutorizacionRequestDTO requestDTO = new AutorizacionRequestDTO();
        requestDTO.setUsuarioId(null);
        requestDTO.setRol("");
        requestDTO.setModulo("");
        requestDTO.setPermiso("");
        requestDTO.setEstado("");

        // When / Then
        mockMvc.perform(post("/api/autorizaciones")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.estado").value(400))
                .andExpect(jsonPath("$.error").value("Error de validación"))
                .andExpect(jsonPath("$.mensajes", hasKey("usuarioId")))
                .andExpect(jsonPath("$.mensajes", hasKey("rol")))
                .andExpect(jsonPath("$.mensajes", hasKey("modulo")))
                .andExpect(jsonPath("$.mensajes", hasKey("permiso")))
                .andExpect(jsonPath("$.mensajes", hasKey("estado")));

        verify(autorizacionService, never()).crearAutorizacion(any(AutorizacionRequestDTO.class));
    }

    @Test
    void crearAutorizacion_CuandoReglaNegocioFalla_Retorna400() throws Exception {
        // Given
        AutorizacionRequestDTO requestDTO = crearRequestValido();

        when(autorizacionService.crearAutorizacion(any(AutorizacionRequestDTO.class)))
                .thenThrow(new DatoInvalidoException("El rol no existe"));

        // When / Then
        mockMvc.perform(post("/api/autorizaciones")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.estado").value(400))
                .andExpect(jsonPath("$.error").value("Dato inválido"))
                .andExpect(jsonPath("$.mensaje").value("El rol no existe"));

        verify(autorizacionService).crearAutorizacion(any(AutorizacionRequestDTO.class));
    }

    @Test
    void crearAutorizacion_CuandoErrorInesperado_Retorna500() throws Exception {
        // Given
        AutorizacionRequestDTO requestDTO = crearRequestValido();

        when(autorizacionService.crearAutorizacion(any(AutorizacionRequestDTO.class)))
                .thenThrow(new RuntimeException("Error inesperado"));

        // When / Then
        mockMvc.perform(post("/api/autorizaciones")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.estado").value(500))
                .andExpect(jsonPath("$.error").value("Error interno del servidor"))
                .andExpect(jsonPath("$.mensaje").value("Error inesperado"));

        verify(autorizacionService).crearAutorizacion(any(AutorizacionRequestDTO.class));
    }

    @Test
    void listarAutorizaciones_CuandoExistenAutorizaciones_Retorna200() throws Exception {
        // Given
        List<AutorizacionResponseDTO> autorizaciones = List.of(
                new AutorizacionResponseDTO(1L, 1L, "ADMINISTRADOR", "PRESTAMOS", "GESTIONAR_PRESTAMOS", "ACTIVO"),
                new AutorizacionResponseDTO(2L, 3L, "CLIENTE", "CATALOGO", "VER_CATALOGO", "ACTIVO")
        );

        when(autorizacionService.listarAutorizaciones()).thenReturn(autorizaciones);

        // When / Then
        mockMvc.perform(get("/api/autorizaciones"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].rol").value("ADMINISTRADOR"))
                .andExpect(jsonPath("$[1].modulo").value("CATALOGO"));

        verify(autorizacionService).listarAutorizaciones();
    }

    @Test
    void obtenerAutorizacionPorId_CuandoExiste_Retorna200() throws Exception {
        // Given
        AutorizacionResponseDTO responseDTO = new AutorizacionResponseDTO(
                1L,
                1L,
                "ADMINISTRADOR",
                "PRESTAMOS",
                "GESTIONAR_PRESTAMOS",
                "ACTIVO"
        );

        when(autorizacionService.obtenerAutorizacionPorId(1L)).thenReturn(responseDTO);

        // When / Then
        mockMvc.perform(get("/api/autorizaciones/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.usuarioId").value(1))
                .andExpect(jsonPath("$.estado").value("ACTIVO"));

        verify(autorizacionService).obtenerAutorizacionPorId(1L);
    }

    @Test
    void obtenerAutorizacionPorId_CuandoNoExiste_Retorna404() throws Exception {
        // Given
        when(autorizacionService.obtenerAutorizacionPorId(99L))
                .thenThrow(new RecursoNoEncontradoException("Autorización no encontrada"));

        // When / Then
        mockMvc.perform(get("/api/autorizaciones/{id}", 99L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.estado").value(404))
                .andExpect(jsonPath("$.error").value("Recurso no encontrado"))
                .andExpect(jsonPath("$.mensaje").value("Autorización no encontrada"));

        verify(autorizacionService).obtenerAutorizacionPorId(99L);
    }

    @Test
    void listarPorUsuario_CuandoExistenAutorizaciones_Retorna200() throws Exception {
        // Given
        List<AutorizacionResponseDTO> autorizaciones = List.of(
                new AutorizacionResponseDTO(1L, 1L, "ADMINISTRADOR", "PRESTAMOS", "GESTIONAR_PRESTAMOS", "ACTIVO")
        );

        when(autorizacionService.listarPorUsuario(1L)).thenReturn(autorizaciones);

        // When / Then
        mockMvc.perform(get("/api/autorizaciones/usuario/{usuarioId}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].usuarioId").value(1));

        verify(autorizacionService).listarPorUsuario(1L);
    }

    @Test
    void actualizarAutorizacion_CuandoDatosValidos_Retorna200() throws Exception {
        // Given
        AutorizacionRequestDTO requestDTO = crearRequestValido();

        AutorizacionResponseDTO responseDTO = new AutorizacionResponseDTO(
                1L,
                1L,
                "ADMINISTRADOR",
                "PRESTAMOS",
                "GESTIONAR_PRESTAMOS",
                "ACTIVO"
        );

        when(autorizacionService.actualizarAutorizacion(eq(1L), any(AutorizacionRequestDTO.class)))
                .thenReturn(responseDTO);

        // When / Then
        mockMvc.perform(put("/api/autorizaciones/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.modulo").value("PRESTAMOS"));

        verify(autorizacionService).actualizarAutorizacion(eq(1L), any(AutorizacionRequestDTO.class));
    }

    @Test
    void actualizarAutorizacion_CuandoEstadoInvalido_Retorna400() throws Exception {
        // Given
        AutorizacionRequestDTO requestDTO = crearRequestValido();
        requestDTO.setEstado("BLOQUEADO");

        when(autorizacionService.actualizarAutorizacion(eq(1L), any(AutorizacionRequestDTO.class)))
                .thenThrow(new DatoInvalidoException("El estado debe ser ACTIVO o INACTIVO"));

        // When / Then
        mockMvc.perform(put("/api/autorizaciones/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.estado").value(400))
                .andExpect(jsonPath("$.error").value("Dato inválido"))
                .andExpect(jsonPath("$.mensaje").value("El estado debe ser ACTIVO o INACTIVO"));

        verify(autorizacionService).actualizarAutorizacion(eq(1L), any(AutorizacionRequestDTO.class));
    }

    @Test
    void eliminarAutorizacion_CuandoExiste_Retorna204() throws Exception {
        // When / Then
        mockMvc.perform(delete("/api/autorizaciones/{id}", 1L))
                .andExpect(status().isNoContent());

        verify(autorizacionService).eliminarAutorizacion(1L);
    }

    @Test
    void eliminarAutorizacion_CuandoNoExiste_Retorna404() throws Exception {
        // Given
        doThrow(new RecursoNoEncontradoException("Autorización no encontrada"))
                .when(autorizacionService).eliminarAutorizacion(99L);

        // When / Then
        mockMvc.perform(delete("/api/autorizaciones/{id}", 99L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.estado").value(404))
                .andExpect(jsonPath("$.mensaje").value("Autorización no encontrada"));

        verify(autorizacionService).eliminarAutorizacion(99L);
    }

    @Test
    void eliminarAutorizacion_CuandoYaEstaInactiva_Retorna400() throws Exception {
        // Given
        doThrow(new DatoInvalidoException("La autorización ya está inactiva"))
                .when(autorizacionService).eliminarAutorizacion(1L);

        // When / Then
        mockMvc.perform(delete("/api/autorizaciones/{id}", 1L))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.estado").value(400))
                .andExpect(jsonPath("$.error").value("Dato inválido"))
                .andExpect(jsonPath("$.mensaje").value("La autorización ya está inactiva"));

        verify(autorizacionService).eliminarAutorizacion(1L);
    }

    @Test
    void validarAutorizacion_CuandoTienePermiso_RetornaTrue() throws Exception {
        // Given
        ValidarAutorizacionRequestDTO requestDTO = crearValidarRequestValido();

        when(autorizacionService.validarAutorizacion(any(ValidarAutorizacionRequestDTO.class))).thenReturn(true);

        // When / Then
        mockMvc.perform(post("/api/autorizaciones/validar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));

        verify(autorizacionService).validarAutorizacion(any(ValidarAutorizacionRequestDTO.class));
    }

    @Test
    void validarAutorizacion_CuandoDatosInvalidos_Retorna400() throws Exception {
        // Given
        ValidarAutorizacionRequestDTO requestDTO = new ValidarAutorizacionRequestDTO();
        requestDTO.setUsuarioId(null);
        requestDTO.setModulo("");
        requestDTO.setPermiso("");

        // When / Then
        mockMvc.perform(post("/api/autorizaciones/validar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.estado").value(400))
                .andExpect(jsonPath("$.error").value("Error de validación"))
                .andExpect(jsonPath("$.mensajes", hasKey("usuarioId")))
                .andExpect(jsonPath("$.mensajes", hasKey("modulo")))
                .andExpect(jsonPath("$.mensajes", hasKey("permiso")));

        verify(autorizacionService, never()).validarAutorizacion(any(ValidarAutorizacionRequestDTO.class));
    }

    private AutorizacionRequestDTO crearRequestValido() {
        AutorizacionRequestDTO requestDTO = new AutorizacionRequestDTO();
        requestDTO.setUsuarioId(1L);
        requestDTO.setRol("ADMINISTRADOR");
        requestDTO.setModulo("PRESTAMOS");
        requestDTO.setPermiso("GESTIONAR_PRESTAMOS");
        requestDTO.setEstado("ACTIVO");
        return requestDTO;
    }

    private ValidarAutorizacionRequestDTO crearValidarRequestValido() {
        ValidarAutorizacionRequestDTO requestDTO = new ValidarAutorizacionRequestDTO();
        requestDTO.setUsuarioId(1L);
        requestDTO.setModulo("PRESTAMOS");
        requestDTO.setPermiso("GESTIONAR_PRESTAMOS");
        return requestDTO;
    }
}
package GameShelf.ms_roles.controller;

import java.util.List;

import static org.hamcrest.Matchers.hasKey;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.Mock;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import com.fasterxml.jackson.databind.ObjectMapper;

import GameShelf.ms_roles.dto.RolRequestDTO;
import GameShelf.ms_roles.dto.RolResponseDTO;
import GameShelf.ms_roles.exception.DatoDuplicadoException;
import GameShelf.ms_roles.exception.GlobalExceptionHandler;
import GameShelf.ms_roles.exception.RolNoEncontradoException;
import GameShelf.ms_roles.service.RolService;

@ExtendWith(MockitoExtension.class)
class RolControllerTest {

    @Mock
    private RolService rolService;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        RolController rolController = new RolController(rolService);

        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();

        mockMvc = MockMvcBuilders
                .standaloneSetup(rolController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .setValidator(validator)
                .build();

        objectMapper = new ObjectMapper();
    }

    @Test
    void crearRol_CuandoDatosValidos_Retorna201() throws Exception {
        // Given
        RolRequestDTO requestDTO = new RolRequestDTO();
        requestDTO.setNombre("CLIENTE");
        requestDTO.setDescripcion("Usuario cliente del sistema");
        requestDTO.setEstado("ACTIVO");

        RolResponseDTO responseDTO = new RolResponseDTO(
                1L,
                "CLIENTE",
                "Usuario cliente del sistema",
                "ACTIVO"
        );

        when(rolService.crearRol(any(RolRequestDTO.class))).thenReturn(responseDTO);

        // When / Then
        mockMvc.perform(post("/api/roles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nombre").value("CLIENTE"))
                .andExpect(jsonPath("$.descripcion").value("Usuario cliente del sistema"))
                .andExpect(jsonPath("$.estado").value("ACTIVO"));

        verify(rolService).crearRol(any(RolRequestDTO.class));
    }

    @Test
    void crearRol_CuandoDatosInvalidos_Retorna400() throws Exception {
        // Given
        RolRequestDTO requestDTO = new RolRequestDTO();
        requestDTO.setNombre("AB");
        requestDTO.setDescripcion("1234");
        requestDTO.setEstado("ACTIVO");

        // When / Then
        mockMvc.perform(post("/api/roles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.estado").value(400))
                .andExpect(jsonPath("$.error").value("Validación fallida"))
                .andExpect(jsonPath("$.mensajes", hasKey("nombre")))
                .andExpect(jsonPath("$.mensajes", hasKey("descripcion")));

        verify(rolService, never()).crearRol(any(RolRequestDTO.class));
    }

    @Test
    void crearRol_CuandoRolDuplicado_Retorna400() throws Exception {
        // Given
        RolRequestDTO requestDTO = new RolRequestDTO();
        requestDTO.setNombre("CLIENTE");
        requestDTO.setDescripcion("Usuario cliente del sistema");
        requestDTO.setEstado("ACTIVO");

        when(rolService.crearRol(any(RolRequestDTO.class)))
                .thenThrow(new DatoDuplicadoException("El rol ya existe"));

        // When / Then
        mockMvc.perform(post("/api/roles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.estado").value(400))
                .andExpect(jsonPath("$.error").value("Dato inválido"))
                .andExpect(jsonPath("$.mensaje").value("El rol ya existe"));

        verify(rolService).crearRol(any(RolRequestDTO.class));
    }

    @Test
    void listarRoles_CuandoExistenRoles_Retorna200() throws Exception {
        // Given
        List<RolResponseDTO> roles = List.of(
                new RolResponseDTO(1L, "CLIENTE", "Usuario cliente del sistema", "ACTIVO"),
                new RolResponseDTO(2L, "ADMINISTRADOR", "Administrador del sistema", "ACTIVO")
        );

        when(rolService.listarRoles()).thenReturn(roles);

        // When / Then
        mockMvc.perform(get("/api/roles"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].nombre").value("CLIENTE"))
                .andExpect(jsonPath("$[1].nombre").value("ADMINISTRADOR"));

        verify(rolService).listarRoles();
    }

    @Test
    void buscarPorId_CuandoRolExiste_Retorna200() throws Exception {
        // Given
        RolResponseDTO responseDTO = new RolResponseDTO(
                1L,
                "CLIENTE",
                "Usuario cliente del sistema",
                "ACTIVO"
        );

        when(rolService.buscarPorId(1L)).thenReturn(responseDTO);

        // When / Then
        mockMvc.perform(get("/api/roles/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nombre").value("CLIENTE"))
                .andExpect(jsonPath("$.estado").value("ACTIVO"));

        verify(rolService).buscarPorId(1L);
    }

    @Test
    void buscarPorId_CuandoRolNoExiste_Retorna404() throws Exception {
        // Given
        when(rolService.buscarPorId(99L))
                .thenThrow(new RolNoEncontradoException("Rol no encontrado con ID: 99"));

        // When / Then
        mockMvc.perform(get("/api/roles/{id}", 99L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.estado").value(404))
                .andExpect(jsonPath("$.error").value("No encontrado"))
                .andExpect(jsonPath("$.mensaje").value("Rol no encontrado con ID: 99"));

        verify(rolService).buscarPorId(99L);
    }

    @Test
    void actualizarRol_CuandoDatosValidos_Retorna200() throws Exception {
        // Given
        RolRequestDTO requestDTO = new RolRequestDTO();
        requestDTO.setNombre("ADMINISTRADOR");
        requestDTO.setDescripcion("Administrador del sistema");
        requestDTO.setEstado("ACTIVO");

        RolResponseDTO responseDTO = new RolResponseDTO(
                1L,
                "ADMINISTRADOR",
                "Administrador del sistema",
                "ACTIVO"
        );

        when(rolService.actualizarRol(eq(1L), any(RolRequestDTO.class)))
                .thenReturn(responseDTO);

        // When / Then
        mockMvc.perform(put("/api/roles/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nombre").value("ADMINISTRADOR"))
                .andExpect(jsonPath("$.descripcion").value("Administrador del sistema"))
                .andExpect(jsonPath("$.estado").value("ACTIVO"));

        verify(rolService).actualizarRol(eq(1L), any(RolRequestDTO.class));
    }

    @Test
    void actualizarRol_CuandoEstadoInvalido_Retorna400() throws Exception {
        // Given
        RolRequestDTO requestDTO = new RolRequestDTO();
        requestDTO.setNombre("CLIENTE");
        requestDTO.setDescripcion("Usuario cliente del sistema");
        requestDTO.setEstado("BLOQUEADO");

        when(rolService.actualizarRol(eq(1L), any(RolRequestDTO.class)))
                .thenThrow(new DatoDuplicadoException("El estado debe ser ACTIVO o INACTIVO"));

        // When / Then
        mockMvc.perform(put("/api/roles/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.estado").value(400))
                .andExpect(jsonPath("$.error").value("Dato inválido"))
                .andExpect(jsonPath("$.mensaje").value("El estado debe ser ACTIVO o INACTIVO"));

        verify(rolService).actualizarRol(eq(1L), any(RolRequestDTO.class));
    }

    @Test
    void eliminarRol_CuandoExiste_Retorna200() throws Exception {
        // When / Then
        mockMvc.perform(delete("/api/roles/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mensaje").value("Rol desactivado correctamente"));

        verify(rolService).eliminarRol(1L);
    }

    @Test
    void buscarPorEstado_CuandoEstadoValido_Retorna200() throws Exception {
        // Given
        List<RolResponseDTO> roles = List.of(
                new RolResponseDTO(1L, "CLIENTE", "Usuario cliente del sistema", "ACTIVO")
        );

        when(rolService.buscarPorEstado("ACTIVO")).thenReturn(roles);

        // When / Then
        mockMvc.perform(get("/api/roles/estado/{estado}", "ACTIVO"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].estado").value("ACTIVO"));

        verify(rolService).buscarPorEstado("ACTIVO");
    }

    @Test
    void buscarPorNombre_CuandoExistenCoincidencias_Retorna200() throws Exception {
        // Given
        List<RolResponseDTO> roles = List.of(
                new RolResponseDTO(1L, "CLIENTE", "Usuario cliente del sistema", "ACTIVO")
        );

        when(rolService.buscarPorNombre("cli")).thenReturn(roles);

        // When / Then
        mockMvc.perform(get("/api/roles/buscar/{nombre}", "cli"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].nombre").value("CLIENTE"));

        verify(rolService).buscarPorNombre("cli");
    }

    @Test
    void buscarPorNombreExacto_CuandoExiste_Retorna200() throws Exception {
        // Given
        RolResponseDTO responseDTO = new RolResponseDTO(
                1L,
                "CLIENTE",
                "Usuario cliente del sistema",
                "ACTIVO"
        );

        when(rolService.buscarPorNombreExacto("CLIENTE")).thenReturn(responseDTO);

        // When / Then
        mockMvc.perform(get("/api/roles/nombre/{nombre}", "CLIENTE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nombre").value("CLIENTE"));

        verify(rolService).buscarPorNombreExacto("CLIENTE");
    }

    @Test
    void validarRol_CuandoRolEstaActivo_RetornaTrue() throws Exception {
        // Given
        when(rolService.validarRolActivo("CLIENTE")).thenReturn(true);

        // When / Then
        mockMvc.perform(get("/api/roles/validar/{nombre}", "CLIENTE"))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));

        verify(rolService).validarRolActivo("CLIENTE");
    }
}
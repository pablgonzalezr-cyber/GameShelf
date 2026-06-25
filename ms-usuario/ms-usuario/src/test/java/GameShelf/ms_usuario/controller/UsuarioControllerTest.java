package GameShelf.ms_usuario.controller;

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

import GameShelf.ms_usuario.dto.UsuarioRequestDTO;
import GameShelf.ms_usuario.dto.UsuarioResponseDTO;
import GameShelf.ms_usuario.dto.UsuarioUpdateDTO;
import GameShelf.ms_usuario.exception.ComunicacionRolException;
import GameShelf.ms_usuario.exception.DatoDuplicadoException;
import GameShelf.ms_usuario.exception.GlobalExceptionHandler;
import GameShelf.ms_usuario.exception.UsuarioNoEncontradoException;
import GameShelf.ms_usuario.service.UsuarioService;

@ExtendWith(MockitoExtension.class)
class UsuarioControllerTest {

    @Mock
    private UsuarioService usuarioService;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        UsuarioController usuarioController = new UsuarioController(usuarioService);

        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();

        mockMvc = MockMvcBuilders
                .standaloneSetup(usuarioController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .setValidator(validator)
                .build();

        objectMapper = new ObjectMapper();
    }

    @Test
    void crearUsuario_CuandoDatosValidos_Retorna201() throws Exception {
        // Given
        UsuarioRequestDTO requestDTO = new UsuarioRequestDTO();
        requestDTO.setUsuario("pablo");
        requestDTO.setContrasena("1234");
        requestDTO.setCorreo("pablo@gmail.com");
        requestDTO.setRol("CLIENTE");

        UsuarioResponseDTO responseDTO = new UsuarioResponseDTO(
                1L,
                "pablo",
                "pablo@gmail.com",
                "CLIENTE"
        );

        when(usuarioService.crearUsuario(any(UsuarioRequestDTO.class))).thenReturn(responseDTO);

        // When / Then
        mockMvc.perform(post("/api/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.usuario").value("pablo"))
                .andExpect(jsonPath("$.correo").value("pablo@gmail.com"))
                .andExpect(jsonPath("$.rol").value("CLIENTE"));

        verify(usuarioService).crearUsuario(any(UsuarioRequestDTO.class));
    }

    @Test
    void crearUsuario_CuandoDatosInvalidos_Retorna400() throws Exception {
        // Given
        UsuarioRequestDTO requestDTO = new UsuarioRequestDTO();
        requestDTO.setUsuario("ab");
        requestDTO.setContrasena("123");
        requestDTO.setCorreo("correo-invalido");
        requestDTO.setRol("CLIENTE");

        // When / Then
        mockMvc.perform(post("/api/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.estado").value(400))
                .andExpect(jsonPath("$.error").value("Validación fallida"))
                .andExpect(jsonPath("$.mensajes", hasKey("usuario")))
                .andExpect(jsonPath("$.mensajes", hasKey("contrasena")))
                .andExpect(jsonPath("$.mensajes", hasKey("correo")));

        verify(usuarioService, never()).crearUsuario(any(UsuarioRequestDTO.class));
    }

    @Test
    void crearUsuario_CuandoUsuarioDuplicado_Retorna400() throws Exception {
        // Given
        UsuarioRequestDTO requestDTO = new UsuarioRequestDTO();
        requestDTO.setUsuario("pablo");
        requestDTO.setContrasena("1234");
        requestDTO.setCorreo("pablo@gmail.com");
        requestDTO.setRol("CLIENTE");

        when(usuarioService.crearUsuario(any(UsuarioRequestDTO.class)))
                .thenThrow(new DatoDuplicadoException("El nombre de usuario ya existe"));

        // When / Then
        mockMvc.perform(post("/api/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.estado").value(400))
                .andExpect(jsonPath("$.error").value("Dato inválido"))
                .andExpect(jsonPath("$.mensaje").value("El nombre de usuario ya existe"));

        verify(usuarioService).crearUsuario(any(UsuarioRequestDTO.class));
    }

    @Test
    void crearUsuario_CuandoRolNoDisponible_Retorna503() throws Exception {
        // Given
        UsuarioRequestDTO requestDTO = new UsuarioRequestDTO();
        requestDTO.setUsuario("pablo");
        requestDTO.setContrasena("1234");
        requestDTO.setCorreo("pablo@gmail.com");
        requestDTO.setRol("CLIENTE");

        when(usuarioService.crearUsuario(any(UsuarioRequestDTO.class)))
                .thenThrow(new ComunicacionRolException("No se pudo validar el rol con ms-roles"));

        // When / Then
        mockMvc.perform(post("/api/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.estado").value(503))
                .andExpect(jsonPath("$.error").value("Servicio no disponible"))
                .andExpect(jsonPath("$.mensaje").value("No se pudo validar el rol con ms-roles"));

        verify(usuarioService).crearUsuario(any(UsuarioRequestDTO.class));
    }

    @Test
    void listarUsuarios_CuandoExistenUsuarios_Retorna200() throws Exception {
        // Given
        List<UsuarioResponseDTO> usuarios = List.of(
                new UsuarioResponseDTO(1L, "pablo", "pablo@gmail.com", "CLIENTE"),
                new UsuarioResponseDTO(2L, "gabriel", "gabriel@gmail.com", "ADMINISTRADOR")
        );

        when(usuarioService.listarUsuarios()).thenReturn(usuarios);

        // When / Then
        mockMvc.perform(get("/api/usuarios"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].usuario").value("pablo"))
                .andExpect(jsonPath("$[1].usuario").value("gabriel"));

        verify(usuarioService).listarUsuarios();
    }

    @Test
    void buscarUsuario_CuandoExiste_Retorna200() throws Exception {
        // Given
        UsuarioResponseDTO responseDTO = new UsuarioResponseDTO(
                1L,
                "pablo",
                "pablo@gmail.com",
                "CLIENTE"
        );

        when(usuarioService.buscarPorId(1L)).thenReturn(responseDTO);

        // When / Then
        mockMvc.perform(get("/api/usuarios/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.usuario").value("pablo"))
                .andExpect(jsonPath("$.rol").value("CLIENTE"));

        verify(usuarioService).buscarPorId(1L);
    }

    @Test
    void buscarUsuario_CuandoNoExiste_Retorna404() throws Exception {
        // Given
        when(usuarioService.buscarPorId(99L))
                .thenThrow(new UsuarioNoEncontradoException("Usuario no encontrado con ID: 99"));

        // When / Then
        mockMvc.perform(get("/api/usuarios/{id}", 99L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.estado").value(404))
                .andExpect(jsonPath("$.error").value("No encontrado"))
                .andExpect(jsonPath("$.mensaje").value("Usuario no encontrado con ID: 99"));

        verify(usuarioService).buscarPorId(99L);
    }

    @Test
    void actualizarUsuario_CuandoDatosValidos_Retorna200() throws Exception {
        // Given
        UsuarioUpdateDTO updateDTO = new UsuarioUpdateDTO();
        updateDTO.setUsuario("pablo_actualizado");
        updateDTO.setContrasena("12345");
        updateDTO.setCorreo("pablo.actualizado@gmail.com");
        updateDTO.setRol("ADMINISTRADOR");

        UsuarioResponseDTO responseDTO = new UsuarioResponseDTO(
                1L,
                "pablo_actualizado",
                "pablo.actualizado@gmail.com",
                "ADMINISTRADOR"
        );

        when(usuarioService.actualizarUsuario(eq(1L), any(UsuarioUpdateDTO.class)))
                .thenReturn(responseDTO);

        // When / Then
        mockMvc.perform(put("/api/usuarios/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.usuario").value("pablo_actualizado"))
                .andExpect(jsonPath("$.correo").value("pablo.actualizado@gmail.com"))
                .andExpect(jsonPath("$.rol").value("ADMINISTRADOR"));

        verify(usuarioService).actualizarUsuario(eq(1L), any(UsuarioUpdateDTO.class));
    }

    @Test
    void actualizarUsuario_CuandoDatosInvalidos_Retorna400() throws Exception {
        // Given
        UsuarioUpdateDTO updateDTO = new UsuarioUpdateDTO();
        updateDTO.setUsuario("ab");
        updateDTO.setContrasena("123");
        updateDTO.setCorreo("correo-invalido");
        updateDTO.setRol("ADMINISTRADOR");

        // When / Then
        mockMvc.perform(put("/api/usuarios/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.estado").value(400))
                .andExpect(jsonPath("$.error").value("Validación fallida"))
                .andExpect(jsonPath("$.mensajes", hasKey("usuario")))
                .andExpect(jsonPath("$.mensajes", hasKey("contrasena")))
                .andExpect(jsonPath("$.mensajes", hasKey("correo")));

        verify(usuarioService, never()).actualizarUsuario(eq(1L), any(UsuarioUpdateDTO.class));
    }

    @Test
    void actualizarUsuario_CuandoNoExiste_Retorna404() throws Exception {
        // Given
        UsuarioUpdateDTO updateDTO = new UsuarioUpdateDTO();
        updateDTO.setUsuario("pablo_actualizado");
        updateDTO.setContrasena("12345");
        updateDTO.setCorreo("pablo.actualizado@gmail.com");
        updateDTO.setRol("ADMINISTRADOR");

        when(usuarioService.actualizarUsuario(eq(99L), any(UsuarioUpdateDTO.class)))
                .thenThrow(new UsuarioNoEncontradoException("Usuario no encontrado con ID: 99"));

        // When / Then
        mockMvc.perform(put("/api/usuarios/{id}", 99L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.estado").value(404))
                .andExpect(jsonPath("$.error").value("No encontrado"))
                .andExpect(jsonPath("$.mensaje").value("Usuario no encontrado con ID: 99"));

        verify(usuarioService).actualizarUsuario(eq(99L), any(UsuarioUpdateDTO.class));
    }

    @Test
    void eliminarUsuario_CuandoExiste_Retorna200() throws Exception {
        // When / Then
        mockMvc.perform(delete("/api/usuarios/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mensaje").value("Usuario eliminado correctamente"));

        verify(usuarioService).eliminarUsuario(1L);
    }

    @Test
    void eliminarUsuario_CuandoNoExiste_Retorna404() throws Exception {
        // Given
        doThrow(new UsuarioNoEncontradoException("Usuario no encontrado con ID: 99"))
                .when(usuarioService).eliminarUsuario(99L);

        // When / Then
        mockMvc.perform(delete("/api/usuarios/{id}", 99L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.estado").value(404))
                .andExpect(jsonPath("$.error").value("No encontrado"))
                .andExpect(jsonPath("$.mensaje").value("Usuario no encontrado con ID: 99"));

        verify(usuarioService).eliminarUsuario(99L);
    }

    @Test
    void buscarPorRol_CuandoExistenUsuarios_Retorna200() throws Exception {
        // Given
        List<UsuarioResponseDTO> usuarios = List.of(
                new UsuarioResponseDTO(1L, "pablo", "pablo@gmail.com", "CLIENTE")
        );

        when(usuarioService.buscarPorRol("CLIENTE")).thenReturn(usuarios);

        // When / Then
        mockMvc.perform(get("/api/usuarios/rol/{rol}", "CLIENTE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].rol").value("CLIENTE"));

        verify(usuarioService).buscarPorRol("CLIENTE");
    }

    @Test
    void buscarPorNombre_CuandoExistenCoincidencias_Retorna200() throws Exception {
        // Given
        List<UsuarioResponseDTO> usuarios = List.of(
                new UsuarioResponseDTO(1L, "pablo", "pablo@gmail.com", "CLIENTE")
        );

        when(usuarioService.buscarPorNombre("pab")).thenReturn(usuarios);

        // When / Then
        mockMvc.perform(get("/api/usuarios/buscar/{usuario}", "pab"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].usuario").value("pablo"));

        verify(usuarioService).buscarPorNombre("pab");
    }
}
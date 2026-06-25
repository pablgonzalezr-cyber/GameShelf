package GameShelf.ms_videojuego.controller;

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

import GameShelf.ms_videojuego.dto.VideoJuegoRequestDTO;
import GameShelf.ms_videojuego.dto.VideoJuegoResponseDTO;
import GameShelf.ms_videojuego.exception.ComunicacionCategoriaException;
import GameShelf.ms_videojuego.exception.DatoDuplicadoException;
import GameShelf.ms_videojuego.exception.GlobalExceptionHandler;
import GameShelf.ms_videojuego.exception.VideoJuegoNoEncontradoException;
import GameShelf.ms_videojuego.service.VideoJuegoService;

@ExtendWith(MockitoExtension.class)
class VideoJuegoControllerTest {

    @Mock
    private VideoJuegoService videoJuegoService;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        VideoJuegoController videoJuegoController = new VideoJuegoController(videoJuegoService);

        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();

        mockMvc = MockMvcBuilders
                .standaloneSetup(videoJuegoController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .setValidator(validator)
                .build();

        objectMapper = new ObjectMapper();
    }

    @Test
    void crearVideoJuego_CuandoDatosValidos_Retorna201() throws Exception {
        // Given
        VideoJuegoRequestDTO requestDTO = crearRequestValido();

        VideoJuegoResponseDTO responseDTO = new VideoJuegoResponseDTO(
                1L,
                "Zelda",
                "Videojuego de aventura",
                49990.0,
                1L,
                "AVENTURA",
                "PC",
                "DISPONIBLE"
        );

        when(videoJuegoService.crearVideoJuego(any(VideoJuegoRequestDTO.class))).thenReturn(responseDTO);

        // When / Then
        mockMvc.perform(post("/api/videojuegos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.titulo").value("Zelda"))
                .andExpect(jsonPath("$.nombreCategoria").value("AVENTURA"))
                .andExpect(jsonPath("$.plataforma").value("PC"))
                .andExpect(jsonPath("$.estado").value("DISPONIBLE"));

        verify(videoJuegoService).crearVideoJuego(any(VideoJuegoRequestDTO.class));
    }

    @Test
    void crearVideoJuego_CuandoDatosInvalidos_Retorna400() throws Exception {
        // Given
        VideoJuegoRequestDTO requestDTO = new VideoJuegoRequestDTO();
        requestDTO.setTitulo("A");
        requestDTO.setDescripcion("1234");
        requestDTO.setPrecio(-1.0);
        requestDTO.setCategoriaId(null);
        requestDTO.setPlataforma("");

        // When / Then
        mockMvc.perform(post("/api/videojuegos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.estado").value(400))
                .andExpect(jsonPath("$.error").value("Validación fallida"))
                .andExpect(jsonPath("$.mensajes", hasKey("titulo")))
                .andExpect(jsonPath("$.mensajes", hasKey("descripcion")))
                .andExpect(jsonPath("$.mensajes", hasKey("precio")))
                .andExpect(jsonPath("$.mensajes", hasKey("categoriaId")))
                .andExpect(jsonPath("$.mensajes", hasKey("plataforma")));

        verify(videoJuegoService, never()).crearVideoJuego(any(VideoJuegoRequestDTO.class));
    }

    @Test
    void crearVideoJuego_CuandoVideojuegoDuplicado_Retorna400() throws Exception {
        // Given
        VideoJuegoRequestDTO requestDTO = crearRequestValido();

        when(videoJuegoService.crearVideoJuego(any(VideoJuegoRequestDTO.class)))
                .thenThrow(new DatoDuplicadoException("El videojuego ya existe para esa plataforma"));

        // When / Then
        mockMvc.perform(post("/api/videojuegos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.estado").value(400))
                .andExpect(jsonPath("$.error").value("Dato inválido"))
                .andExpect(jsonPath("$.mensaje").value("El videojuego ya existe para esa plataforma"));

        verify(videoJuegoService).crearVideoJuego(any(VideoJuegoRequestDTO.class));
    }

    @Test
    void crearVideoJuego_CuandoCategoriaNoDisponible_Retorna503() throws Exception {
        // Given
        VideoJuegoRequestDTO requestDTO = crearRequestValido();

        when(videoJuegoService.crearVideoJuego(any(VideoJuegoRequestDTO.class)))
                .thenThrow(new ComunicacionCategoriaException("No se pudo comunicar con ms-categoria"));

        // When / Then
        mockMvc.perform(post("/api/videojuegos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.estado").value(503))
                .andExpect(jsonPath("$.error").value("Servicio no disponible"))
                .andExpect(jsonPath("$.mensaje").value("No se pudo comunicar con ms-categoria"));

        verify(videoJuegoService).crearVideoJuego(any(VideoJuegoRequestDTO.class));
    }

    @Test
    void listarVideoJuegos_CuandoExistenVideojuegos_Retorna200() throws Exception {
        // Given
        List<VideoJuegoResponseDTO> videojuegos = List.of(
                new VideoJuegoResponseDTO(1L, "Zelda", "Videojuego de aventura", 49990.0, 1L, "AVENTURA", "PC", "DISPONIBLE"),
                new VideoJuegoResponseDTO(2L, "Mario", "Juego de plataformas", 39990.0, 1L, "AVENTURA", "SWITCH", "DISPONIBLE")
        );

        when(videoJuegoService.listarVideoJuegos()).thenReturn(videojuegos);

        // When / Then
        mockMvc.perform(get("/api/videojuegos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].titulo").value("Zelda"))
                .andExpect(jsonPath("$[1].titulo").value("Mario"));

        verify(videoJuegoService).listarVideoJuegos();
    }

    @Test
    void buscarPorId_CuandoVideojuegoExiste_Retorna200() throws Exception {
        // Given
        VideoJuegoResponseDTO responseDTO = new VideoJuegoResponseDTO(
                1L,
                "Zelda",
                "Videojuego de aventura",
                49990.0,
                1L,
                "AVENTURA",
                "PC",
                "DISPONIBLE"
        );

        when(videoJuegoService.buscarPorId(1L)).thenReturn(responseDTO);

        // When / Then
        mockMvc.perform(get("/api/videojuegos/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.titulo").value("Zelda"))
                .andExpect(jsonPath("$.estado").value("DISPONIBLE"));

        verify(videoJuegoService).buscarPorId(1L);
    }

    @Test
    void buscarPorId_CuandoVideojuegoNoExiste_Retorna404() throws Exception {
        // Given
        when(videoJuegoService.buscarPorId(99L))
                .thenThrow(new VideoJuegoNoEncontradoException("Videojuego no encontrado con ID: 99"));

        // When / Then
        mockMvc.perform(get("/api/videojuegos/{id}", 99L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.estado").value(404))
                .andExpect(jsonPath("$.error").value("No encontrado"))
                .andExpect(jsonPath("$.mensaje").value("Videojuego no encontrado con ID: 99"));

        verify(videoJuegoService).buscarPorId(99L);
    }

    @Test
    void actualizarVideoJuego_CuandoDatosValidos_Retorna200() throws Exception {
        // Given
        VideoJuegoRequestDTO requestDTO = crearRequestValido();

        VideoJuegoResponseDTO responseDTO = new VideoJuegoResponseDTO(
                1L,
                "Zelda",
                "Videojuego de aventura",
                49990.0,
                1L,
                "AVENTURA",
                "PC",
                "DISPONIBLE"
        );

        when(videoJuegoService.actualizarVideoJuego(eq(1L), any(VideoJuegoRequestDTO.class)))
                .thenReturn(responseDTO);

        // When / Then
        mockMvc.perform(put("/api/videojuegos/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.titulo").value("Zelda"))
                .andExpect(jsonPath("$.plataforma").value("PC"));

        verify(videoJuegoService).actualizarVideoJuego(eq(1L), any(VideoJuegoRequestDTO.class));
    }

    @Test
    void actualizarVideoJuego_CuandoEstadoInvalido_Retorna400() throws Exception {
        // Given
        VideoJuegoRequestDTO requestDTO = crearRequestValido();
        requestDTO.setEstado("BLOQUEADO");

        when(videoJuegoService.actualizarVideoJuego(eq(1L), any(VideoJuegoRequestDTO.class)))
                .thenThrow(new DatoDuplicadoException("El estado debe ser DISPONIBLE, NO_DISPONIBLE o INACTIVO"));

        // When / Then
        mockMvc.perform(put("/api/videojuegos/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.estado").value(400))
                .andExpect(jsonPath("$.error").value("Dato inválido"))
                .andExpect(jsonPath("$.mensaje").value("El estado debe ser DISPONIBLE, NO_DISPONIBLE o INACTIVO"));

        verify(videoJuegoService).actualizarVideoJuego(eq(1L), any(VideoJuegoRequestDTO.class));
    }

    @Test
    void eliminarVideoJuego_CuandoExiste_Retorna200() throws Exception {
        // When / Then
        mockMvc.perform(delete("/api/videojuegos/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mensaje").value("Videojuego desactivado correctamente"));

        verify(videoJuegoService).eliminarVideoJuego(1L);
    }

    @Test
    void eliminarVideoJuego_CuandoNoExiste_Retorna404() throws Exception {
        // Given
        doThrow(new VideoJuegoNoEncontradoException("Videojuego no encontrado con ID: 99"))
                .when(videoJuegoService).eliminarVideoJuego(99L);

        // When / Then
        mockMvc.perform(delete("/api/videojuegos/{id}", 99L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.estado").value(404))
                .andExpect(jsonPath("$.error").value("No encontrado"))
                .andExpect(jsonPath("$.mensaje").value("Videojuego no encontrado con ID: 99"));

        verify(videoJuegoService).eliminarVideoJuego(99L);
    }

    @Test
    void buscarPorCategoria_CuandoExistenVideojuegos_Retorna200() throws Exception {
        // Given
        List<VideoJuegoResponseDTO> videojuegos = List.of(
                new VideoJuegoResponseDTO(1L, "Zelda", "Videojuego de aventura", 49990.0, 1L, "AVENTURA", "PC", "DISPONIBLE")
        );

        when(videoJuegoService.buscarPorCategoria(1L)).thenReturn(videojuegos);

        // When / Then
        mockMvc.perform(get("/api/videojuegos/categoria/{categoriaId}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].categoriaId").value(1));

        verify(videoJuegoService).buscarPorCategoria(1L);
    }

    @Test
    void buscarPorTitulo_CuandoExistenCoincidencias_Retorna200() throws Exception {
        // Given
        List<VideoJuegoResponseDTO> videojuegos = List.of(
                new VideoJuegoResponseDTO(1L, "Zelda", "Videojuego de aventura", 49990.0, 1L, "AVENTURA", "PC", "DISPONIBLE")
        );

        when(videoJuegoService.buscarPorTitulo("zel")).thenReturn(videojuegos);

        // When / Then
        mockMvc.perform(get("/api/videojuegos/buscar/{titulo}", "zel"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].titulo").value("Zelda"));

        verify(videoJuegoService).buscarPorTitulo("zel");
    }

    @Test
    void buscarPorEstado_CuandoEstadoValido_Retorna200() throws Exception {
        // Given
        List<VideoJuegoResponseDTO> videojuegos = List.of(
                new VideoJuegoResponseDTO(1L, "Zelda", "Videojuego de aventura", 49990.0, 1L, "AVENTURA", "PC", "DISPONIBLE")
        );

        when(videoJuegoService.buscarPorEstado("DISPONIBLE")).thenReturn(videojuegos);

        // When / Then
        mockMvc.perform(get("/api/videojuegos/estado/{estado}", "DISPONIBLE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].estado").value("DISPONIBLE"));

        verify(videoJuegoService).buscarPorEstado("DISPONIBLE");
    }

    @Test
    void buscarPorPlataforma_CuandoPlataformaValida_Retorna200() throws Exception {
        // Given
        List<VideoJuegoResponseDTO> videojuegos = List.of(
                new VideoJuegoResponseDTO(1L, "Zelda", "Videojuego de aventura", 49990.0, 1L, "AVENTURA", "PC", "DISPONIBLE")
        );

        when(videoJuegoService.buscarPorPlataforma("PC")).thenReturn(videojuegos);

        // When / Then
        mockMvc.perform(get("/api/videojuegos/plataforma/{plataforma}", "PC"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].plataforma").value("PC"));

        verify(videoJuegoService).buscarPorPlataforma("PC");
    }

    private VideoJuegoRequestDTO crearRequestValido() {
        VideoJuegoRequestDTO requestDTO = new VideoJuegoRequestDTO();
        requestDTO.setTitulo("Zelda");
        requestDTO.setDescripcion("Videojuego de aventura");
        requestDTO.setPrecio(49990.0);
        requestDTO.setCategoriaId(1L);
        requestDTO.setPlataforma("PC");
        requestDTO.setEstado("DISPONIBLE");
        return requestDTO;
    }
}
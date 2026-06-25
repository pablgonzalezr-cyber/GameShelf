package GameShelf.ms_categoria.controller;

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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import com.fasterxml.jackson.databind.ObjectMapper;

import GameShelf.ms_categoria.dto.CategoriaRequestDTO;
import GameShelf.ms_categoria.dto.CategoriaResponseDTO;
import GameShelf.ms_categoria.exception.CategoriaNoEncontradaException;
import GameShelf.ms_categoria.exception.DatoDuplicadoException;
import GameShelf.ms_categoria.exception.DatoInvalidoException;
import GameShelf.ms_categoria.exception.GlobalExceptionHandler;
import GameShelf.ms_categoria.service.CategoriaService;

@ExtendWith(MockitoExtension.class)
class CategoriaControllerTest {

    @Mock
    private CategoriaService categoriaService;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        CategoriaController categoriaController = new CategoriaController(categoriaService);

        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();

        mockMvc = MockMvcBuilders
                .standaloneSetup(categoriaController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .setValidator(validator)
                .build();

        objectMapper = new ObjectMapper();
    }

    @Test
    void crearCategoria_CuandoDatosValidos_Retorna201() throws Exception {
        // Given
        CategoriaRequestDTO requestDTO = new CategoriaRequestDTO();
        requestDTO.setNombre("AVENTURA");
        requestDTO.setDescripcion("Videojuegos de aventura");
        requestDTO.setEstado("ACTIVO");

        CategoriaResponseDTO responseDTO = new CategoriaResponseDTO(
                1L,
                "AVENTURA",
                "Videojuegos de aventura",
                "ACTIVO"
        );

        when(categoriaService.crearCategoria(any(CategoriaRequestDTO.class))).thenReturn(responseDTO);

        // When / Then
        mockMvc.perform(post("/api/categorias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nombre").value("AVENTURA"))
                .andExpect(jsonPath("$.descripcion").value("Videojuegos de aventura"))
                .andExpect(jsonPath("$.estado").value("ACTIVO"));

        verify(categoriaService).crearCategoria(any(CategoriaRequestDTO.class));
    }

    @Test
    void crearCategoria_CuandoDatosInvalidos_Retorna400() throws Exception {
        // Given
        CategoriaRequestDTO requestDTO = new CategoriaRequestDTO();
        requestDTO.setNombre("AB");
        requestDTO.setDescripcion("1234");
        requestDTO.setEstado("ACTIVO");

        // When / Then
        mockMvc.perform(post("/api/categorias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.estado").value(400))
                .andExpect(jsonPath("$.error").value("Error de validación"))
                .andExpect(jsonPath("$.mensajes", hasKey("nombre")))
                .andExpect(jsonPath("$.mensajes", hasKey("descripcion")));

        verify(categoriaService, never()).crearCategoria(any(CategoriaRequestDTO.class));
    }

    @Test
    void crearCategoria_CuandoNombreDuplicado_Retorna400() throws Exception {
        // Given
        CategoriaRequestDTO requestDTO = new CategoriaRequestDTO();
        requestDTO.setNombre("AVENTURA");
        requestDTO.setDescripcion("Videojuegos de aventura");
        requestDTO.setEstado("ACTIVO");

        when(categoriaService.crearCategoria(any(CategoriaRequestDTO.class)))
                .thenThrow(new DatoDuplicadoException("La categoría ya existe"));

        // When / Then
        mockMvc.perform(post("/api/categorias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.estado").value(400))
                .andExpect(jsonPath("$.error").value("Dato duplicado"))
                .andExpect(jsonPath("$.mensaje").value("La categoría ya existe"));

        verify(categoriaService).crearCategoria(any(CategoriaRequestDTO.class));
    }

    @Test
    void listarCategorias_CuandoExistenCategorias_Retorna200() throws Exception {
        // Given
        List<CategoriaResponseDTO> categorias = List.of(
                new CategoriaResponseDTO(1L, "AVENTURA", "Videojuegos de aventura", "ACTIVO"),
                new CategoriaResponseDTO(2L, "ACCION", "Videojuegos de acción", "ACTIVO")
        );

        when(categoriaService.listarCategorias()).thenReturn(categorias);

        // When / Then
        mockMvc.perform(get("/api/categorias"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].nombre").value("AVENTURA"))
                .andExpect(jsonPath("$[1].nombre").value("ACCION"));

        verify(categoriaService).listarCategorias();
    }

    @Test
    void buscarCategoria_CuandoExiste_Retorna200() throws Exception {
        // Given
        CategoriaResponseDTO responseDTO = new CategoriaResponseDTO(
                1L,
                "AVENTURA",
                "Videojuegos de aventura",
                "ACTIVO"
        );

        when(categoriaService.buscarPorId(1L)).thenReturn(responseDTO);

        // When / Then
        mockMvc.perform(get("/api/categorias/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nombre").value("AVENTURA"))
                .andExpect(jsonPath("$.estado").value("ACTIVO"));

        verify(categoriaService).buscarPorId(1L);
    }

    @Test
    void buscarCategoria_CuandoNoExiste_Retorna404() throws Exception {
        // Given
        when(categoriaService.buscarPorId(99L))
                .thenThrow(new CategoriaNoEncontradaException("Categoría no encontrada"));

        // When / Then
        mockMvc.perform(get("/api/categorias/{id}", 99L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.estado").value(404))
                .andExpect(jsonPath("$.error").value("Categoría no encontrada"))
                .andExpect(jsonPath("$.mensaje").value("Categoría no encontrada"));

        verify(categoriaService).buscarPorId(99L);
    }

    @Test
    void buscarPorNombreExacto_CuandoExiste_Retorna200() throws Exception {
        // Given
        CategoriaResponseDTO responseDTO = new CategoriaResponseDTO(
                1L,
                "AVENTURA",
                "Videojuegos de aventura",
                "ACTIVO"
        );

        when(categoriaService.buscarPorNombreExacto("AVENTURA")).thenReturn(responseDTO);

        // When / Then
        mockMvc.perform(get("/api/categorias/nombre/{nombre}", "AVENTURA"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nombre").value("AVENTURA"));

        verify(categoriaService).buscarPorNombreExacto("AVENTURA");
    }

    @Test
    void actualizarCategoria_CuandoDatosValidos_Retorna200() throws Exception {
        // Given
        CategoriaRequestDTO requestDTO = new CategoriaRequestDTO();
        requestDTO.setNombre("RPG");
        requestDTO.setDescripcion("Videojuegos de rol");
        requestDTO.setEstado("ACTIVO");

        CategoriaResponseDTO responseDTO = new CategoriaResponseDTO(
                1L,
                "RPG",
                "Videojuegos de rol",
                "ACTIVO"
        );

        when(categoriaService.actualizarCategoria(eq(1L), any(CategoriaRequestDTO.class)))
                .thenReturn(responseDTO);

        // When / Then
        mockMvc.perform(put("/api/categorias/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nombre").value("RPG"))
                .andExpect(jsonPath("$.descripcion").value("Videojuegos de rol"))
                .andExpect(jsonPath("$.estado").value("ACTIVO"));

        verify(categoriaService).actualizarCategoria(eq(1L), any(CategoriaRequestDTO.class));
    }

    @Test
    void actualizarCategoria_CuandoEstadoInvalido_Retorna400() throws Exception {
        // Given
        CategoriaRequestDTO requestDTO = new CategoriaRequestDTO();
        requestDTO.setNombre("RPG");
        requestDTO.setDescripcion("Videojuegos de rol");
        requestDTO.setEstado("BLOQUEADO");

        when(categoriaService.actualizarCategoria(eq(1L), any(CategoriaRequestDTO.class)))
                .thenThrow(new DatoInvalidoException("El estado debe ser ACTIVO o INACTIVO"));

        // When / Then
        mockMvc.perform(put("/api/categorias/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.estado").value(400))
                .andExpect(jsonPath("$.error").value("Dato inválido"))
                .andExpect(jsonPath("$.mensaje").value("El estado debe ser ACTIVO o INACTIVO"));

        verify(categoriaService).actualizarCategoria(eq(1L), any(CategoriaRequestDTO.class));
    }

    @Test
    void eliminarCategoria_CuandoExiste_Retorna200() throws Exception {
        // When / Then
        mockMvc.perform(delete("/api/categorias/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mensaje").value("Categoría desactivada correctamente"));

        verify(categoriaService).eliminarCategoria(1L);
    }

    @Test
    void buscarPorEstado_CuandoEstadoValido_Retorna200() throws Exception {
        // Given
        List<CategoriaResponseDTO> categorias = List.of(
                new CategoriaResponseDTO(1L, "AVENTURA", "Videojuegos de aventura", "ACTIVO")
        );

        when(categoriaService.buscarPorEstado("ACTIVO")).thenReturn(categorias);

        // When / Then
        mockMvc.perform(get("/api/categorias/estado/{estado}", "ACTIVO"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].estado").value("ACTIVO"));

        verify(categoriaService).buscarPorEstado("ACTIVO");
    }

    @Test
    void buscarPorNombre_CuandoExistenCoincidencias_Retorna200() throws Exception {
        // Given
        List<CategoriaResponseDTO> categorias = List.of(
                new CategoriaResponseDTO(1L, "AVENTURA", "Videojuegos de aventura", "ACTIVO")
        );

        when(categoriaService.buscarPorNombre("aven")).thenReturn(categorias);

        // When / Then
        mockMvc.perform(get("/api/categorias/buscar/{nombre}", "aven"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].nombre").value("AVENTURA"));

        verify(categoriaService).buscarPorNombre("aven");
    }
}
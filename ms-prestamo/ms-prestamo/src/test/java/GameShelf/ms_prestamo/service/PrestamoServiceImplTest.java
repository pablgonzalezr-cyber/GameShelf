package GameShelf.ms_prestamo.service;

import GameShelf.ms_prestamo.client.StockClient;
import GameShelf.ms_prestamo.client.UsuarioClient;
import GameShelf.ms_prestamo.client.VideoJuegoClient;
import GameShelf.ms_prestamo.dto.PrestamoRequestDTO;
import GameShelf.ms_prestamo.dto.PrestamoResponseDTO;
import GameShelf.ms_prestamo.dto.RenovacionPrestamoRequestDTO;
import GameShelf.ms_prestamo.dto.RenovacionPrestamoResponseDTO;
import GameShelf.ms_prestamo.dto.StockResponseDTO;
import GameShelf.ms_prestamo.dto.UsuarioResponseDTO;
import GameShelf.ms_prestamo.dto.VideoJuegoResponseDTO;
import GameShelf.ms_prestamo.exception.ComunicacionMicroservicioException;
import GameShelf.ms_prestamo.exception.DatoInvalidoException;
import GameShelf.ms_prestamo.exception.PrestamoNoEncontradoException;
import GameShelf.ms_prestamo.model.PrestamoModel;
import GameShelf.ms_prestamo.model.RenovacionPrestamoModel;
import GameShelf.ms_prestamo.repository.PrestamoRepository;
import GameShelf.ms_prestamo.repository.RenovacionPrestamoRepository;
import feign.FeignException;
import feign.Request;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PrestamoServiceImplTest {

    @Mock
    private PrestamoRepository prestamoRepository;

    @Mock
    private RenovacionPrestamoRepository renovacionPrestamoRepository;

    @Mock
    private UsuarioClient usuarioClient;

    @Mock
    private VideoJuegoClient videoJuegoClient;

    @Mock
    private StockClient stockClient;

    @InjectMocks
    private PrestamoServiceImpl prestamoService;

    private PrestamoRequestDTO requestDTO;
    private PrestamoModel prestamoModel;
    private UsuarioResponseDTO usuarioActivo;
    private VideoJuegoResponseDTO videojuegoDisponible;
    private StockResponseDTO stockActivo;

    @BeforeEach
    void setUp() {
        requestDTO = new PrestamoRequestDTO();
        requestDTO.setUsuarioId(1L);
        requestDTO.setVideojuegoId(2L);
        requestDTO.setFechaDevolucion(LocalDate.now().plusDays(7));

        prestamoModel = new PrestamoModel();
        prestamoModel.setId(1L);
        prestamoModel.setUsuarioId(1L);
        prestamoModel.setVideojuegoId(2L);
        prestamoModel.setFechaPrestamo(LocalDate.now());
        prestamoModel.setFechaDevolucion(LocalDate.now().plusDays(7));
        prestamoModel.setEstado("PRESTADO");

        usuarioActivo = new UsuarioResponseDTO(
                1L,
                "pablo",
                "pablo@gmail.com",
                "CLIENTE",
                "ACTIVO"
        );

        videojuegoDisponible = new VideoJuegoResponseDTO(
                2L,
                "Zelda",
                "Videojuego de aventura",
                49990.0,
                1L,
                "AVENTURA",
                "PC",
                "DISPONIBLE"
        );

        stockActivo = new StockResponseDTO(
                1L,
                2L,
                10,
                5,
                "ACTIVO"
        );
    }

    @Test
    void crearPrestamo_CuandoDatosValidos_GuardaPrestamoYReduceStock() {
        // Given
        when(usuarioClient.buscarUsuarioPorId(1L)).thenReturn(usuarioActivo);
        when(videoJuegoClient.buscarVideojuegoPorId(2L)).thenReturn(videojuegoDisponible);
        when(stockClient.buscarPorVideojuego(2L)).thenReturn(stockActivo);
        when(prestamoRepository.existsByUsuarioIdAndVideojuegoIdAndEstado(1L, 2L, "PRESTADO")).thenReturn(false);
        when(prestamoRepository.save(any(PrestamoModel.class))).thenAnswer(invocation -> {
            PrestamoModel prestamoGuardado = invocation.getArgument(0);
            prestamoGuardado.setId(1L);
            return prestamoGuardado;
        });

        // When
        PrestamoResponseDTO respuesta = prestamoService.crearPrestamo(requestDTO);

        // Then
        assertNotNull(respuesta);
        assertEquals(1L, respuesta.getId());
        assertEquals(1L, respuesta.getUsuarioId());
        assertEquals(2L, respuesta.getVideojuegoId());
        assertEquals("PRESTADO", respuesta.getEstado());
        assertEquals(requestDTO.getFechaDevolucion(), respuesta.getFechaDevolucion());

        verify(stockClient).reducirStock(2L);

        ArgumentCaptor<PrestamoModel> captor = ArgumentCaptor.forClass(PrestamoModel.class);
        verify(prestamoRepository).save(captor.capture());

        PrestamoModel prestamoEnviadoAGuardar = captor.getValue();
        assertEquals(1L, prestamoEnviadoAGuardar.getUsuarioId());
        assertEquals(2L, prestamoEnviadoAGuardar.getVideojuegoId());
        assertEquals("PRESTADO", prestamoEnviadoAGuardar.getEstado());
        assertEquals(LocalDate.now(), prestamoEnviadoAGuardar.getFechaPrestamo());
    }

    @Test
    void crearPrestamo_CuandoUsuarioNoExiste_LanzaDatoInvalidoException() {
        // Given
        when(usuarioClient.buscarUsuarioPorId(1L)).thenThrow(crearFeignNotFound());

        // When / Then
        DatoInvalidoException exception = assertThrows(
                DatoInvalidoException.class,
                () -> prestamoService.crearPrestamo(requestDTO)
        );

        assertEquals("El usuario ingresado no existe", exception.getMessage());
        verify(prestamoRepository, never()).save(any(PrestamoModel.class));
        verify(stockClient, never()).reducirStock(any());
    }

    @Test
    void crearPrestamo_CuandoUsuarioEstaInactivo_LanzaDatoInvalidoException() {
        // Given
        usuarioActivo.setEstado("INACTIVO");
        when(usuarioClient.buscarUsuarioPorId(1L)).thenReturn(usuarioActivo);

        // When / Then
        DatoInvalidoException exception = assertThrows(
                DatoInvalidoException.class,
                () -> prestamoService.crearPrestamo(requestDTO)
        );

        assertEquals("El usuario no está activo", exception.getMessage());
        verify(prestamoRepository, never()).save(any(PrestamoModel.class));
    }

    @Test
    void crearPrestamo_CuandoUsuarioClientFalla_LanzaComunicacionMicroservicioException() {
        // Given
        when(usuarioClient.buscarUsuarioPorId(1L)).thenThrow(crearFeignServiceUnavailable());

        // When / Then
        ComunicacionMicroservicioException exception = assertThrows(
                ComunicacionMicroservicioException.class,
                () -> prestamoService.crearPrestamo(requestDTO)
        );

        assertEquals("No se pudo comunicar con ms-usuario", exception.getMessage());
        verify(prestamoRepository, never()).save(any(PrestamoModel.class));
    }

    @Test
    void crearPrestamo_CuandoVideojuegoNoExiste_LanzaDatoInvalidoException() {
        // Given
        when(usuarioClient.buscarUsuarioPorId(1L)).thenReturn(usuarioActivo);
        when(videoJuegoClient.buscarVideojuegoPorId(2L)).thenThrow(crearFeignNotFound());

        // When / Then
        DatoInvalidoException exception = assertThrows(
                DatoInvalidoException.class,
                () -> prestamoService.crearPrestamo(requestDTO)
        );

        assertEquals("El videojuego ingresado no existe", exception.getMessage());
        verify(prestamoRepository, never()).save(any(PrestamoModel.class));
    }

    @Test
    void crearPrestamo_CuandoVideojuegoNoDisponible_LanzaDatoInvalidoException() {
        // Given
        videojuegoDisponible.setEstado("INACTIVO");
        when(usuarioClient.buscarUsuarioPorId(1L)).thenReturn(usuarioActivo);
        when(videoJuegoClient.buscarVideojuegoPorId(2L)).thenReturn(videojuegoDisponible);

        // When / Then
        DatoInvalidoException exception = assertThrows(
                DatoInvalidoException.class,
                () -> prestamoService.crearPrestamo(requestDTO)
        );

        assertEquals("El videojuego no está disponible", exception.getMessage());
        verify(prestamoRepository, never()).save(any(PrestamoModel.class));
    }

    @Test
    void crearPrestamo_CuandoStockNoExiste_LanzaDatoInvalidoException() {
        // Given
        when(usuarioClient.buscarUsuarioPorId(1L)).thenReturn(usuarioActivo);
        when(videoJuegoClient.buscarVideojuegoPorId(2L)).thenReturn(videojuegoDisponible);
        when(stockClient.buscarPorVideojuego(2L)).thenThrow(crearFeignNotFound());

        // When / Then
        DatoInvalidoException exception = assertThrows(
                DatoInvalidoException.class,
                () -> prestamoService.crearPrestamo(requestDTO)
        );

        assertEquals("No existe stock para este videojuego", exception.getMessage());
        verify(prestamoRepository, never()).save(any(PrestamoModel.class));
    }

    @Test
    void crearPrestamo_CuandoStockSinDisponibilidad_LanzaDatoInvalidoException() {
        // Given
        stockActivo.setCantidadDisponible(0);
        when(usuarioClient.buscarUsuarioPorId(1L)).thenReturn(usuarioActivo);
        when(videoJuegoClient.buscarVideojuegoPorId(2L)).thenReturn(videojuegoDisponible);
        when(stockClient.buscarPorVideojuego(2L)).thenReturn(stockActivo);

        // When / Then
        DatoInvalidoException exception = assertThrows(
                DatoInvalidoException.class,
                () -> prestamoService.crearPrestamo(requestDTO)
        );

        assertEquals("No hay stock disponible para este videojuego", exception.getMessage());
        verify(prestamoRepository, never()).save(any(PrestamoModel.class));
    }

    @Test
    void crearPrestamo_CuandoYaExistePrestamoActivo_LanzaDatoInvalidoException() {
        // Given
        when(usuarioClient.buscarUsuarioPorId(1L)).thenReturn(usuarioActivo);
        when(videoJuegoClient.buscarVideojuegoPorId(2L)).thenReturn(videojuegoDisponible);
        when(stockClient.buscarPorVideojuego(2L)).thenReturn(stockActivo);
        when(prestamoRepository.existsByUsuarioIdAndVideojuegoIdAndEstado(1L, 2L, "PRESTADO")).thenReturn(true);

        // When / Then
        DatoInvalidoException exception = assertThrows(
                DatoInvalidoException.class,
                () -> prestamoService.crearPrestamo(requestDTO)
        );

        assertEquals("El usuario ya tiene un préstamo activo de este videojuego", exception.getMessage());
        verify(stockClient, never()).reducirStock(any());
        verify(prestamoRepository, never()).save(any(PrestamoModel.class));
    }

    @Test
    void listarPrestamos_CuandoExistenPrestamos_RetornaLista() {
        // Given
        PrestamoModel prestamoDos = new PrestamoModel();
        prestamoDos.setId(2L);
        prestamoDos.setUsuarioId(3L);
        prestamoDos.setVideojuegoId(4L);
        prestamoDos.setFechaPrestamo(LocalDate.now());
        prestamoDos.setFechaDevolucion(LocalDate.now().plusDays(10));
        prestamoDos.setEstado("PRESTADO");

        when(prestamoRepository.findAll()).thenReturn(List.of(prestamoModel, prestamoDos));

        // When
        List<PrestamoResponseDTO> respuesta = prestamoService.listarPrestamos();

        // Then
        assertEquals(2, respuesta.size());
        assertEquals(1L, respuesta.get(0).getUsuarioId());
        assertEquals(3L, respuesta.get(1).getUsuarioId());
        verify(prestamoRepository).findAll();
    }

    @Test
    void buscarPorId_CuandoPrestamoExiste_RetornaPrestamo() {
        // Given
        when(prestamoRepository.findById(1L)).thenReturn(Optional.of(prestamoModel));

        // When
        PrestamoResponseDTO respuesta = prestamoService.buscarPorId(1L);

        // Then
        assertEquals(1L, respuesta.getId());
        assertEquals("PRESTADO", respuesta.getEstado());
        verify(prestamoRepository).findById(1L);
    }

    @Test
    void buscarPorId_CuandoPrestamoNoExiste_LanzaPrestamoNoEncontradoException() {
        // Given
        when(prestamoRepository.findById(99L)).thenReturn(Optional.empty());

        // When / Then
        PrestamoNoEncontradoException exception = assertThrows(
                PrestamoNoEncontradoException.class,
                () -> prestamoService.buscarPorId(99L)
        );

        assertEquals("Préstamo no encontrado", exception.getMessage());
        verify(prestamoRepository).findById(99L);
    }

    @Test
    void buscarPorUsuario_CuandoExistenPrestamos_RetornaLista() {
        // Given
        when(prestamoRepository.findByUsuarioId(1L)).thenReturn(List.of(prestamoModel));

        // When
        List<PrestamoResponseDTO> respuesta = prestamoService.buscarPorUsuario(1L);

        // Then
        assertEquals(1, respuesta.size());
        assertEquals(1L, respuesta.get(0).getUsuarioId());
        verify(prestamoRepository).findByUsuarioId(1L);
    }

    @Test
    void buscarPorVideojuego_CuandoExistenPrestamos_RetornaLista() {
        // Given
        when(prestamoRepository.findByVideojuegoId(2L)).thenReturn(List.of(prestamoModel));

        // When
        List<PrestamoResponseDTO> respuesta = prestamoService.buscarPorVideojuego(2L);

        // Then
        assertEquals(1, respuesta.size());
        assertEquals(2L, respuesta.get(0).getVideojuegoId());
        verify(prestamoRepository).findByVideojuegoId(2L);
    }

    @Test
    void buscarPorEstado_CuandoEstadoValidoEnMinuscula_NormalizaYRetornaLista() {
        // Given
        when(prestamoRepository.findByEstado("PRESTADO")).thenReturn(List.of(prestamoModel));

        // When
        List<PrestamoResponseDTO> respuesta = prestamoService.buscarPorEstado(" prestado ");

        // Then
        assertEquals(1, respuesta.size());
        assertEquals("PRESTADO", respuesta.get(0).getEstado());
        verify(prestamoRepository).findByEstado("PRESTADO");
    }

    @Test
    void buscarPorEstado_CuandoEstadoInvalido_LanzaDatoInvalidoException() {
        // When / Then
        DatoInvalidoException exception = assertThrows(
                DatoInvalidoException.class,
                () -> prestamoService.buscarPorEstado("BLOQUEADO")
        );

        assertEquals("El estado debe ser PRESTADO, DEVUELTO o CANCELADO", exception.getMessage());
        verify(prestamoRepository, never()).findByEstado(any());
    }

    @Test
    void devolverPrestamo_CuandoPrestamoActivo_AumentaStockYMarcaDevuelto() {
        // Given
        when(prestamoRepository.findById(1L)).thenReturn(Optional.of(prestamoModel));
        when(prestamoRepository.save(any(PrestamoModel.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        PrestamoResponseDTO respuesta = prestamoService.devolverPrestamo(1L);

        // Then
        assertEquals("DEVUELTO", respuesta.getEstado());
        assertEquals(LocalDate.now(), respuesta.getFechaDevolucion());
        verify(stockClient).aumentarStock(2L);
        verify(prestamoRepository).save(prestamoModel);
    }

    @Test
    void devolverPrestamo_CuandoPrestamoNoEstaActivo_LanzaDatoInvalidoException() {
        // Given
        prestamoModel.setEstado("DEVUELTO");
        when(prestamoRepository.findById(1L)).thenReturn(Optional.of(prestamoModel));

        // When / Then
        DatoInvalidoException exception = assertThrows(
                DatoInvalidoException.class,
                () -> prestamoService.devolverPrestamo(1L)
        );

        assertEquals("El préstamo no está activo", exception.getMessage());
        verify(stockClient, never()).aumentarStock(any());
        verify(prestamoRepository, never()).save(any(PrestamoModel.class));
    }

    @Test
    void cancelarPrestamo_CuandoPrestamoEstaActivo_AumentaStockYMarcaCancelado() {
        // Given
        when(prestamoRepository.findById(1L)).thenReturn(Optional.of(prestamoModel));
        when(prestamoRepository.save(any(PrestamoModel.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        prestamoService.cancelarPrestamo(1L);

        // Then
        assertEquals("CANCELADO", prestamoModel.getEstado());
        assertEquals(LocalDate.now(), prestamoModel.getFechaDevolucion());
        verify(stockClient).aumentarStock(2L);
        verify(prestamoRepository).save(prestamoModel);
    }

    @Test
    void cancelarPrestamo_CuandoPrestamoNoEstaActivo_NoAumentaStockYMarcaCancelado() {
        // Given
        prestamoModel.setEstado("DEVUELTO");
        when(prestamoRepository.findById(1L)).thenReturn(Optional.of(prestamoModel));
        when(prestamoRepository.save(any(PrestamoModel.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        prestamoService.cancelarPrestamo(1L);

        // Then
        assertEquals("CANCELADO", prestamoModel.getEstado());
        verify(stockClient, never()).aumentarStock(any());
        verify(prestamoRepository).save(prestamoModel);
    }

    @Test
    void renovarPrestamo_CuandoDatosValidos_GuardaRenovacionYActualizaPrestamo() {
        // Given
        RenovacionPrestamoRequestDTO renovacionDTO = new RenovacionPrestamoRequestDTO();
        renovacionDTO.setNuevaFechaDevolucion(LocalDate.now().plusDays(14));
        renovacionDTO.setMotivo("El usuario solicita más tiempo");

        when(prestamoRepository.findById(1L)).thenReturn(Optional.of(prestamoModel));
        when(renovacionPrestamoRepository.save(any(RenovacionPrestamoModel.class))).thenAnswer(invocation -> {
            RenovacionPrestamoModel renovacionGuardada = invocation.getArgument(0);
            renovacionGuardada.setId(1L);
            return renovacionGuardada;
        });
        when(prestamoRepository.save(any(PrestamoModel.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        RenovacionPrestamoResponseDTO respuesta = prestamoService.renovarPrestamo(1L, renovacionDTO);

        // Then
        assertEquals(1L, respuesta.getId());
        assertEquals(1L, respuesta.getPrestamoId());
        assertEquals(LocalDate.now().plusDays(7), respuesta.getFechaAnteriorDevolucion());
        assertEquals(LocalDate.now().plusDays(14), respuesta.getNuevaFechaDevolucion());
        assertEquals("El usuario solicita más tiempo", respuesta.getMotivo());
        assertEquals(LocalDate.now().plusDays(14), prestamoModel.getFechaDevolucion());

        verify(renovacionPrestamoRepository).save(any(RenovacionPrestamoModel.class));
        verify(prestamoRepository).save(prestamoModel);
    }

    @Test
    void renovarPrestamo_CuandoPrestamoNoEstaPrestado_LanzaDatoInvalidoException() {
        // Given
        prestamoModel.setEstado("DEVUELTO");
        RenovacionPrestamoRequestDTO renovacionDTO = new RenovacionPrestamoRequestDTO();
        renovacionDTO.setNuevaFechaDevolucion(LocalDate.now().plusDays(14));
        renovacionDTO.setMotivo("El usuario solicita más tiempo");

        when(prestamoRepository.findById(1L)).thenReturn(Optional.of(prestamoModel));

        // When / Then
        DatoInvalidoException exception = assertThrows(
                DatoInvalidoException.class,
                () -> prestamoService.renovarPrestamo(1L, renovacionDTO)
        );

        assertEquals("Solo se pueden renovar préstamos en estado PRESTADO", exception.getMessage());
        verify(renovacionPrestamoRepository, never()).save(any());
    }

    @Test
    void renovarPrestamo_CuandoNuevaFechaNoEsPosterior_LanzaDatoInvalidoException() {
        // Given
        RenovacionPrestamoRequestDTO renovacionDTO = new RenovacionPrestamoRequestDTO();
        renovacionDTO.setNuevaFechaDevolucion(LocalDate.now().plusDays(7));
        renovacionDTO.setMotivo("El usuario solicita más tiempo");

        when(prestamoRepository.findById(1L)).thenReturn(Optional.of(prestamoModel));

        // When / Then
        DatoInvalidoException exception = assertThrows(
                DatoInvalidoException.class,
                () -> prestamoService.renovarPrestamo(1L, renovacionDTO)
        );

        assertEquals("La nueva fecha de devolución debe ser posterior a la fecha actual de devolución", exception.getMessage());
        verify(renovacionPrestamoRepository, never()).save(any());
    }

    @Test
    void listarRenovacionesPorPrestamo_CuandoExistenRenovaciones_RetornaLista() {
        // Given
        RenovacionPrestamoModel renovacion = new RenovacionPrestamoModel();
        renovacion.setId(1L);
        renovacion.setPrestamo(prestamoModel);
        renovacion.setFechaAnteriorDevolucion(LocalDate.now().plusDays(7));
        renovacion.setNuevaFechaDevolucion(LocalDate.now().plusDays(14));
        renovacion.setMotivo("El usuario solicita más tiempo");
        renovacion.setFechaRenovacion(LocalDate.now());

        when(prestamoRepository.findById(1L)).thenReturn(Optional.of(prestamoModel));
        when(renovacionPrestamoRepository.findByPrestamoIdOrderByFechaRenovacionAsc(1L))
                .thenReturn(List.of(renovacion));

        // When
        List<RenovacionPrestamoResponseDTO> respuesta = prestamoService.listarRenovacionesPorPrestamo(1L);

        // Then
        assertEquals(1, respuesta.size());
        assertEquals(1L, respuesta.get(0).getPrestamoId());
        assertEquals("El usuario solicita más tiempo", respuesta.get(0).getMotivo());
        verify(renovacionPrestamoRepository).findByPrestamoIdOrderByFechaRenovacionAsc(1L);
    }

    private FeignException.NotFound crearFeignNotFound() {
        Request request = crearRequestFeign();
        Map<String, Collection<String>> headers = Collections.emptyMap();

        return new FeignException.NotFound(
                "Recurso no encontrado",
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
                "/api/recurso/1",
                Map.of(),
                null,
                StandardCharsets.UTF_8,
                null
        );
    }
}
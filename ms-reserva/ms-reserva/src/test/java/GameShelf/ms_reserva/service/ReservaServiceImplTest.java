package GameShelf.ms_reserva.service;

import GameShelf.ms_reserva.client.StockClient;
import GameShelf.ms_reserva.client.UsuarioClient;
import GameShelf.ms_reserva.client.VideojuegoClient;
import GameShelf.ms_reserva.dto.HistorialReservaResponseDTO;
import GameShelf.ms_reserva.dto.ReservaRequestDTO;
import GameShelf.ms_reserva.dto.ReservaResponseDTO;
import GameShelf.ms_reserva.dto.StockResponseDTO;
import GameShelf.ms_reserva.dto.UsuarioResponseDTO;
import GameShelf.ms_reserva.dto.VideojuegoResponseDTO;
import GameShelf.ms_reserva.exception.DatoDuplicadoException;
import GameShelf.ms_reserva.exception.DatoInvalidoException;
import GameShelf.ms_reserva.exception.RecursoNoEncontradoException;
import GameShelf.ms_reserva.model.HistorialReservaModel;
import GameShelf.ms_reserva.model.ReservaModel;
import GameShelf.ms_reserva.repository.HistorialReservaRepository;
import GameShelf.ms_reserva.repository.ReservaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReservaServiceImplTest {

    @Mock
    private ReservaRepository reservaRepository;

    @Mock
    private HistorialReservaRepository historialReservaRepository;

    @Mock
    private UsuarioClient usuarioClient;

    @Mock
    private VideojuegoClient videojuegoClient;

    @Mock
    private StockClient stockClient;

    @InjectMocks
    private ReservaServiceImpl reservaService;

    private ReservaRequestDTO requestDTO;
    private ReservaModel reservaModel;
    private UsuarioResponseDTO usuarioActivo;
    private VideojuegoResponseDTO videojuegoDisponible;
    private StockResponseDTO stockActivo;

    @BeforeEach
    void setUp() {
        requestDTO = new ReservaRequestDTO();
        requestDTO.setUsuarioId(1L);
        requestDTO.setVideojuegoId(2L);
        requestDTO.setEstado(null);

        reservaModel = new ReservaModel();
        reservaModel.setId(1L);
        reservaModel.setUsuarioId(1L);
        reservaModel.setVideojuegoId(2L);
        reservaModel.setFechaReserva(LocalDate.now());
        reservaModel.setEstado("PENDIENTE");

        usuarioActivo = new UsuarioResponseDTO(
                1L,
                "pablo",
                "pablo@gmail.com",
                "CLIENTE",
                "ACTIVO"
        );

        videojuegoDisponible = new VideojuegoResponseDTO(
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
    void crearReserva_CuandoDatosValidos_GuardaReservaReduceStockYRegistraHistorial() {
        // Given
        when(usuarioClient.obtenerUsuarioPorId(1L)).thenReturn(usuarioActivo);
        when(videojuegoClient.obtenerVideojuegoPorId(2L)).thenReturn(videojuegoDisponible);
        when(stockClient.obtenerStockPorVideojuego(2L)).thenReturn(stockActivo);
        when(reservaRepository.existsByUsuarioIdAndVideojuegoIdAndEstadoIn(
                1L,
                2L,
                List.of("PENDIENTE", "CONFIRMADA")
        )).thenReturn(false);
        when(reservaRepository.save(any(ReservaModel.class))).thenAnswer(invocation -> {
            ReservaModel reservaGuardada = invocation.getArgument(0);
            reservaGuardada.setId(1L);
            return reservaGuardada;
        });

        // When
        ReservaResponseDTO respuesta = reservaService.crearReserva(requestDTO);

        // Then
        assertNotNull(respuesta);
        assertEquals(1L, respuesta.getId());
        assertEquals(1L, respuesta.getUsuarioId());
        assertEquals(2L, respuesta.getVideojuegoId());
        assertEquals(LocalDate.now(), respuesta.getFechaReserva());
        assertEquals("PENDIENTE", respuesta.getEstado());

        verify(stockClient).reducirStock(2L);
        verify(historialReservaRepository).save(any(HistorialReservaModel.class));

        ArgumentCaptor<ReservaModel> captor = ArgumentCaptor.forClass(ReservaModel.class);
        verify(reservaRepository).save(captor.capture());

        ReservaModel reservaEnviadaAGuardar = captor.getValue();
        assertEquals(1L, reservaEnviadaAGuardar.getUsuarioId());
        assertEquals(2L, reservaEnviadaAGuardar.getVideojuegoId());
        assertEquals("PENDIENTE", reservaEnviadaAGuardar.getEstado());
        assertEquals(LocalDate.now(), reservaEnviadaAGuardar.getFechaReserva());
    }

    @Test
    void crearReserva_CuandoUsuarioNoExiste_LanzaDatoInvalidoException() {
        // Given
        when(usuarioClient.obtenerUsuarioPorId(1L)).thenReturn(null);

        // When / Then
        DatoInvalidoException exception = assertThrows(
                DatoInvalidoException.class,
                () -> reservaService.crearReserva(requestDTO)
        );

        assertEquals("El usuario no existe", exception.getMessage());
        verify(reservaRepository, never()).save(any(ReservaModel.class));
        verify(stockClient, never()).reducirStock(any());
    }

    @Test
    void crearReserva_CuandoUsuarioEstaInactivo_LanzaDatoInvalidoException() {
        // Given
        usuarioActivo.setEstado("INACTIVO");
        when(usuarioClient.obtenerUsuarioPorId(1L)).thenReturn(usuarioActivo);

        // When / Then
        DatoInvalidoException exception = assertThrows(
                DatoInvalidoException.class,
                () -> reservaService.crearReserva(requestDTO)
        );

        assertEquals("El usuario no está activo", exception.getMessage());
        verify(reservaRepository, never()).save(any(ReservaModel.class));
    }

    @Test
    void crearReserva_CuandoVideojuegoNoExiste_LanzaDatoInvalidoException() {
        // Given
        when(usuarioClient.obtenerUsuarioPorId(1L)).thenReturn(usuarioActivo);
        when(videojuegoClient.obtenerVideojuegoPorId(2L)).thenReturn(null);

        // When / Then
        DatoInvalidoException exception = assertThrows(
                DatoInvalidoException.class,
                () -> reservaService.crearReserva(requestDTO)
        );

        assertEquals("El videojuego no existe", exception.getMessage());
        verify(reservaRepository, never()).save(any(ReservaModel.class));
    }

    @Test
    void crearReserva_CuandoVideojuegoNoDisponible_LanzaDatoInvalidoException() {
        // Given
        videojuegoDisponible.setEstado("INACTIVO");
        when(usuarioClient.obtenerUsuarioPorId(1L)).thenReturn(usuarioActivo);
        when(videojuegoClient.obtenerVideojuegoPorId(2L)).thenReturn(videojuegoDisponible);

        // When / Then
        DatoInvalidoException exception = assertThrows(
                DatoInvalidoException.class,
                () -> reservaService.crearReserva(requestDTO)
        );

        assertEquals("El videojuego no está disponible para reservar", exception.getMessage());
        verify(reservaRepository, never()).save(any(ReservaModel.class));
    }

    @Test
    void crearReserva_CuandoStockNoExiste_LanzaDatoInvalidoException() {
        // Given
        when(usuarioClient.obtenerUsuarioPorId(1L)).thenReturn(usuarioActivo);
        when(videojuegoClient.obtenerVideojuegoPorId(2L)).thenReturn(videojuegoDisponible);
        when(stockClient.obtenerStockPorVideojuego(2L)).thenReturn(null);

        // When / Then
        DatoInvalidoException exception = assertThrows(
                DatoInvalidoException.class,
                () -> reservaService.crearReserva(requestDTO)
        );

        assertEquals("No existe stock para este videojuego", exception.getMessage());
        verify(reservaRepository, never()).save(any(ReservaModel.class));
    }

    @Test
    void crearReserva_CuandoStockInactivo_LanzaDatoInvalidoException() {
        // Given
        stockActivo.setEstado("INACTIVO");
        when(usuarioClient.obtenerUsuarioPorId(1L)).thenReturn(usuarioActivo);
        when(videojuegoClient.obtenerVideojuegoPorId(2L)).thenReturn(videojuegoDisponible);
        when(stockClient.obtenerStockPorVideojuego(2L)).thenReturn(stockActivo);

        // When / Then
        DatoInvalidoException exception = assertThrows(
                DatoInvalidoException.class,
                () -> reservaService.crearReserva(requestDTO)
        );

        assertEquals("El stock del videojuego no está activo", exception.getMessage());
        verify(reservaRepository, never()).save(any(ReservaModel.class));
    }

    @Test
    void crearReserva_CuandoNoHayStockDisponible_LanzaDatoInvalidoException() {
        // Given
        stockActivo.setCantidadDisponible(0);
        when(usuarioClient.obtenerUsuarioPorId(1L)).thenReturn(usuarioActivo);
        when(videojuegoClient.obtenerVideojuegoPorId(2L)).thenReturn(videojuegoDisponible);
        when(stockClient.obtenerStockPorVideojuego(2L)).thenReturn(stockActivo);

        // When / Then
        DatoInvalidoException exception = assertThrows(
                DatoInvalidoException.class,
                () -> reservaService.crearReserva(requestDTO)
        );

        assertEquals("No hay stock disponible para reservar este videojuego", exception.getMessage());
        verify(reservaRepository, never()).save(any(ReservaModel.class));
    }

    @Test
    void crearReserva_CuandoYaExisteReservaActiva_LanzaDatoDuplicadoException() {
        // Given
        when(usuarioClient.obtenerUsuarioPorId(1L)).thenReturn(usuarioActivo);
        when(videojuegoClient.obtenerVideojuegoPorId(2L)).thenReturn(videojuegoDisponible);
        when(stockClient.obtenerStockPorVideojuego(2L)).thenReturn(stockActivo);
        when(reservaRepository.existsByUsuarioIdAndVideojuegoIdAndEstadoIn(
                1L,
                2L,
                List.of("PENDIENTE", "CONFIRMADA")
        )).thenReturn(true);

        // When / Then
        DatoDuplicadoException exception = assertThrows(
                DatoDuplicadoException.class,
                () -> reservaService.crearReserva(requestDTO)
        );

        assertEquals("El usuario ya tiene una reserva activa para este videojuego", exception.getMessage());
        verify(stockClient, never()).reducirStock(any());
        verify(reservaRepository, never()).save(any(ReservaModel.class));
    }

    @Test
    void listarReservas_CuandoExistenReservas_RetornaLista() {
        // Given
        ReservaModel reservaDos = new ReservaModel();
        reservaDos.setId(2L);
        reservaDos.setUsuarioId(3L);
        reservaDos.setVideojuegoId(4L);
        reservaDos.setFechaReserva(LocalDate.now());
        reservaDos.setEstado("CONFIRMADA");

        when(reservaRepository.findAll()).thenReturn(List.of(reservaModel, reservaDos));

        // When
        List<ReservaResponseDTO> respuesta = reservaService.listarReservas();

        // Then
        assertEquals(2, respuesta.size());
        assertEquals(1L, respuesta.get(0).getUsuarioId());
        assertEquals(3L, respuesta.get(1).getUsuarioId());
        verify(reservaRepository).findAll();
    }

    @Test
    void buscarReservaPorId_CuandoExiste_RetornaReserva() {
        // Given
        when(reservaRepository.findById(1L)).thenReturn(Optional.of(reservaModel));

        // When
        ReservaResponseDTO respuesta = reservaService.buscarReservaPorId(1L);

        // Then
        assertEquals(1L, respuesta.getId());
        assertEquals("PENDIENTE", respuesta.getEstado());
        verify(reservaRepository).findById(1L);
    }

    @Test
    void buscarReservaPorId_CuandoNoExiste_LanzaRecursoNoEncontradoException() {
        // Given
        when(reservaRepository.findById(99L)).thenReturn(Optional.empty());

        // When / Then
        RecursoNoEncontradoException exception = assertThrows(
                RecursoNoEncontradoException.class,
                () -> reservaService.buscarReservaPorId(99L)
        );

        assertEquals("Reserva no encontrada", exception.getMessage());
        verify(reservaRepository).findById(99L);
    }

    @Test
    void buscarReservasPorUsuario_CuandoUsuarioValido_RetornaLista() {
        // Given
        when(usuarioClient.obtenerUsuarioPorId(1L)).thenReturn(usuarioActivo);
        when(reservaRepository.findByUsuarioId(1L)).thenReturn(List.of(reservaModel));

        // When
        List<ReservaResponseDTO> respuesta = reservaService.buscarReservasPorUsuario(1L);

        // Then
        assertEquals(1, respuesta.size());
        assertEquals(1L, respuesta.get(0).getUsuarioId());
        verify(reservaRepository).findByUsuarioId(1L);
    }

    @Test
    void buscarReservasPorVideojuego_CuandoVideojuegoValido_RetornaLista() {
        // Given
        when(videojuegoClient.obtenerVideojuegoPorId(2L)).thenReturn(videojuegoDisponible);
        when(reservaRepository.findByVideojuegoId(2L)).thenReturn(List.of(reservaModel));

        // When
        List<ReservaResponseDTO> respuesta = reservaService.buscarReservasPorVideojuego(2L);

        // Then
        assertEquals(1, respuesta.size());
        assertEquals(2L, respuesta.get(0).getVideojuegoId());
        verify(reservaRepository).findByVideojuegoId(2L);
    }

    @Test
    void buscarReservasPorEstado_CuandoEstadoValidoEnMinuscula_NormalizaYRetornaLista() {
        // Given
        when(reservaRepository.findByEstado("PENDIENTE")).thenReturn(List.of(reservaModel));

        // When
        List<ReservaResponseDTO> respuesta = reservaService.buscarReservasPorEstado(" pendiente ");

        // Then
        assertEquals(1, respuesta.size());
        assertEquals("PENDIENTE", respuesta.get(0).getEstado());
        verify(reservaRepository).findByEstado("PENDIENTE");
    }

    @Test
    void buscarReservasPorEstado_CuandoEstadoInvalido_LanzaDatoInvalidoException() {
        // When / Then
        DatoInvalidoException exception = assertThrows(
                DatoInvalidoException.class,
                () -> reservaService.buscarReservasPorEstado("BLOQUEADO")
        );

        assertEquals("Estado de reserva inválido. Use PENDIENTE, CONFIRMADA, CANCELADA o EXPIRADA", exception.getMessage());
        verify(reservaRepository, never()).findByEstado(any());
    }

    @Test
    void actualizarReserva_CuandoDatosValidos_ActualizaEstado() {
        // Given
        ReservaRequestDTO actualizarDTO = new ReservaRequestDTO();
        actualizarDTO.setUsuarioId(1L);
        actualizarDTO.setVideojuegoId(2L);
        actualizarDTO.setEstado("confirmada");

        when(reservaRepository.findById(1L)).thenReturn(Optional.of(reservaModel));
        when(usuarioClient.obtenerUsuarioPorId(1L)).thenReturn(usuarioActivo);
        when(videojuegoClient.obtenerVideojuegoPorId(2L)).thenReturn(videojuegoDisponible);
        when(reservaRepository.save(any(ReservaModel.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        ReservaResponseDTO respuesta = reservaService.actualizarReserva(1L, actualizarDTO);

        // Then
        assertEquals(1L, respuesta.getId());
        assertEquals("CONFIRMADA", respuesta.getEstado());
        verify(reservaRepository).save(reservaModel);
    }

    @Test
    void actualizarReserva_CuandoCambiaUsuario_LanzaDatoInvalidoException() {
        // Given
        ReservaRequestDTO actualizarDTO = new ReservaRequestDTO();
        actualizarDTO.setUsuarioId(99L);
        actualizarDTO.setVideojuegoId(2L);
        actualizarDTO.setEstado("PENDIENTE");

        when(reservaRepository.findById(1L)).thenReturn(Optional.of(reservaModel));

        // When / Then
        DatoInvalidoException exception = assertThrows(
                DatoInvalidoException.class,
                () -> reservaService.actualizarReserva(1L, actualizarDTO)
        );

        assertEquals("No se puede cambiar el usuario de una reserva existente", exception.getMessage());
        verify(reservaRepository, never()).save(any(ReservaModel.class));
    }

    @Test
    void confirmarReserva_CuandoPendiente_CambiaAConfirmadaYRegistraHistorial() {
        // Given
        when(reservaRepository.findById(1L)).thenReturn(Optional.of(reservaModel));
        when(reservaRepository.save(any(ReservaModel.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        ReservaResponseDTO respuesta = reservaService.confirmarReserva(1L);

        // Then
        assertEquals("CONFIRMADA", respuesta.getEstado());
        verify(reservaRepository).save(reservaModel);
        verify(historialReservaRepository).save(any(HistorialReservaModel.class));
    }

    @Test
    void confirmarReserva_CuandoNoEstaPendiente_LanzaDatoInvalidoException() {
        // Given
        reservaModel.setEstado("CONFIRMADA");
        when(reservaRepository.findById(1L)).thenReturn(Optional.of(reservaModel));

        // When / Then
        DatoInvalidoException exception = assertThrows(
                DatoInvalidoException.class,
                () -> reservaService.confirmarReserva(1L)
        );

        assertEquals("Solo se pueden confirmar reservas pendientes", exception.getMessage());
        verify(reservaRepository, never()).save(any(ReservaModel.class));
    }

    @Test
    void cancelarReserva_CuandoReservaValida_AumentaStockCambiaACanceladaYRegistraHistorial() {
        // Given
        when(reservaRepository.findById(1L)).thenReturn(Optional.of(reservaModel));
        when(reservaRepository.save(any(ReservaModel.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        ReservaResponseDTO respuesta = reservaService.cancelarReserva(1L);

        // Then
        assertEquals("CANCELADA", respuesta.getEstado());
        verify(stockClient).aumentarStock(2L);
        verify(reservaRepository).save(reservaModel);
        verify(historialReservaRepository).save(any(HistorialReservaModel.class));
    }

    @Test
    void cancelarReserva_CuandoYaEstaCancelada_LanzaDatoInvalidoException() {
        // Given
        reservaModel.setEstado("CANCELADA");
        when(reservaRepository.findById(1L)).thenReturn(Optional.of(reservaModel));

        // When / Then
        DatoInvalidoException exception = assertThrows(
                DatoInvalidoException.class,
                () -> reservaService.cancelarReserva(1L)
        );

        assertEquals("La reserva ya está cancelada", exception.getMessage());
        verify(stockClient, never()).aumentarStock(any());
    }

    @Test
    void eliminarReserva_CuandoReservaValida_AumentaStockYCancelaReserva() {
        // Given
        when(reservaRepository.findById(1L)).thenReturn(Optional.of(reservaModel));
        when(reservaRepository.save(any(ReservaModel.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        reservaService.eliminarReserva(1L);

        // Then
        assertEquals("CANCELADA", reservaModel.getEstado());
        verify(stockClient).aumentarStock(2L);
        verify(reservaRepository).save(reservaModel);
        verify(historialReservaRepository).save(any(HistorialReservaModel.class));
    }

    @Test
    void listarHistorialPorReserva_CuandoExisteHistorial_RetornaLista() {
        // Given
        HistorialReservaModel historial = new HistorialReservaModel();
        historial.setId(1L);
        historial.setReserva(reservaModel);
        historial.setEstadoAnterior("PENDIENTE");
        historial.setEstadoNuevo("CONFIRMADA");
        historial.setFechaCambio(LocalDate.now());
        historial.setMotivo("Reserva confirmada correctamente");

        when(reservaRepository.findById(1L)).thenReturn(Optional.of(reservaModel));
        when(historialReservaRepository.findByReservaId(1L)).thenReturn(List.of(historial));

        // When
        List<HistorialReservaResponseDTO> respuesta = reservaService.listarHistorialPorReserva(1L);

        // Then
        assertEquals(1, respuesta.size());
        assertEquals(1L, respuesta.get(0).getReservaId());
        assertEquals("PENDIENTE", respuesta.get(0).getEstadoAnterior());
        assertEquals("CONFIRMADA", respuesta.get(0).getEstadoNuevo());
        assertEquals("Reserva confirmada correctamente", respuesta.get(0).getMotivo());
        verify(historialReservaRepository).findByReservaId(1L);
    }
}
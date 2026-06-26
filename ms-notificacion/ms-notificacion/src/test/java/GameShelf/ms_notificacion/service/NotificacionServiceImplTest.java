package GameShelf.ms_notificacion.service;

import GameShelf.ms_notificacion.client.UsuarioClient;
import GameShelf.ms_notificacion.dto.NotificacionRequestDTO;
import GameShelf.ms_notificacion.dto.NotificacionResponseDTO;
import GameShelf.ms_notificacion.dto.UsuarioResponseDTO;
import GameShelf.ms_notificacion.exception.DatoInvalidoException;
import GameShelf.ms_notificacion.exception.RecursoNoEncontradoException;
import GameShelf.ms_notificacion.model.NotificacionModel;
import GameShelf.ms_notificacion.repository.NotificacionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NotificacionServiceImplTest {

    @Mock
    private NotificacionRepository notificacionRepository;

    @Mock
    private UsuarioClient usuarioClient;

    @InjectMocks
    private NotificacionServiceImpl notificacionService;

    private NotificacionRequestDTO requestDTO;
    private NotificacionModel notificacionModel;
    private UsuarioResponseDTO usuarioActivo;

    @BeforeEach
    void setUp() {
        requestDTO = new NotificacionRequestDTO();
        requestDTO.setUsuarioId(1L);
        requestDTO.setTitulo("Reserva creada");
        requestDTO.setMensaje("Tu reserva fue creada correctamente");
        requestDTO.setTipo(" reserva ");
        requestDTO.setEstado(null);
        requestDTO.setReferenciaId(10L);
        requestDTO.setReferenciaTipo("RESERVA");

        notificacionModel = new NotificacionModel();
        notificacionModel.setId(1L);
        notificacionModel.setUsuarioId(1L);
        notificacionModel.setTitulo("Reserva creada");
        notificacionModel.setMensaje("Tu reserva fue creada correctamente");
        notificacionModel.setTipo("RESERVA");
        notificacionModel.setEstado("PENDIENTE");
        notificacionModel.setFechaCreacion(LocalDateTime.now());
        notificacionModel.setFechaLectura(null);
        notificacionModel.setReferenciaId(10L);
        notificacionModel.setReferenciaTipo("RESERVA");

        usuarioActivo = new UsuarioResponseDTO(
                1L,
                "pablo",
                "pablo@gmail.com",
                "CLIENTE",
                "ACTIVO"
        );
    }

    @Test
    void crearNotificacion_CuandoDatosValidos_GuardaNotificacionConEstadoPendiente() {
        // Given
        when(usuarioClient.obtenerUsuarioPorId(1L)).thenReturn(usuarioActivo);
        when(notificacionRepository.save(any(NotificacionModel.class))).thenAnswer(invocation -> {
            NotificacionModel notificacionGuardada = invocation.getArgument(0);
            notificacionGuardada.setId(1L);
            notificacionGuardada.setFechaCreacion(LocalDateTime.now());
            return notificacionGuardada;
        });

        // When
        NotificacionResponseDTO respuesta = notificacionService.crearNotificacion(requestDTO);

        // Then
        assertNotNull(respuesta);
        assertEquals(1L, respuesta.getId());
        assertEquals(1L, respuesta.getUsuarioId());
        assertEquals("Reserva creada", respuesta.getTitulo());
        assertEquals("Tu reserva fue creada correctamente", respuesta.getMensaje());
        assertEquals("RESERVA", respuesta.getTipo());
        assertEquals("PENDIENTE", respuesta.getEstado());
        assertNull(respuesta.getFechaLectura());
        assertEquals(10L, respuesta.getReferenciaId());
        assertEquals("RESERVA", respuesta.getReferenciaTipo());

        ArgumentCaptor<NotificacionModel> captor = ArgumentCaptor.forClass(NotificacionModel.class);
        verify(notificacionRepository).save(captor.capture());

        NotificacionModel notificacionEnviadaAGuardar = captor.getValue();
        assertEquals(1L, notificacionEnviadaAGuardar.getUsuarioId());
        assertEquals("RESERVA", notificacionEnviadaAGuardar.getTipo());
        assertEquals("PENDIENTE", notificacionEnviadaAGuardar.getEstado());
        assertNull(notificacionEnviadaAGuardar.getFechaLectura());
    }

    @Test
    void crearNotificacion_CuandoEstadoEsLeida_GuardaFechaLectura() {
        // Given
        requestDTO.setEstado("leida");

        when(usuarioClient.obtenerUsuarioPorId(1L)).thenReturn(usuarioActivo);
        when(notificacionRepository.save(any(NotificacionModel.class))).thenAnswer(invocation -> {
            NotificacionModel notificacionGuardada = invocation.getArgument(0);
            notificacionGuardada.setId(1L);
            notificacionGuardada.setFechaCreacion(LocalDateTime.now());
            return notificacionGuardada;
        });

        // When
        NotificacionResponseDTO respuesta = notificacionService.crearNotificacion(requestDTO);

        // Then
        assertEquals("LEIDA", respuesta.getEstado());
        assertNotNull(respuesta.getFechaLectura());
        verify(notificacionRepository).save(any(NotificacionModel.class));
    }

    @Test
    void crearNotificacion_CuandoUsuarioEsNulo_LanzaDatoInvalidoException() {
        // Given
        requestDTO.setUsuarioId(null);

        // When / Then
        DatoInvalidoException exception = assertThrows(
                DatoInvalidoException.class,
                () -> notificacionService.crearNotificacion(requestDTO)
        );

        assertEquals("El usuario es obligatorio", exception.getMessage());
        verify(usuarioClient, never()).obtenerUsuarioPorId(any());
        verify(notificacionRepository, never()).save(any(NotificacionModel.class));
    }

    @Test
    void crearNotificacion_CuandoUsuarioNoExiste_LanzaDatoInvalidoException() {
        // Given
        when(usuarioClient.obtenerUsuarioPorId(1L)).thenReturn(null);

        // When / Then
        DatoInvalidoException exception = assertThrows(
                DatoInvalidoException.class,
                () -> notificacionService.crearNotificacion(requestDTO)
        );

        assertEquals("El usuario no existe", exception.getMessage());
        verify(notificacionRepository, never()).save(any(NotificacionModel.class));
    }

    @Test
    void crearNotificacion_CuandoUsuarioEstaInactivo_LanzaDatoInvalidoException() {
        // Given
        usuarioActivo.setEstado("INACTIVO");
        when(usuarioClient.obtenerUsuarioPorId(1L)).thenReturn(usuarioActivo);

        // When / Then
        DatoInvalidoException exception = assertThrows(
                DatoInvalidoException.class,
                () -> notificacionService.crearNotificacion(requestDTO)
        );

        assertEquals("El usuario no está activo", exception.getMessage());
        verify(notificacionRepository, never()).save(any(NotificacionModel.class));
    }

    @Test
    void crearNotificacion_CuandoTipoEsInvalido_LanzaDatoInvalidoException() {
        // Given
        requestDTO.setTipo("PROMOCION");
        when(usuarioClient.obtenerUsuarioPorId(1L)).thenReturn(usuarioActivo);

        // When / Then
        DatoInvalidoException exception = assertThrows(
                DatoInvalidoException.class,
                () -> notificacionService.crearNotificacion(requestDTO)
        );

        assertEquals("Tipo inválido. Debe ser RESERVA, PRESTAMO, MULTA o SISTEMA", exception.getMessage());
        verify(notificacionRepository, never()).save(any(NotificacionModel.class));
    }

    @Test
    void crearNotificacion_CuandoEstadoEsInvalido_LanzaDatoInvalidoException() {
        // Given
        requestDTO.setEstado("BLOQUEADO");
        when(usuarioClient.obtenerUsuarioPorId(1L)).thenReturn(usuarioActivo);

        // When / Then
        DatoInvalidoException exception = assertThrows(
                DatoInvalidoException.class,
                () -> notificacionService.crearNotificacion(requestDTO)
        );

        assertEquals("Estado inválido. Debe ser PENDIENTE, LEIDA o ELIMINADA", exception.getMessage());
        verify(notificacionRepository, never()).save(any(NotificacionModel.class));
    }

    @Test
    void listarNotificaciones_CuandoExistenNotificaciones_RetornaLista() {
        // Given
        NotificacionModel notificacionDos = new NotificacionModel();
        notificacionDos.setId(2L);
        notificacionDos.setUsuarioId(2L);
        notificacionDos.setTitulo("Multa generada");
        notificacionDos.setMensaje("Tienes una multa pendiente");
        notificacionDos.setTipo("MULTA");
        notificacionDos.setEstado("PENDIENTE");
        notificacionDos.setFechaCreacion(LocalDateTime.now());

        when(notificacionRepository.findAll()).thenReturn(List.of(notificacionModel, notificacionDos));

        // When
        List<NotificacionResponseDTO> respuesta = notificacionService.listarNotificaciones();

        // Then
        assertEquals(2, respuesta.size());
        assertEquals("Reserva creada", respuesta.get(0).getTitulo());
        assertEquals("Multa generada", respuesta.get(1).getTitulo());
        verify(notificacionRepository).findAll();
    }

    @Test
    void obtenerNotificacionPorId_CuandoExiste_RetornaNotificacion() {
        // Given
        when(notificacionRepository.findById(1L)).thenReturn(Optional.of(notificacionModel));

        // When
        NotificacionResponseDTO respuesta = notificacionService.obtenerNotificacionPorId(1L);

        // Then
        assertEquals(1L, respuesta.getId());
        assertEquals("RESERVA", respuesta.getTipo());
        assertEquals("PENDIENTE", respuesta.getEstado());
        verify(notificacionRepository).findById(1L);
    }

    @Test
    void obtenerNotificacionPorId_CuandoNoExiste_LanzaRecursoNoEncontradoException() {
        // Given
        when(notificacionRepository.findById(99L)).thenReturn(Optional.empty());

        // When / Then
        RecursoNoEncontradoException exception = assertThrows(
                RecursoNoEncontradoException.class,
                () -> notificacionService.obtenerNotificacionPorId(99L)
        );

        assertEquals("Notificación no encontrada", exception.getMessage());
        verify(notificacionRepository).findById(99L);
    }

    @Test
    void listarPorUsuario_CuandoUsuarioValido_RetornaLista() {
        // Given
        when(usuarioClient.obtenerUsuarioPorId(1L)).thenReturn(usuarioActivo);
        when(notificacionRepository.findByUsuarioId(1L)).thenReturn(List.of(notificacionModel));

        // When
        List<NotificacionResponseDTO> respuesta = notificacionService.listarPorUsuario(1L);

        // Then
        assertEquals(1, respuesta.size());
        assertEquals(1L, respuesta.get(0).getUsuarioId());
        verify(notificacionRepository).findByUsuarioId(1L);
    }

    @Test
    void listarPendientesPorUsuario_CuandoUsuarioValido_RetornaListaPendiente() {
        // Given
        when(usuarioClient.obtenerUsuarioPorId(1L)).thenReturn(usuarioActivo);
        when(notificacionRepository.findByUsuarioIdAndEstado(1L, "PENDIENTE")).thenReturn(List.of(notificacionModel));

        // When
        List<NotificacionResponseDTO> respuesta = notificacionService.listarPendientesPorUsuario(1L);

        // Then
        assertEquals(1, respuesta.size());
        assertEquals("PENDIENTE", respuesta.get(0).getEstado());
        verify(notificacionRepository).findByUsuarioIdAndEstado(1L, "PENDIENTE");
    }

    @Test
    void listarPorEstado_CuandoEstadoValidoEnMinuscula_NormalizaYRetornaLista() {
        // Given
        when(notificacionRepository.findByEstado("PENDIENTE")).thenReturn(List.of(notificacionModel));

        // When
        List<NotificacionResponseDTO> respuesta = notificacionService.listarPorEstado(" pendiente ");

        // Then
        assertEquals(1, respuesta.size());
        assertEquals("PENDIENTE", respuesta.get(0).getEstado());
        verify(notificacionRepository).findByEstado("PENDIENTE");
    }

    @Test
    void listarPorEstado_CuandoEstadoEsVacio_LanzaDatoInvalidoException() {
        // When / Then
        DatoInvalidoException exception = assertThrows(
                DatoInvalidoException.class,
                () -> notificacionService.listarPorEstado(" ")
        );

        assertEquals("El estado es obligatorio", exception.getMessage());
        verify(notificacionRepository, never()).findByEstado(any());
    }

    @Test
    void actualizarNotificacion_CuandoDatosValidos_ActualizaNotificacion() {
        // Given
        NotificacionRequestDTO actualizarDTO = new NotificacionRequestDTO();
        actualizarDTO.setUsuarioId(1L);
        actualizarDTO.setTitulo("Préstamo creado");
        actualizarDTO.setMensaje("Tu préstamo fue creado correctamente");
        actualizarDTO.setTipo("prestamo");
        actualizarDTO.setEstado("leida");
        actualizarDTO.setReferenciaId(20L);
        actualizarDTO.setReferenciaTipo("PRESTAMO");

        when(notificacionRepository.findById(1L)).thenReturn(Optional.of(notificacionModel));
        when(usuarioClient.obtenerUsuarioPorId(1L)).thenReturn(usuarioActivo);
        when(notificacionRepository.save(any(NotificacionModel.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        NotificacionResponseDTO respuesta = notificacionService.actualizarNotificacion(1L, actualizarDTO);

        // Then
        assertEquals(1L, respuesta.getId());
        assertEquals("Préstamo creado", respuesta.getTitulo());
        assertEquals("PRESTAMO", respuesta.getTipo());
        assertEquals("LEIDA", respuesta.getEstado());
        assertNotNull(respuesta.getFechaLectura());
        assertEquals(20L, respuesta.getReferenciaId());

        verify(notificacionRepository).save(notificacionModel);
    }

    @Test
    void actualizarNotificacion_CuandoEstadoVuelveAPendiente_LimpiaFechaLectura() {
        // Given
        notificacionModel.setEstado("LEIDA");
        notificacionModel.setFechaLectura(LocalDateTime.now());

        NotificacionRequestDTO actualizarDTO = new NotificacionRequestDTO();
        actualizarDTO.setUsuarioId(1L);
        actualizarDTO.setTitulo("Reserva pendiente");
        actualizarDTO.setMensaje("La reserva volvió a quedar pendiente");
        actualizarDTO.setTipo("RESERVA");
        actualizarDTO.setEstado("PENDIENTE");
        actualizarDTO.setReferenciaId(10L);
        actualizarDTO.setReferenciaTipo("RESERVA");

        when(notificacionRepository.findById(1L)).thenReturn(Optional.of(notificacionModel));
        when(usuarioClient.obtenerUsuarioPorId(1L)).thenReturn(usuarioActivo);
        when(notificacionRepository.save(any(NotificacionModel.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        NotificacionResponseDTO respuesta = notificacionService.actualizarNotificacion(1L, actualizarDTO);

        // Then
        assertEquals("PENDIENTE", respuesta.getEstado());
        assertNull(respuesta.getFechaLectura());
        verify(notificacionRepository).save(notificacionModel);
    }

    @Test
    void actualizarNotificacion_CuandoNoExiste_LanzaRecursoNoEncontradoException() {
        // Given
        when(notificacionRepository.findById(99L)).thenReturn(Optional.empty());

        // When / Then
        RecursoNoEncontradoException exception = assertThrows(
                RecursoNoEncontradoException.class,
                () -> notificacionService.actualizarNotificacion(99L, requestDTO)
        );

        assertEquals("Notificación no encontrada", exception.getMessage());
        verify(usuarioClient, never()).obtenerUsuarioPorId(any());
        verify(notificacionRepository, never()).save(any(NotificacionModel.class));
    }

    @Test
    void marcarComoLeida_CuandoPendiente_CambiaEstadoALeida() {
        // Given
        when(notificacionRepository.findById(1L)).thenReturn(Optional.of(notificacionModel));
        when(notificacionRepository.save(any(NotificacionModel.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        NotificacionResponseDTO respuesta = notificacionService.marcarComoLeida(1L);

        // Then
        assertEquals("LEIDA", respuesta.getEstado());
        assertNotNull(respuesta.getFechaLectura());
        verify(notificacionRepository).save(notificacionModel);
    }

    @Test
    void marcarComoLeida_CuandoYaEstaLeida_LanzaDatoInvalidoException() {
        // Given
        notificacionModel.setEstado("LEIDA");
        when(notificacionRepository.findById(1L)).thenReturn(Optional.of(notificacionModel));

        // When / Then
        DatoInvalidoException exception = assertThrows(
                DatoInvalidoException.class,
                () -> notificacionService.marcarComoLeida(1L)
        );

        assertEquals("La notificación ya está leída", exception.getMessage());
        verify(notificacionRepository, never()).save(any(NotificacionModel.class));
    }

    @Test
    void marcarComoLeida_CuandoEstaEliminada_LanzaDatoInvalidoException() {
        // Given
        notificacionModel.setEstado("ELIMINADA");
        when(notificacionRepository.findById(1L)).thenReturn(Optional.of(notificacionModel));

        // When / Then
        DatoInvalidoException exception = assertThrows(
                DatoInvalidoException.class,
                () -> notificacionService.marcarComoLeida(1L)
        );

        assertEquals("No se puede marcar como leída una notificación eliminada", exception.getMessage());
        verify(notificacionRepository, never()).save(any(NotificacionModel.class));
    }

    @Test
    void eliminarNotificacion_CuandoExiste_CambiaEstadoAEliminada() {
        // Given
        when(notificacionRepository.findById(1L)).thenReturn(Optional.of(notificacionModel));
        when(notificacionRepository.save(any(NotificacionModel.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        notificacionService.eliminarNotificacion(1L);

        // Then
        assertEquals("ELIMINADA", notificacionModel.getEstado());
        verify(notificacionRepository).save(notificacionModel);
    }

    @Test
    void eliminarNotificacion_CuandoYaEstaEliminada_LanzaDatoInvalidoException() {
        // Given
        notificacionModel.setEstado("ELIMINADA");
        when(notificacionRepository.findById(1L)).thenReturn(Optional.of(notificacionModel));

        // When / Then
        DatoInvalidoException exception = assertThrows(
                DatoInvalidoException.class,
                () -> notificacionService.eliminarNotificacion(1L)
        );

        assertEquals("La notificación ya está eliminada", exception.getMessage());
        verify(notificacionRepository, never()).save(any(NotificacionModel.class));
    }
}

package GameShelf.ms_multa.service;

import GameShelf.ms_multa.client.PrestamoClient;
import GameShelf.ms_multa.client.UsuarioClient;
import GameShelf.ms_multa.dto.MultaRequestDTO;
import GameShelf.ms_multa.dto.MultaResponseDTO;
import GameShelf.ms_multa.dto.PagoMultaRequestDTO;
import GameShelf.ms_multa.dto.PagoMultaResponseDTO;
import GameShelf.ms_multa.dto.PrestamoResponseDTO;
import GameShelf.ms_multa.dto.UsuarioResponseDTO;
import GameShelf.ms_multa.exception.ComunicacionMicroservicioException;
import GameShelf.ms_multa.exception.DatoInvalidoException;
import GameShelf.ms_multa.exception.MultaNoEncontradaException;
import GameShelf.ms_multa.model.MultaModel;
import GameShelf.ms_multa.model.PagoMultaModel;
import GameShelf.ms_multa.repository.MultaRepository;
import GameShelf.ms_multa.repository.PagoMultaRepository;
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
class MultaServiceImplTest {

    @Mock
    private MultaRepository multaRepository;

    @Mock
    private PagoMultaRepository pagoMultaRepository;

    @Mock
    private UsuarioClient usuarioClient;

    @Mock
    private PrestamoClient prestamoClient;

    @InjectMocks
    private MultaServiceImpl multaService;

    private MultaRequestDTO requestDTO;
    private MultaModel multaModel;
    private UsuarioResponseDTO usuarioActivo;
    private PrestamoResponseDTO prestamoValido;

    @BeforeEach
    void setUp() {
        requestDTO = new MultaRequestDTO();
        requestDTO.setUsuarioId(1L);
        requestDTO.setPrestamoId(10L);
        requestDTO.setMonto(5000.0);
        requestDTO.setMotivo("Atraso en la devolución del videojuego");
        requestDTO.setEstado("PENDIENTE");

        multaModel = new MultaModel();
        multaModel.setId(1L);
        multaModel.setUsuarioId(1L);
        multaModel.setPrestamoId(10L);
        multaModel.setMonto(5000.0);
        multaModel.setMotivo("Atraso en la devolución del videojuego");
        multaModel.setFechaMulta(LocalDate.now());
        multaModel.setEstado("PENDIENTE");

        usuarioActivo = new UsuarioResponseDTO(
                1L,
                "pablo",
                "pablo@gmail.com",
                "CLIENTE",
                "ACTIVO"
        );

        prestamoValido = new PrestamoResponseDTO(
                10L,
                1L,
                2L,
                LocalDate.now().minusDays(10),
                LocalDate.now().minusDays(1),
                "PRESTADO"
        );
    }

    @Test
    void crearMulta_CuandoDatosValidos_GuardaMultaConEstadoPendiente() {
        // Given
        when(usuarioClient.buscarUsuarioPorId(1L)).thenReturn(usuarioActivo);
        when(prestamoClient.buscarPrestamoPorId(10L)).thenReturn(prestamoValido);
        when(multaRepository.existsByPrestamoIdAndEstado(10L, "PENDIENTE")).thenReturn(false);
        when(multaRepository.save(any(MultaModel.class))).thenAnswer(invocation -> {
            MultaModel multaGuardada = invocation.getArgument(0);
            multaGuardada.setId(1L);
            return multaGuardada;
        });

        // When
        MultaResponseDTO respuesta = multaService.crearMulta(requestDTO);

        // Then
        assertNotNull(respuesta);
        assertEquals(1L, respuesta.getId());
        assertEquals(1L, respuesta.getUsuarioId());
        assertEquals(10L, respuesta.getPrestamoId());
        assertEquals(5000.0, respuesta.getMonto());
        assertEquals("PENDIENTE", respuesta.getEstado());
        assertEquals(LocalDate.now(), respuesta.getFechaMulta());

        ArgumentCaptor<MultaModel> captor = ArgumentCaptor.forClass(MultaModel.class);
        verify(multaRepository).save(captor.capture());

        MultaModel multaEnviadaAGuardar = captor.getValue();
        assertEquals(1L, multaEnviadaAGuardar.getUsuarioId());
        assertEquals(10L, multaEnviadaAGuardar.getPrestamoId());
        assertEquals("PENDIENTE", multaEnviadaAGuardar.getEstado());
        assertEquals(LocalDate.now(), multaEnviadaAGuardar.getFechaMulta());
    }

    @Test
    void crearMulta_CuandoUsuarioNoExiste_LanzaDatoInvalidoException() {
        // Given
        when(usuarioClient.buscarUsuarioPorId(1L)).thenThrow(crearFeignNotFound());

        // When / Then
        DatoInvalidoException exception = assertThrows(
                DatoInvalidoException.class,
                () -> multaService.crearMulta(requestDTO)
        );

        assertEquals("El usuario ingresado no existe", exception.getMessage());
        verify(multaRepository, never()).save(any(MultaModel.class));
    }

    @Test
    void crearMulta_CuandoUsuarioEstaInactivo_LanzaDatoInvalidoException() {
        // Given
        usuarioActivo.setEstado("INACTIVO");
        when(usuarioClient.buscarUsuarioPorId(1L)).thenReturn(usuarioActivo);

        // When / Then
        DatoInvalidoException exception = assertThrows(
                DatoInvalidoException.class,
                () -> multaService.crearMulta(requestDTO)
        );

        assertEquals("El usuario no está activo", exception.getMessage());
        verify(multaRepository, never()).save(any(MultaModel.class));
    }

    @Test
    void crearMulta_CuandoUsuarioClientFalla_LanzaComunicacionMicroservicioException() {
        // Given
        when(usuarioClient.buscarUsuarioPorId(1L)).thenThrow(crearFeignServiceUnavailable());

        // When / Then
        ComunicacionMicroservicioException exception = assertThrows(
                ComunicacionMicroservicioException.class,
                () -> multaService.crearMulta(requestDTO)
        );

        assertEquals("No se pudo comunicar con ms-usuario", exception.getMessage());
        verify(multaRepository, never()).save(any(MultaModel.class));
    }

    @Test
    void crearMulta_CuandoPrestamoNoExiste_LanzaDatoInvalidoException() {
        // Given
        when(usuarioClient.buscarUsuarioPorId(1L)).thenReturn(usuarioActivo);
        when(prestamoClient.buscarPrestamoPorId(10L)).thenThrow(crearFeignNotFound());

        // When / Then
        DatoInvalidoException exception = assertThrows(
                DatoInvalidoException.class,
                () -> multaService.crearMulta(requestDTO)
        );

        assertEquals("El préstamo ingresado no existe", exception.getMessage());
        verify(multaRepository, never()).save(any(MultaModel.class));
    }

    @Test
    void crearMulta_CuandoPrestamoClientFalla_LanzaComunicacionMicroservicioException() {
        // Given
        when(usuarioClient.buscarUsuarioPorId(1L)).thenReturn(usuarioActivo);
        when(prestamoClient.buscarPrestamoPorId(10L)).thenThrow(crearFeignServiceUnavailable());

        // When / Then
        ComunicacionMicroservicioException exception = assertThrows(
                ComunicacionMicroservicioException.class,
                () -> multaService.crearMulta(requestDTO)
        );

        assertEquals("No se pudo comunicar con ms-prestamo", exception.getMessage());
        verify(multaRepository, never()).save(any(MultaModel.class));
    }

    @Test
    void crearMulta_CuandoPrestamoNoPerteneceAlUsuario_LanzaDatoInvalidoException() {
        // Given
        prestamoValido.setUsuarioId(99L);
        when(usuarioClient.buscarUsuarioPorId(1L)).thenReturn(usuarioActivo);
        when(prestamoClient.buscarPrestamoPorId(10L)).thenReturn(prestamoValido);

        // When / Then
        DatoInvalidoException exception = assertThrows(
                DatoInvalidoException.class,
                () -> multaService.crearMulta(requestDTO)
        );

        assertEquals("El préstamo no pertenece al usuario indicado", exception.getMessage());
        verify(multaRepository, never()).save(any(MultaModel.class));
    }

    @Test
    void crearMulta_CuandoYaTieneMultaPendiente_LanzaDatoInvalidoException() {
        // Given
        when(usuarioClient.buscarUsuarioPorId(1L)).thenReturn(usuarioActivo);
        when(prestamoClient.buscarPrestamoPorId(10L)).thenReturn(prestamoValido);
        when(multaRepository.existsByPrestamoIdAndEstado(10L, "PENDIENTE")).thenReturn(true);

        // When / Then
        DatoInvalidoException exception = assertThrows(
                DatoInvalidoException.class,
                () -> multaService.crearMulta(requestDTO)
        );

        assertEquals("Este préstamo ya tiene una multa pendiente", exception.getMessage());
        verify(multaRepository, never()).save(any(MultaModel.class));
    }

    @Test
    void listarMultas_CuandoExistenMultas_RetornaLista() {
        // Given
        MultaModel multaDos = new MultaModel();
        multaDos.setId(2L);
        multaDos.setUsuarioId(3L);
        multaDos.setPrestamoId(20L);
        multaDos.setMonto(7000.0);
        multaDos.setMotivo("Atraso extendido");
        multaDos.setFechaMulta(LocalDate.now());
        multaDos.setEstado("PENDIENTE");

        when(multaRepository.findAll()).thenReturn(List.of(multaModel, multaDos));

        // When
        List<MultaResponseDTO> respuesta = multaService.listarMultas();

        // Then
        assertEquals(2, respuesta.size());
        assertEquals(1L, respuesta.get(0).getUsuarioId());
        assertEquals(3L, respuesta.get(1).getUsuarioId());
        verify(multaRepository).findAll();
    }

    @Test
    void buscarPorId_CuandoMultaExiste_RetornaMulta() {
        // Given
        when(multaRepository.findById(1L)).thenReturn(Optional.of(multaModel));

        // When
        MultaResponseDTO respuesta = multaService.buscarPorId(1L);

        // Then
        assertEquals(1L, respuesta.getId());
        assertEquals("PENDIENTE", respuesta.getEstado());
        verify(multaRepository).findById(1L);
    }

    @Test
    void buscarPorId_CuandoMultaNoExiste_LanzaMultaNoEncontradaException() {
        // Given
        when(multaRepository.findById(99L)).thenReturn(Optional.empty());

        // When / Then
        MultaNoEncontradaException exception = assertThrows(
                MultaNoEncontradaException.class,
                () -> multaService.buscarPorId(99L)
        );

        assertEquals("Multa no encontrada", exception.getMessage());
        verify(multaRepository).findById(99L);
    }

    @Test
    void buscarPorUsuario_CuandoExistenMultas_RetornaLista() {
        // Given
        when(multaRepository.findByUsuarioId(1L)).thenReturn(List.of(multaModel));

        // When
        List<MultaResponseDTO> respuesta = multaService.buscarPorUsuario(1L);

        // Then
        assertEquals(1, respuesta.size());
        assertEquals(1L, respuesta.get(0).getUsuarioId());
        verify(multaRepository).findByUsuarioId(1L);
    }

    @Test
    void buscarPorPrestamo_CuandoExistenMultas_RetornaLista() {
        // Given
        when(multaRepository.findByPrestamoId(10L)).thenReturn(List.of(multaModel));

        // When
        List<MultaResponseDTO> respuesta = multaService.buscarPorPrestamo(10L);

        // Then
        assertEquals(1, respuesta.size());
        assertEquals(10L, respuesta.get(0).getPrestamoId());
        verify(multaRepository).findByPrestamoId(10L);
    }

    @Test
    void buscarPorEstado_CuandoEstadoValidoEnMinuscula_NormalizaYRetornaLista() {
        // Given
        when(multaRepository.findByEstado("PENDIENTE")).thenReturn(List.of(multaModel));

        // When
        List<MultaResponseDTO> respuesta = multaService.buscarPorEstado(" pendiente ");

        // Then
        assertEquals(1, respuesta.size());
        assertEquals("PENDIENTE", respuesta.get(0).getEstado());
        verify(multaRepository).findByEstado("PENDIENTE");
    }

    @Test
    void buscarPorEstado_CuandoEstadoInvalido_LanzaDatoInvalidoException() {
        // When / Then
        DatoInvalidoException exception = assertThrows(
                DatoInvalidoException.class,
                () -> multaService.buscarPorEstado("BLOQUEADO")
        );

        assertEquals("El estado debe ser PENDIENTE, PAGADA o ANULADA", exception.getMessage());
        verify(multaRepository, never()).findByEstado(any());
    }

    @Test
    void actualizarMulta_CuandoDatosValidos_ActualizaMulta() {
        // Given
        MultaRequestDTO actualizarDTO = new MultaRequestDTO();
        actualizarDTO.setUsuarioId(1L);
        actualizarDTO.setPrestamoId(10L);
        actualizarDTO.setMonto(7000.0);
        actualizarDTO.setMotivo("Atraso extendido en la devolución");
        actualizarDTO.setEstado("pagada");

        when(multaRepository.findById(1L)).thenReturn(Optional.of(multaModel));
        when(usuarioClient.buscarUsuarioPorId(1L)).thenReturn(usuarioActivo);
        when(prestamoClient.buscarPrestamoPorId(10L)).thenReturn(prestamoValido);
        when(multaRepository.save(any(MultaModel.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        MultaResponseDTO respuesta = multaService.actualizarMulta(1L, actualizarDTO);

        // Then
        assertEquals(1L, respuesta.getId());
        assertEquals(7000.0, respuesta.getMonto());
        assertEquals("Atraso extendido en la devolución", respuesta.getMotivo());
        assertEquals("PAGADA", respuesta.getEstado());
        verify(multaRepository).save(multaModel);
    }

    @Test
    void actualizarMulta_CuandoEstadoInvalido_LanzaDatoInvalidoException() {
        // Given
        requestDTO.setEstado("BLOQUEADO");
        when(multaRepository.findById(1L)).thenReturn(Optional.of(multaModel));
        when(usuarioClient.buscarUsuarioPorId(1L)).thenReturn(usuarioActivo);
        when(prestamoClient.buscarPrestamoPorId(10L)).thenReturn(prestamoValido);

        // When / Then
        DatoInvalidoException exception = assertThrows(
                DatoInvalidoException.class,
                () -> multaService.actualizarMulta(1L, requestDTO)
        );

        assertEquals("El estado debe ser PENDIENTE, PAGADA o ANULADA", exception.getMessage());
        verify(multaRepository, never()).save(any(MultaModel.class));
    }

    @Test
    void pagarMulta_CuandoMultaPendiente_CambiaEstadoAPagada() {
        // Given
        when(multaRepository.findById(1L)).thenReturn(Optional.of(multaModel));
        when(multaRepository.save(any(MultaModel.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        MultaResponseDTO respuesta = multaService.pagarMulta(1L);

        // Then
        assertEquals("PAGADA", respuesta.getEstado());
        verify(multaRepository).save(multaModel);
    }

    @Test
    void pagarMulta_CuandoNoEstaPendiente_LanzaDatoInvalidoException() {
        // Given
        multaModel.setEstado("ANULADA");
        when(multaRepository.findById(1L)).thenReturn(Optional.of(multaModel));

        // When / Then
        DatoInvalidoException exception = assertThrows(
                DatoInvalidoException.class,
                () -> multaService.pagarMulta(1L)
        );

        assertEquals("Solo se pueden pagar multas pendientes", exception.getMessage());
        verify(multaRepository, never()).save(any(MultaModel.class));
    }

    @Test
    void anularMulta_CuandoNoEstaPagada_CambiaEstadoAAnulada() {
        // Given
        when(multaRepository.findById(1L)).thenReturn(Optional.of(multaModel));
        when(multaRepository.save(any(MultaModel.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        MultaResponseDTO respuesta = multaService.anularMulta(1L);

        // Then
        assertEquals("ANULADA", respuesta.getEstado());
        verify(multaRepository).save(multaModel);
    }

    @Test
    void anularMulta_CuandoEstaPagada_LanzaDatoInvalidoException() {
        // Given
        multaModel.setEstado("PAGADA");
        when(multaRepository.findById(1L)).thenReturn(Optional.of(multaModel));

        // When / Then
        DatoInvalidoException exception = assertThrows(
                DatoInvalidoException.class,
                () -> multaService.anularMulta(1L)
        );

        assertEquals("No se puede anular una multa pagada", exception.getMessage());
        verify(multaRepository, never()).save(any(MultaModel.class));
    }

    @Test
    void eliminarMulta_CuandoExiste_EliminaMulta() {
        // Given
        when(multaRepository.findById(1L)).thenReturn(Optional.of(multaModel));

        // When
        multaService.eliminarMulta(1L);

        // Then
        verify(multaRepository).delete(multaModel);
    }

    @Test
    void registrarPago_CuandoDatosValidos_GuardaPagoYCambiaMultaAPagada() {
        // Given
        PagoMultaRequestDTO pagoDTO = new PagoMultaRequestDTO();
        pagoDTO.setMontoPagado(5000.0);
        pagoDTO.setMetodoPago(" efectivo ");

        when(multaRepository.findById(1L)).thenReturn(Optional.of(multaModel));
        when(multaRepository.save(any(MultaModel.class))).thenAnswer(invocation -> {
            MultaModel multaGuardada = invocation.getArgument(0);
            PagoMultaModel pago = multaGuardada.getPagos().get(0);
            pago.setId(1L);
            return multaGuardada;
        });

        // When
        PagoMultaResponseDTO respuesta = multaService.registrarPago(1L, pagoDTO);

        // Then
        assertEquals(1L, respuesta.getId());
        assertEquals(1L, respuesta.getMultaId());
        assertEquals(5000.0, respuesta.getMontoPagado());
        assertEquals("EFECTIVO", respuesta.getMetodoPago());
        assertEquals("CONFIRMADO", respuesta.getEstadoPago());
        assertEquals("PAGADA", multaModel.getEstado());
        verify(multaRepository).save(multaModel);
    }

    @Test
    void registrarPago_CuandoMultaAnulada_LanzaDatoInvalidoException() {
        // Given
        multaModel.setEstado("ANULADA");
        PagoMultaRequestDTO pagoDTO = new PagoMultaRequestDTO();
        pagoDTO.setMontoPagado(5000.0);
        pagoDTO.setMetodoPago("EFECTIVO");

        when(multaRepository.findById(1L)).thenReturn(Optional.of(multaModel));

        // When / Then
        DatoInvalidoException exception = assertThrows(
                DatoInvalidoException.class,
                () -> multaService.registrarPago(1L, pagoDTO)
        );

        assertEquals("No se puede pagar una multa anulada", exception.getMessage());
        verify(multaRepository, never()).save(any(MultaModel.class));
    }

    @Test
    void registrarPago_CuandoMontoNoCoincide_LanzaDatoInvalidoException() {
        // Given
        PagoMultaRequestDTO pagoDTO = new PagoMultaRequestDTO();
        pagoDTO.setMontoPagado(3000.0);
        pagoDTO.setMetodoPago("EFECTIVO");

        when(multaRepository.findById(1L)).thenReturn(Optional.of(multaModel));

        // When / Then
        DatoInvalidoException exception = assertThrows(
                DatoInvalidoException.class,
                () -> multaService.registrarPago(1L, pagoDTO)
        );

        assertEquals("El monto pagado debe ser exactamente igual al monto de la multa: 5000.0", exception.getMessage());
        verify(multaRepository, never()).save(any(MultaModel.class));
    }

    @Test
    void listarPagosPorMulta_CuandoExistenPagos_RetornaLista() {
        // Given
        PagoMultaModel pago = new PagoMultaModel();
        pago.setId(1L);
        pago.setMulta(multaModel);
        pago.setMontoPagado(5000.0);
        pago.setFechaPago(LocalDate.now());
        pago.setMetodoPago("EFECTIVO");
        pago.setEstadoPago("CONFIRMADO");

        when(multaRepository.findById(1L)).thenReturn(Optional.of(multaModel));
        when(pagoMultaRepository.findByMultaId(1L)).thenReturn(List.of(pago));

        // When
        List<PagoMultaResponseDTO> respuesta = multaService.listarPagosPorMulta(1L);

        // Then
        assertEquals(1, respuesta.size());
        assertEquals(1L, respuesta.get(0).getMultaId());
        assertEquals("EFECTIVO", respuesta.get(0).getMetodoPago());
        verify(pagoMultaRepository).findByMultaId(1L);
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
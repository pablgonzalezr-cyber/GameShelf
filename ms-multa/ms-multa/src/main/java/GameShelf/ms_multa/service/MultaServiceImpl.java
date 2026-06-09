package GameShelf.ms_multa.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

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
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class MultaServiceImpl implements MultaService {

    private final MultaRepository multaRepository;
    private final UsuarioClient usuarioClient;
    private final PrestamoClient prestamoClient;
    private final PagoMultaRepository pagoMultaRepository;

    public MultaServiceImpl(MultaRepository multaRepository,
                            PagoMultaRepository pagoMultaRepository,
                            UsuarioClient usuarioClient,
                            PrestamoClient prestamoClient) {
        this.multaRepository = multaRepository;
        this.pagoMultaRepository = pagoMultaRepository;
        this.usuarioClient = usuarioClient;
        this.prestamoClient = prestamoClient;
    }

    @Override
    public MultaResponseDTO crearMulta(MultaRequestDTO multaRequestDTO) {

        log.info("Creando multa para usuario ID: {} y préstamo ID: {}",
                multaRequestDTO.getUsuarioId(),
                multaRequestDTO.getPrestamoId());

        validarUsuario(multaRequestDTO.getUsuarioId());

        PrestamoResponseDTO prestamo = validarPrestamo(multaRequestDTO.getPrestamoId());

        if (!prestamo.getUsuarioId().equals(multaRequestDTO.getUsuarioId())) {
            throw new DatoInvalidoException("El préstamo no pertenece al usuario indicado");
        }

        if (multaRepository.existsByPrestamoIdAndEstado(multaRequestDTO.getPrestamoId(), "PENDIENTE")) {
            throw new DatoInvalidoException("Este préstamo ya tiene una multa pendiente");
        }

        MultaModel multa = new MultaModel();
        multa.setUsuarioId(multaRequestDTO.getUsuarioId());
        multa.setPrestamoId(multaRequestDTO.getPrestamoId());
        multa.setMonto(multaRequestDTO.getMonto());
        multa.setMotivo(multaRequestDTO.getMotivo());
        multa.setFechaMulta(LocalDate.now());
        multa.setEstado("PENDIENTE");

        MultaModel multaGuardada = multaRepository.save(multa);

        log.info("Multa creada correctamente con ID: {}", multaGuardada.getId());

        return convertirAResponseDTO(multaGuardada);
    }

    @Override
    public List<MultaResponseDTO> listarMultas() {

        log.info("Listando multas");

        List<MultaModel> multas = multaRepository.findAll();
        List<MultaResponseDTO> respuesta = new ArrayList<>();

        for (MultaModel multa : multas) {
            respuesta.add(convertirAResponseDTO(multa));
        }

        return respuesta;
    }

    @Override
    public MultaResponseDTO buscarPorId(Long id) {

        log.info("Buscando multa ID: {}", id);

        MultaModel multa = multaRepository.findById(id)
                .orElseThrow(() -> new MultaNoEncontradaException("Multa no encontrada"));

        return convertirAResponseDTO(multa);
    }

    @Override
    public List<MultaResponseDTO> buscarPorUsuario(Long usuarioId) {

        log.info("Buscando multas por usuario ID: {}", usuarioId);

        List<MultaModel> multas = multaRepository.findByUsuarioId(usuarioId);
        List<MultaResponseDTO> respuesta = new ArrayList<>();

        for (MultaModel multa : multas) {
            respuesta.add(convertirAResponseDTO(multa));
        }

        return respuesta;
    }

    @Override
    public List<MultaResponseDTO> buscarPorPrestamo(Long prestamoId) {

        log.info("Buscando multas por préstamo ID: {}", prestamoId);

        List<MultaModel> multas = multaRepository.findByPrestamoId(prestamoId);
        List<MultaResponseDTO> respuesta = new ArrayList<>();

        for (MultaModel multa : multas) {
            respuesta.add(convertirAResponseDTO(multa));
        }

        return respuesta;
    }

    @Override
    public List<MultaResponseDTO> buscarPorEstado(String estado) {

        log.info("Buscando multas por estado: {}", estado);

        String estadoLimpio = validarEstadoMulta(estado);

        List<MultaModel> multas = multaRepository.findByEstado(estadoLimpio);
        List<MultaResponseDTO> respuesta = new ArrayList<>();

        for (MultaModel multa : multas) {
            respuesta.add(convertirAResponseDTO(multa));
        }

        return respuesta;
    }

    @Override
    public MultaResponseDTO actualizarMulta(Long id, MultaRequestDTO multaRequestDTO) {

        log.info("Actualizando multa ID: {}", id);

        MultaModel multa = multaRepository.findById(id)
                .orElseThrow(() -> new MultaNoEncontradaException("Multa no encontrada"));

        validarUsuario(multaRequestDTO.getUsuarioId());

        PrestamoResponseDTO prestamo = validarPrestamo(multaRequestDTO.getPrestamoId());

        if (!prestamo.getUsuarioId().equals(multaRequestDTO.getUsuarioId())) {
            throw new DatoInvalidoException("El préstamo no pertenece al usuario indicado");
        }

        String estadoLimpio = validarEstadoMulta(multaRequestDTO.getEstado());

        multa.setUsuarioId(multaRequestDTO.getUsuarioId());
        multa.setPrestamoId(multaRequestDTO.getPrestamoId());
        multa.setMonto(multaRequestDTO.getMonto());
        multa.setMotivo(multaRequestDTO.getMotivo());
        multa.setEstado(estadoLimpio);

        MultaModel multaActualizada = multaRepository.save(multa);

        log.info("Multa actualizada correctamente con ID: {}", id);

        return convertirAResponseDTO(multaActualizada);
    }

    @Override
    public MultaResponseDTO pagarMulta(Long id) {

        log.info("Pagando multa ID: {}", id);

        MultaModel multa = multaRepository.findById(id)
                .orElseThrow(() -> new MultaNoEncontradaException("Multa no encontrada"));

        if (!multa.getEstado().equalsIgnoreCase("PENDIENTE")) {
            throw new DatoInvalidoException("Solo se pueden pagar multas pendientes");
        }

        multa.setEstado("PAGADA");

        MultaModel multaActualizada = multaRepository.save(multa);

        log.info("Multa pagada correctamente con ID: {}", id);

        return convertirAResponseDTO(multaActualizada);
    }

    @Override
    public MultaResponseDTO anularMulta(Long id) {

        log.info("Anulando multa ID: {}", id);

        MultaModel multa = multaRepository.findById(id)
                .orElseThrow(() -> new MultaNoEncontradaException("Multa no encontrada"));

        if (multa.getEstado().equalsIgnoreCase("PAGADA")) {
            throw new DatoInvalidoException("No se puede anular una multa pagada");
        }

        multa.setEstado("ANULADA");

        MultaModel multaActualizada = multaRepository.save(multa);

        log.info("Multa anulada correctamente con ID: {}", id);

        return convertirAResponseDTO(multaActualizada);
    }

    @Override
    public void eliminarMulta(Long id) {

        log.info("Eliminando multa ID: {}", id);

        MultaModel multa = multaRepository.findById(id)
                .orElseThrow(() -> new MultaNoEncontradaException("Multa no encontrada"));

        multaRepository.delete(multa);

        log.info("Multa eliminada correctamente con ID: {}", id);
    }

    @Override
    public PagoMultaResponseDTO registrarPago(Long multaId, PagoMultaRequestDTO pagoRequestDTO) {

        log.info("Registrando pago para multa ID: {}", multaId);

        MultaModel multa = multaRepository.findById(multaId)
                .orElseThrow(() -> new MultaNoEncontradaException("Multa no encontrada"));

        if (multa.getEstado().equalsIgnoreCase("ANULADA")) {
            throw new DatoInvalidoException("No se puede pagar una multa anulada");
        }

        if (multa.getEstado().equalsIgnoreCase("PAGADA")) {
            throw new DatoInvalidoException("La multa ya se encuentra pagada");
        }

        if (pagoRequestDTO.getMontoPagado() == null) {
            throw new DatoInvalidoException("El monto pagado es obligatorio");
        }

        if (Double.compare(pagoRequestDTO.getMontoPagado(), multa.getMonto()) != 0) {
            throw new DatoInvalidoException(
                    "El monto pagado debe ser exactamente igual al monto de la multa: " + multa.getMonto());
        }

        PagoMultaModel pago = new PagoMultaModel();
        pago.setMulta(multa);
        pago.setMontoPagado(pagoRequestDTO.getMontoPagado());
        pago.setFechaPago(LocalDate.now());
        pago.setMetodoPago(pagoRequestDTO.getMetodoPago().trim().toUpperCase());
        pago.setEstadoPago("CONFIRMADO");

        multa.getPagos().add(pago);

        // Como el pago debe ser completo, la multa pasa directamente a PAGADA
        multa.setEstado("PAGADA");

        MultaModel multaGuardada = multaRepository.save(multa);

        PagoMultaModel pagoGuardado = multaGuardada.getPagos().get(multaGuardada.getPagos().size() - 1);

        log.info("Pago registrado correctamente para multa ID: {}", multaId);

        return convertirPagoAResponseDTO(pagoGuardado);
    }  

    @Override
    public List<PagoMultaResponseDTO> listarPagosPorMulta(Long multaId) {

        log.info("Listando pagos de multa ID: {}", multaId);

        multaRepository.findById(multaId)
                .orElseThrow(() -> new MultaNoEncontradaException("Multa no encontrada"));

        List<PagoMultaModel> pagos = pagoMultaRepository.findByMultaId(multaId);
        List<PagoMultaResponseDTO> respuesta = new ArrayList<>();

        for (PagoMultaModel pago : pagos) {
            respuesta.add(convertirPagoAResponseDTO(pago));
        }

        return respuesta;
    }

    private UsuarioResponseDTO validarUsuario(Long usuarioId) {

        if (usuarioId == null) {
            throw new DatoInvalidoException("El usuario es obligatorio");
        }

        try {
            log.info("Validando usuario con ms-usuario ID: {}", usuarioId);

            UsuarioResponseDTO usuario = usuarioClient.buscarUsuarioPorId(usuarioId);

            if (usuario == null) {
                throw new ComunicacionMicroservicioException("El microservicio de usuario no devolvió información");
            }

            if (usuario.getEstado() != null && !usuario.getEstado().equalsIgnoreCase("ACTIVO")) {
                throw new DatoInvalidoException("El usuario no está activo");
            }

            return usuario;

        } catch (FeignException.NotFound e) {
            throw new DatoInvalidoException("El usuario ingresado no existe");

        } catch (FeignException e) {
            throw new ComunicacionMicroservicioException("No se pudo comunicar con ms-usuario");
        }
    }

    private PrestamoResponseDTO validarPrestamo(Long prestamoId) {

        if (prestamoId == null) {
            throw new DatoInvalidoException("El préstamo es obligatorio");
        }

        try {
            log.info("Validando préstamo con ms-prestamo ID: {}", prestamoId);

            PrestamoResponseDTO prestamo = prestamoClient.buscarPrestamoPorId(prestamoId);

            if (prestamo == null) {
                throw new ComunicacionMicroservicioException("El microservicio de préstamo no devolvió información");
            }

            return prestamo;

        } catch (FeignException.NotFound e) {
            throw new DatoInvalidoException("El préstamo ingresado no existe");

        } catch (FeignException e) {
            throw new ComunicacionMicroservicioException("No se pudo comunicar con ms-prestamo");
        }
    }

    private String validarEstadoMulta(String estado) {

        if (estado == null || estado.trim().isEmpty()) {
            throw new DatoInvalidoException("El estado es obligatorio");
        }

        String estadoLimpio = estado.trim().toUpperCase();

        if (!estadoLimpio.equals("PENDIENTE")
                && !estadoLimpio.equals("PAGADA")
                && !estadoLimpio.equals("ANULADA")) {

            throw new DatoInvalidoException("El estado debe ser PENDIENTE, PAGADA o ANULADA");
        }

        return estadoLimpio;
    }

    private PagoMultaResponseDTO convertirPagoAResponseDTO(PagoMultaModel pago) {

        return new PagoMultaResponseDTO(
                pago.getId(),
                pago.getMulta().getId(),
                pago.getMontoPagado(),
                pago.getFechaPago(),
                pago.getMetodoPago(),
                pago.getEstadoPago());
    }

    private MultaResponseDTO convertirAResponseDTO(MultaModel multa) {

        return new MultaResponseDTO(
                multa.getId(),
                multa.getUsuarioId(),
                multa.getPrestamoId(),
                multa.getMonto(),
                multa.getMotivo(),
                multa.getFechaMulta(),
                multa.getEstado());
    }
}
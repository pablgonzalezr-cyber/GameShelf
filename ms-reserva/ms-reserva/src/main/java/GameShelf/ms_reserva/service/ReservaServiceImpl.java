package GameShelf.ms_reserva.service;

import GameShelf.ms_reserva.client.StockClient;
import GameShelf.ms_reserva.client.UsuarioClient;
import GameShelf.ms_reserva.client.VideojuegoClient;
import GameShelf.ms_reserva.dto.ReservaRequestDTO;
import GameShelf.ms_reserva.dto.ReservaResponseDTO;
import GameShelf.ms_reserva.dto.StockResponseDTO;
import GameShelf.ms_reserva.dto.UsuarioResponseDTO;
import GameShelf.ms_reserva.dto.VideojuegoResponseDTO;
import GameShelf.ms_reserva.exception.DatoDuplicadoException;
import GameShelf.ms_reserva.exception.DatoInvalidoException;
import GameShelf.ms_reserva.exception.RecursoNoEncontradoException;
import GameShelf.ms_reserva.model.ReservaModel;
import GameShelf.ms_reserva.repository.ReservaRepository;

import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@Slf4j
public class ReservaServiceImpl implements ReservaService {

    private final ReservaRepository reservaRepository;
    private final UsuarioClient usuarioClient;
    private final VideojuegoClient videojuegoClient;
    private final StockClient stockClient;

    public ReservaServiceImpl(ReservaRepository reservaRepository,
                              UsuarioClient usuarioClient,
                              VideojuegoClient videojuegoClient,
                              StockClient stockClient) {
        this.reservaRepository = reservaRepository;
        this.usuarioClient = usuarioClient;
        this.videojuegoClient = videojuegoClient;
        this.stockClient = stockClient;
    }

    @Override
    public List<ReservaResponseDTO> listarReservas() {

        List<ReservaModel> reservas = reservaRepository.findAll();
        List<ReservaResponseDTO> respuesta = new ArrayList<>();

        for (ReservaModel reserva : reservas) {
            respuesta.add(convertirAResponseDTO(reserva));
        }

        return respuesta;
    }

    @Override
    public ReservaResponseDTO buscarReservaPorId(Long id) {

        ReservaModel reserva = reservaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Reserva no encontrada"));

        return convertirAResponseDTO(reserva);
    }

    @Override
    public List<ReservaResponseDTO> buscarReservasPorUsuario(Long usuarioId) {

        validarUsuario(usuarioId);

        List<ReservaModel> reservas = reservaRepository.findByUsuarioId(usuarioId);
        List<ReservaResponseDTO> respuesta = new ArrayList<>();

        for (ReservaModel reserva : reservas) {
            respuesta.add(convertirAResponseDTO(reserva));
        }

        return respuesta;
    }

    @Override
    public List<ReservaResponseDTO> buscarReservasPorVideojuego(Long videojuegoId) {

        validarVideojuego(videojuegoId);

        List<ReservaModel> reservas = reservaRepository.findByVideojuegoId(videojuegoId);
        List<ReservaResponseDTO> respuesta = new ArrayList<>();

        for (ReservaModel reserva : reservas) {
            respuesta.add(convertirAResponseDTO(reserva));
        }

        return respuesta;
    }

    @Override
    public List<ReservaResponseDTO> buscarReservasPorEstado(String estado) {

        String estadoLimpio = validarEstado(estado);

        List<ReservaModel> reservas = reservaRepository.findByEstado(estadoLimpio);
        List<ReservaResponseDTO> respuesta = new ArrayList<>();

        for (ReservaModel reserva : reservas) {
            respuesta.add(convertirAResponseDTO(reserva));
        }

        return respuesta;
    }

    @Override
    public ReservaResponseDTO crearReserva(ReservaRequestDTO reservaRequestDTO) {

        log.info("Creando reserva para usuario {} y videojuego {}",
                reservaRequestDTO.getUsuarioId(),
                reservaRequestDTO.getVideojuegoId());

        validarUsuario(reservaRequestDTO.getUsuarioId());

        validarVideojuego(reservaRequestDTO.getVideojuegoId());

        validarStockDisponible(reservaRequestDTO.getVideojuegoId());

        validarFechaVencimiento(reservaRequestDTO.getFechaVencimiento());

        List<String> estadosActivos = Arrays.asList("PENDIENTE", "CONFIRMADA");

        boolean existeReservaActiva = reservaRepository.existsByUsuarioIdAndVideojuegoIdAndEstadoIn(
                reservaRequestDTO.getUsuarioId(),
                reservaRequestDTO.getVideojuegoId(),
                estadosActivos
        );

        if (existeReservaActiva) {
            throw new DatoDuplicadoException("El usuario ya tiene una reserva activa para este videojuego");
        }

        stockClient.reducirStock(reservaRequestDTO.getVideojuegoId());

        ReservaModel reserva = new ReservaModel();
        reserva.setUsuarioId(reservaRequestDTO.getUsuarioId());
        reserva.setVideojuegoId(reservaRequestDTO.getVideojuegoId());
        reserva.setFechaReserva(LocalDate.now());
        reserva.setFechaVencimiento(reservaRequestDTO.getFechaVencimiento());
        reserva.setEstado("PENDIENTE");

        ReservaModel reservaGuardada = reservaRepository.save(reserva);

        log.info("Reserva creada correctamente con ID: {}", reservaGuardada.getId());

        return convertirAResponseDTO(reservaGuardada);
    }

    @Override
    public ReservaResponseDTO actualizarReserva(Long id, ReservaRequestDTO reservaRequestDTO) {

        log.info("Actualizando reserva ID: {}", id);

        ReservaModel reserva = reservaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Reserva no encontrada"));

        if (!reserva.getUsuarioId().equals(reservaRequestDTO.getUsuarioId())) {
            throw new DatoInvalidoException("No se puede cambiar el usuario de una reserva existente");
        }

        if (!reserva.getVideojuegoId().equals(reservaRequestDTO.getVideojuegoId())) {
            throw new DatoInvalidoException("No se puede cambiar el videojuego de una reserva existente");
        }

        validarUsuario(reservaRequestDTO.getUsuarioId());

        validarVideojuego(reservaRequestDTO.getVideojuegoId());

        validarFechaVencimiento(reservaRequestDTO.getFechaVencimiento());

        reserva.setFechaVencimiento(reservaRequestDTO.getFechaVencimiento());

        if (reservaRequestDTO.getEstado() != null && !reservaRequestDTO.getEstado().trim().isEmpty()) {
            String estadoLimpio = validarEstado(reservaRequestDTO.getEstado());
            reserva.setEstado(estadoLimpio);
        }

        ReservaModel reservaActualizada = reservaRepository.save(reserva);

        log.info("Reserva actualizada correctamente con ID: {}", reservaActualizada.getId());

        return convertirAResponseDTO(reservaActualizada);
    }

    @Override
    public ReservaResponseDTO confirmarReserva(Long id) {

        log.info("Confirmando reserva ID: {}", id);

        ReservaModel reserva = reservaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Reserva no encontrada"));

        if (!reserva.getEstado().equalsIgnoreCase("PENDIENTE")) {
            throw new DatoInvalidoException("Solo se pueden confirmar reservas pendientes");
        }

        reserva.setEstado("CONFIRMADA");

        ReservaModel reservaConfirmada = reservaRepository.save(reserva);

        log.info("Reserva confirmada correctamente con ID: {}", reservaConfirmada.getId());

        return convertirAResponseDTO(reservaConfirmada);
    }

    @Override
    public ReservaResponseDTO cancelarReserva(Long id) {

        log.info("Cancelando reserva ID: {}", id);

        ReservaModel reserva = reservaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Reserva no encontrada"));

        if (reserva.getEstado().equalsIgnoreCase("CANCELADA")) {
            throw new DatoInvalidoException("La reserva ya está cancelada");
        }

        if (reserva.getEstado().equalsIgnoreCase("EXPIRADA")) {
            throw new DatoInvalidoException("No se puede cancelar una reserva expirada");
        }

        stockClient.aumentarStock(reserva.getVideojuegoId());

        reserva.setEstado("CANCELADA");

        ReservaModel reservaCancelada = reservaRepository.save(reserva);

        log.info("Reserva cancelada correctamente con ID: {}", reservaCancelada.getId());

        return convertirAResponseDTO(reservaCancelada);
    }

    @Override
    public void eliminarReserva(Long id) {

        log.info("Eliminando/cancelando reserva ID: {}", id);

        ReservaModel reserva = reservaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Reserva no encontrada"));

        if (reserva.getEstado().equalsIgnoreCase("CANCELADA")) {
            throw new DatoInvalidoException("La reserva ya está cancelada");
        }

        if (reserva.getEstado().equalsIgnoreCase("EXPIRADA")) {
            throw new DatoInvalidoException("No se puede eliminar una reserva expirada");
        }

        stockClient.aumentarStock(reserva.getVideojuegoId());

        reserva.setEstado("CANCELADA");

        reservaRepository.save(reserva);

        log.info("Reserva eliminada/cancelada correctamente con ID: {}", id);
    }

    private void validarUsuario(Long usuarioId) {

        UsuarioResponseDTO usuario = usuarioClient.obtenerUsuarioPorId(usuarioId);

        if (usuario == null) {
            throw new DatoInvalidoException("El usuario no existe");
        }

        if (usuario.getEstado() != null && !usuario.getEstado().equalsIgnoreCase("ACTIVO")) {
            throw new DatoInvalidoException("El usuario no está activo");
        }
    }

    private void validarVideojuego(Long videojuegoId) {

        VideojuegoResponseDTO videojuego = videojuegoClient.obtenerVideojuegoPorId(videojuegoId);

        if (videojuego == null) {
            throw new DatoInvalidoException("El videojuego no existe");
        }

        if (videojuego.getEstado() == null || !videojuego.getEstado().trim().equalsIgnoreCase("DISPONIBLE")) {
            throw new DatoInvalidoException("El videojuego no está disponible para reservar");
        }
    }

    private void validarStockDisponible(Long videojuegoId) {

        StockResponseDTO stock = stockClient.obtenerStockPorVideojuego(videojuegoId);

        if (stock == null) {
            throw new DatoInvalidoException("No existe stock para este videojuego");
        }

        if (stock.getEstado() == null || !stock.getEstado().trim().equalsIgnoreCase("ACTIVO")) {
            throw new DatoInvalidoException("El stock del videojuego no está activo");
        }

        if (stock.getCantidadDisponible() == null || stock.getCantidadDisponible() <= 0) {
            throw new DatoInvalidoException("No hay stock disponible para reservar este videojuego");
        }
    }

    private void validarFechaVencimiento(LocalDate fechaVencimiento) {

        if (fechaVencimiento == null) {
            throw new DatoInvalidoException("La fecha de vencimiento es obligatoria");
        }

        if (fechaVencimiento.isBefore(LocalDate.now())) {
            throw new DatoInvalidoException("La fecha de vencimiento no puede ser anterior a la fecha actual");
        }
    }

    private String validarEstado(String estado) {

        if (estado == null || estado.trim().isEmpty()) {
            throw new DatoInvalidoException("El estado es obligatorio");
        }

        String estadoLimpio = estado.trim().toUpperCase();

        if (!estadoLimpio.equals("PENDIENTE")
                && !estadoLimpio.equals("CONFIRMADA")
                && !estadoLimpio.equals("CANCELADA")
                && !estadoLimpio.equals("EXPIRADA")) {

            throw new DatoInvalidoException("Estado de reserva inválido. Use PENDIENTE, CONFIRMADA, CANCELADA o EXPIRADA");
        }

        return estadoLimpio;
    }

    private ReservaResponseDTO convertirAResponseDTO(ReservaModel reserva) {

        return new ReservaResponseDTO(
                reserva.getId(),
                reserva.getUsuarioId(),
                reserva.getVideojuegoId(),
                reserva.getFechaReserva(),
                reserva.getFechaVencimiento(),
                reserva.getEstado()
        );
    }
}
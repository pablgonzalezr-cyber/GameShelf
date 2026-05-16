package GameShelf.ms_reserva.service;

import GameShelf.ms_reserva.client.StockClient;
import GameShelf.ms_reserva.client.UsuarioClient;
import GameShelf.ms_reserva.client.VideojuegoClient;
import GameShelf.ms_reserva.dto.ReservaRequestDTO;
import GameShelf.ms_reserva.dto.ReservaResponseDTO;
import GameShelf.ms_reserva.dto.StockResponseDTO;
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
        usuarioClient.obtenerUsuarioPorId(usuarioId);

        List<ReservaModel> reservas = reservaRepository.findByUsuarioId(usuarioId);
        List<ReservaResponseDTO> respuesta = new ArrayList<>();

        for (ReservaModel reserva : reservas) {
            respuesta.add(convertirAResponseDTO(reserva));
        }

        return respuesta;
    }

    @Override
    public List<ReservaResponseDTO> buscarReservasPorVideojuego(Long videojuegoId) {
        videojuegoClient.obtenerVideojuegoPorId(videojuegoId);

        List<ReservaModel> reservas = reservaRepository.findByVideojuegoId(videojuegoId);
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

        usuarioClient.obtenerUsuarioPorId(reservaRequestDTO.getUsuarioId());

        VideojuegoResponseDTO videojuego = videojuegoClient.obtenerVideojuegoPorId(reservaRequestDTO.getVideojuegoId());

        if (videojuego.getEstado() == null || !videojuego.getEstado().trim().equalsIgnoreCase("DISPONIBLE")) {
            throw new DatoInvalidoException("El videojuego no está disponible para reservar");
        }

        StockResponseDTO stock = stockClient.obtenerStockPorVideojuego(reservaRequestDTO.getVideojuegoId());

        if (stock.getEstado() == null || !stock.getEstado().trim().equalsIgnoreCase("ACTIVO")) {
            throw new DatoInvalidoException("El stock del videojuego no está activo");
        }

        if (stock.getCantidadDisponible() == null || stock.getCantidadDisponible() <= 0) {
            throw new DatoInvalidoException("No hay stock disponible para reservar este videojuego");
        }

        boolean existeReservaPendiente = reservaRepository.existsByUsuarioIdAndVideojuegoIdAndEstado(
                reservaRequestDTO.getUsuarioId(),
                reservaRequestDTO.getVideojuegoId(),
                "PENDIENTE"
        );

        if (existeReservaPendiente) {
            throw new DatoDuplicadoException("El usuario ya tiene una reserva pendiente para este videojuego");
        }

        if (reservaRequestDTO.getFechaVencimiento().isBefore(LocalDate.now())) {
            throw new DatoInvalidoException("La fecha de vencimiento no puede ser anterior a la fecha actual");
        }

        stockClient.reducirStock(reservaRequestDTO.getVideojuegoId());

        ReservaModel reserva = new ReservaModel();
        reserva.setUsuarioId(reservaRequestDTO.getUsuarioId());
        reserva.setVideojuegoId(reservaRequestDTO.getVideojuegoId());
        reserva.setFechaReserva(LocalDate.now());
        reserva.setFechaVencimiento(reservaRequestDTO.getFechaVencimiento());
        reserva.setEstado("PENDIENTE");

        ReservaModel reservaGuardada = reservaRepository.save(reserva);

        return convertirAResponseDTO(reservaGuardada);
    }

    @Override
    public ReservaResponseDTO actualizarReserva(Long id, ReservaRequestDTO reservaRequestDTO) {
        ReservaModel reserva = reservaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Reserva no encontrada"));

        usuarioClient.obtenerUsuarioPorId(reservaRequestDTO.getUsuarioId());
        videojuegoClient.obtenerVideojuegoPorId(reservaRequestDTO.getVideojuegoId());

        if (reservaRequestDTO.getFechaVencimiento().isBefore(LocalDate.now())) {
            throw new DatoInvalidoException("La fecha de vencimiento no puede ser anterior a la fecha actual");
        }

        reserva.setUsuarioId(reservaRequestDTO.getUsuarioId());
        reserva.setVideojuegoId(reservaRequestDTO.getVideojuegoId());
        reserva.setFechaVencimiento(reservaRequestDTO.getFechaVencimiento());

        if (reservaRequestDTO.getEstado() != null && !reservaRequestDTO.getEstado().trim().isEmpty()) {
            validarEstado(reservaRequestDTO.getEstado());
            reserva.setEstado(reservaRequestDTO.getEstado().trim().toUpperCase());
        }

        ReservaModel reservaActualizada = reservaRepository.save(reserva);

        return convertirAResponseDTO(reservaActualizada);
    }

    @Override
    public ReservaResponseDTO confirmarReserva(Long id) {
        ReservaModel reserva = reservaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Reserva no encontrada"));

        if (!reserva.getEstado().equalsIgnoreCase("PENDIENTE")) {
            throw new DatoInvalidoException("Solo se pueden confirmar reservas pendientes");
        }

        reserva.setEstado("CONFIRMADA");

        ReservaModel reservaConfirmada = reservaRepository.save(reserva);

        return convertirAResponseDTO(reservaConfirmada);
    }

    @Override
    public ReservaResponseDTO cancelarReserva(Long id) {
        ReservaModel reserva = reservaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Reserva no encontrada"));

        if (reserva.getEstado().equalsIgnoreCase("CANCELADA")) {
            throw new DatoInvalidoException("La reserva ya está cancelada");
        }

        if (reserva.getEstado().equalsIgnoreCase("PENDIENTE") ||
                reserva.getEstado().equalsIgnoreCase("CONFIRMADA")) {
            stockClient.aumentarStock(reserva.getVideojuegoId());
        }

        reserva.setEstado("CANCELADA");

        ReservaModel reservaCancelada = reservaRepository.save(reserva);

        return convertirAResponseDTO(reservaCancelada);
    }

    @Override
    public void eliminarReserva(Long id) {
        ReservaModel reserva = reservaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Reserva no encontrada"));

        if (reserva.getEstado().equalsIgnoreCase("PENDIENTE") ||
                reserva.getEstado().equalsIgnoreCase("CONFIRMADA")) {
            stockClient.aumentarStock(reserva.getVideojuegoId());
        }

        reserva.setEstado("CANCELADA");
        reservaRepository.save(reserva);
    }

    private void validarEstado(String estado) {
        String estadoLimpio = estado.trim().toUpperCase();

        if (!estadoLimpio.equals("PENDIENTE") &&
                !estadoLimpio.equals("CONFIRMADA") &&
                !estadoLimpio.equals("CANCELADA") &&
                !estadoLimpio.equals("EXPIRADA")) {
            throw new DatoInvalidoException("Estado de reserva inválido. Use PENDIENTE, CONFIRMADA, CANCELADA o EXPIRADA");
        }
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

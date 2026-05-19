package GameShelf.ms_prestamo.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import GameShelf.ms_prestamo.client.StockClient;
import GameShelf.ms_prestamo.client.UsuarioClient;
import GameShelf.ms_prestamo.client.VideoJuegoClient;
import GameShelf.ms_prestamo.dto.PrestamoRequestDTO;
import GameShelf.ms_prestamo.dto.PrestamoResponseDTO;
import GameShelf.ms_prestamo.dto.StockResponseDTO;
import GameShelf.ms_prestamo.dto.UsuarioResponseDTO;
import GameShelf.ms_prestamo.dto.VideoJuegoResponseDTO;
import GameShelf.ms_prestamo.exception.ComunicacionMicroservicioException;
import GameShelf.ms_prestamo.exception.DatoInvalidoException;
import GameShelf.ms_prestamo.exception.PrestamoNoEncontradoException;
import GameShelf.ms_prestamo.model.PrestamoModel;
import GameShelf.ms_prestamo.repository.PrestamoRepository;
import feign.FeignException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class PrestamoServiceImpl implements PrestamoService {

    private final PrestamoRepository prestamoRepository;
    private final UsuarioClient usuarioClient;
    private final VideoJuegoClient videoJuegoClient;
    private final StockClient stockClient;

    public PrestamoServiceImpl(PrestamoRepository prestamoRepository,
            UsuarioClient usuarioClient,
            VideoJuegoClient videoJuegoClient,
            StockClient stockClient) {

        this.prestamoRepository = prestamoRepository;
        this.usuarioClient = usuarioClient;
        this.videoJuegoClient = videoJuegoClient;
        this.stockClient = stockClient;
    }

    @Override
    public PrestamoResponseDTO crearPrestamo(PrestamoRequestDTO prestamoRequestDTO) {

        log.info("Creando préstamo para usuario ID: {} y videojuego ID: {}",
                prestamoRequestDTO.getUsuarioId(),
                prestamoRequestDTO.getVideojuegoId());

        validarUsuario(prestamoRequestDTO.getUsuarioId());
        validarVideojuego(prestamoRequestDTO.getVideojuegoId());
        validarStockDisponible(prestamoRequestDTO.getVideojuegoId());

        if (prestamoRepository.existsByUsuarioIdAndVideojuegoIdAndEstado(
                prestamoRequestDTO.getUsuarioId(),
                prestamoRequestDTO.getVideojuegoId(),
                "PRESTADO")) {

            throw new DatoInvalidoException("El usuario ya tiene un préstamo activo de este videojuego");
        }

        stockClient.reducirStock(prestamoRequestDTO.getVideojuegoId());

        PrestamoModel prestamo = new PrestamoModel();

        prestamo.setUsuarioId(prestamoRequestDTO.getUsuarioId());
        prestamo.setVideojuegoId(prestamoRequestDTO.getVideojuegoId());
        prestamo.setFechaPrestamo(LocalDate.now());
        prestamo.setFechaDevolucion(prestamoRequestDTO.getFechaDevolucion());
        prestamo.setEstado("PRESTADO");

        PrestamoModel prestamoGuardado = prestamoRepository.save(prestamo);

        log.info("Préstamo creado correctamente con ID: {}", prestamoGuardado.getId());

        return convertirAResponseDTO(prestamoGuardado);
    }

    @Override
    public List<PrestamoResponseDTO> listarPrestamos() {

        log.info("Listando préstamos");

        List<PrestamoModel> prestamos = prestamoRepository.findAll();
        List<PrestamoResponseDTO> respuesta = new ArrayList<>();

        for (PrestamoModel prestamo : prestamos) {
            respuesta.add(convertirAResponseDTO(prestamo));
        }

        return respuesta;
    }

    @Override
    public PrestamoResponseDTO buscarPorId(Long id) {

        log.info("Buscando préstamo ID: {}", id);

        PrestamoModel prestamo = prestamoRepository.findById(id)
                .orElseThrow(() -> new PrestamoNoEncontradoException("Préstamo no encontrado"));

        return convertirAResponseDTO(prestamo);
    }

    @Override
    public List<PrestamoResponseDTO> buscarPorUsuario(Long usuarioId) {

        log.info("Buscando préstamos por usuario ID: {}", usuarioId);

        List<PrestamoModel> prestamos = prestamoRepository.findByUsuarioId(usuarioId);
        List<PrestamoResponseDTO> respuesta = new ArrayList<>();

        for (PrestamoModel prestamo : prestamos) {
            respuesta.add(convertirAResponseDTO(prestamo));
        }

        return respuesta;
    }

    @Override
    public List<PrestamoResponseDTO> buscarPorVideojuego(Long videojuegoId) {

        log.info("Buscando préstamos por videojuego ID: {}", videojuegoId);

        List<PrestamoModel> prestamos = prestamoRepository.findByVideojuegoId(videojuegoId);
        List<PrestamoResponseDTO> respuesta = new ArrayList<>();

        for (PrestamoModel prestamo : prestamos) {
            respuesta.add(convertirAResponseDTO(prestamo));
        }

        return respuesta;
    }

    @Override
    public List<PrestamoResponseDTO> buscarPorEstado(String estado) {

        log.info("Buscando préstamos por estado: {}", estado);

        String estadoLimpio = validarEstadoPrestamo(estado);

        List<PrestamoModel> prestamos = prestamoRepository.findByEstado(estadoLimpio);
        List<PrestamoResponseDTO> respuesta = new ArrayList<>();

        for (PrestamoModel prestamo : prestamos) {
            respuesta.add(convertirAResponseDTO(prestamo));
        }

        return respuesta;
    }

    @Override
    public PrestamoResponseDTO devolverPrestamo(Long id) {

        log.info("Devolviendo préstamo ID: {}", id);

        PrestamoModel prestamo = prestamoRepository.findById(id)
                .orElseThrow(() -> new PrestamoNoEncontradoException("Préstamo no encontrado"));

        if (!prestamo.getEstado().equalsIgnoreCase("PRESTADO")) {
            throw new DatoInvalidoException("El préstamo no está activo");
        }

        stockClient.aumentarStock(prestamo.getVideojuegoId());

        prestamo.setEstado("DEVUELTO");
        prestamo.setFechaDevolucion(LocalDate.now());

        PrestamoModel prestamoActualizado = prestamoRepository.save(prestamo);

        log.info("Préstamo devuelto correctamente con ID: {}", id);

        return convertirAResponseDTO(prestamoActualizado);
    }

    @Override
    public void cancelarPrestamo(Long id) {

        log.info("Cancelando préstamo ID: {}", id);

        PrestamoModel prestamo = prestamoRepository.findById(id)
                .orElseThrow(() -> new PrestamoNoEncontradoException("Préstamo no encontrado"));

        if (prestamo.getEstado().equalsIgnoreCase("PRESTADO")) {
            stockClient.aumentarStock(prestamo.getVideojuegoId());
        }

        prestamo.setEstado("CANCELADO");
        prestamo.setFechaDevolucion(LocalDate.now());

        prestamoRepository.save(prestamo);

        log.info("Préstamo cancelado correctamente con ID: {}", id);
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

    private VideoJuegoResponseDTO validarVideojuego(Long videojuegoId) {

        if (videojuegoId == null) {
            throw new DatoInvalidoException("El videojuego es obligatorio");
        }

        try {
            log.info("Validando videojuego con ms-videojuego ID: {}", videojuegoId);

            VideoJuegoResponseDTO videojuego = videoJuegoClient.buscarVideojuegoPorId(videojuegoId);

            if (videojuego == null) {
                throw new ComunicacionMicroservicioException("El microservicio de videojuego no devolvió información");
            }

            if (videojuego.getEstado() == null || !videojuego.getEstado().equalsIgnoreCase("DISPONIBLE")) {
                throw new DatoInvalidoException("El videojuego no está disponible");
            }

            return videojuego;

        } catch (FeignException.NotFound e) {
            throw new DatoInvalidoException("El videojuego ingresado no existe");

        } catch (FeignException e) {
            throw new ComunicacionMicroservicioException("No se pudo comunicar con ms-videojuego");
        }
    }

    private StockResponseDTO validarStockDisponible(Long videojuegoId) {

        try {
            log.info("Validando stock para videojuego ID: {}", videojuegoId);

            StockResponseDTO stock = stockClient.buscarPorVideojuego(videojuegoId);

            if (stock == null) {
                throw new ComunicacionMicroservicioException("El microservicio de stock no devolvió información");
            }

            if (stock.getEstado() == null || !stock.getEstado().equalsIgnoreCase("ACTIVO")) {
                throw new DatoInvalidoException("El stock no está activo");
            }

            if (stock.getCantidadDisponible() == null || stock.getCantidadDisponible() <= 0) {
                throw new DatoInvalidoException("No hay stock disponible para este videojuego");
            }

            return stock;

        } catch (FeignException.NotFound e) {
            throw new DatoInvalidoException("No existe stock para este videojuego");

        } catch (FeignException e) {
            throw new ComunicacionMicroservicioException("No se pudo comunicar con ms-stock");
        }
    }

    private String validarEstadoPrestamo(String estado) {

        if (estado == null || estado.trim().isEmpty()) {
            throw new DatoInvalidoException("El estado es obligatorio");
        }

        String estadoLimpio = estado.trim().toUpperCase();

        if (!estadoLimpio.equals("PRESTADO")
                && !estadoLimpio.equals("DEVUELTO")
                && !estadoLimpio.equals("CANCELADO")) {

            throw new DatoInvalidoException("El estado debe ser PRESTADO, DEVUELTO o CANCELADO");
        }

        return estadoLimpio;
    }

    private PrestamoResponseDTO convertirAResponseDTO(PrestamoModel prestamo) {

        return new PrestamoResponseDTO(
                prestamo.getId(),
                prestamo.getUsuarioId(),
                prestamo.getVideojuegoId(),
                prestamo.getFechaPrestamo(),
                prestamo.getFechaDevolucion(),
                prestamo.getEstado());
    }
}

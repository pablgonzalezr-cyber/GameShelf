package GameShelf.ms_stock.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import GameShelf.ms_stock.client.VideoJuegoClient;
import GameShelf.ms_stock.dto.StockRequestDTO;
import GameShelf.ms_stock.dto.StockResponseDTO;
import GameShelf.ms_stock.dto.VideoJuegoResponseDTO;
import GameShelf.ms_stock.exception.ComunicacionVideojuegoException;
import GameShelf.ms_stock.exception.DatoDuplicadoException;
import GameShelf.ms_stock.exception.DatoInvalidoException;
import GameShelf.ms_stock.exception.StockNoEncontradoException;
import GameShelf.ms_stock.model.StockModel;
import GameShelf.ms_stock.repository.StockRepository;
import feign.FeignException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class StockServiceImpl implements StockService {

    private final StockRepository stockRepository;
    private final VideoJuegoClient videoJuegoClient;

    public StockServiceImpl(StockRepository stockRepository, VideoJuegoClient videoJuegoClient) {
        this.stockRepository = stockRepository;
        this.videoJuegoClient = videoJuegoClient;
    }

    @Override
    public StockResponseDTO crearStock(StockRequestDTO stockRequestDTO) {

        log.info("Creando stock para videojuego ID: {}", stockRequestDTO.getVideojuegoId());

        validarVideojuego(stockRequestDTO.getVideojuegoId());

        if (stockRepository.existsByVideojuegoId(stockRequestDTO.getVideojuegoId())) {
            throw new DatoDuplicadoException("Ya existe stock para este videojuego");
        }


        validarCantidades(stockRequestDTO.getCantidadTotal(), stockRequestDTO.getCantidadDisponible());

        StockModel stock = new StockModel();
        stock.setVideojuegoId(stockRequestDTO.getVideojuegoId());
        stock.setCantidadTotal(stockRequestDTO.getCantidadTotal());
        stock.setCantidadDisponible(stockRequestDTO.getCantidadDisponible());
        stock.setEstado(validarEstado(stockRequestDTO.getEstado()));

        StockModel stockGuardado = stockRepository.save(stock);

        log.info("Stock creado correctamente con ID: {}", stockGuardado.getId());

        return convertirAResponseDTO(stockGuardado);
    }

    @Override
    public List<StockResponseDTO> listarStocks() {

        log.info("Listando stocks");

        List<StockModel> stocks = stockRepository.findAll();
        List<StockResponseDTO> respuesta = new ArrayList<>();

        for (StockModel stock : stocks) {
            respuesta.add(convertirAResponseDTO(stock));
        }

        return respuesta;
    }

    @Override
    public StockResponseDTO buscarPorId(Long id) {

        log.info("Buscando stock ID: {}", id);

        StockModel stock = stockRepository.findById(id)
                .orElseThrow(() -> new StockNoEncontradoException("Stock no encontrado"));

        return convertirAResponseDTO(stock);
    }

    @Override
    public StockResponseDTO buscarPorVideojuego(Long videojuegoId) {

        log.info("Buscando stock por videojuego ID: {}", videojuegoId);

        StockModel stock = stockRepository.findByVideojuegoId(videojuegoId)
                .orElseThrow(() -> new StockNoEncontradoException("No hay stock para este videojuego"));

        return convertirAResponseDTO(stock);
    }

    @Override
    public StockResponseDTO actualizarStock(Long id, StockRequestDTO stockRequestDTO) {

        log.info("Actualizando stock ID: {}", id);

        validarVideojuego(stockRequestDTO.getVideojuegoId());
        validarCantidades(stockRequestDTO.getCantidadTotal(), stockRequestDTO.getCantidadDisponible());

        StockModel stock = stockRepository.findById(id)
                .orElseThrow(() -> new StockNoEncontradoException("Stock no encontrado"));

        if (!stock.getVideojuegoId().equals(stockRequestDTO.getVideojuegoId())
                && stockRepository.existsByVideojuegoId(stockRequestDTO.getVideojuegoId())) {
            throw new DatoDuplicadoException("Ya existe stock para este videojuego");
        }

        stock.setVideojuegoId(stockRequestDTO.getVideojuegoId());
        stock.setCantidadTotal(stockRequestDTO.getCantidadTotal());
        stock.setCantidadDisponible(stockRequestDTO.getCantidadDisponible());
        stock.setEstado(validarEstado(stockRequestDTO.getEstado()));

        StockModel stockActualizado = stockRepository.save(stock);

        log.info("Stock actualizado correctamente con ID: {}", stockActualizado.getId());

        return convertirAResponseDTO(stockActualizado);
    }

    @Override
    public StockResponseDTO reducirStock(Long videojuegoId) {

        log.info("Reduciendo stock para videojuego ID: {}", videojuegoId);

        StockModel stock = stockRepository.findByVideojuegoId(videojuegoId)
                .orElseThrow(() -> new StockNoEncontradoException("No hay stock para este videojuego"));

        if (!stock.getEstado().equalsIgnoreCase("ACTIVO")) {
            throw new DatoInvalidoException("El stock no está activo");
        }

        if (stock.getCantidadDisponible() <= 0) {
            throw new DatoInvalidoException("No hay copias disponibles para préstamo");
        }

        stock.setCantidadDisponible(stock.getCantidadDisponible() - 1);

        StockModel stockActualizado = stockRepository.save(stock);

        log.info("Stock reducido correctamente para videojuego ID: {}", videojuegoId);

        return convertirAResponseDTO(stockActualizado);
    }

    @Override
    public StockResponseDTO aumentarStock(Long videojuegoId) {

        log.info("Aumentando stock para videojuego ID: {}", videojuegoId);

        StockModel stock = stockRepository.findByVideojuegoId(videojuegoId)
                .orElseThrow(() -> new StockNoEncontradoException("No hay stock para este videojuego"));

        if (!stock.getEstado().equalsIgnoreCase("ACTIVO")) {
            throw new DatoInvalidoException("El stock no está activo");
        }

        if (stock.getCantidadDisponible() >= stock.getCantidadTotal()) {
            throw new DatoInvalidoException("La cantidad disponible no puede superar la cantidad total");
        }

        stock.setCantidadDisponible(stock.getCantidadDisponible() + 1);

        StockModel stockActualizado = stockRepository.save(stock);

        log.info("Stock aumentado correctamente para videojuego ID: {}", videojuegoId);

        return convertirAResponseDTO(stockActualizado);
    }

    @Override
    public List<StockResponseDTO> listarPorEstado(String estado) {

        log.info("Listando stock por estado: {}", estado);

        String estadoLimpio = validarEstado(estado);

        List<StockModel> stocks = stockRepository.findByEstado(estadoLimpio);
        List<StockResponseDTO> respuesta = new ArrayList<>();

        for (StockModel stock : stocks) {
            respuesta.add(convertirAResponseDTO(stock));
        }

        return respuesta;
    }

    @Override
    public void eliminarStock(Long id) {

        log.info("Desactivando stock ID: {}", id);

        StockModel stock = stockRepository.findById(id)
                .orElseThrow(() -> new StockNoEncontradoException("Stock no encontrado"));

        stock.setEstado("INACTIVO");

        stockRepository.save(stock);

        log.info("Stock desactivado correctamente con ID: {}", id);
    }

    private VideoJuegoResponseDTO validarVideojuego(Long videojuegoId) {

        if (videojuegoId == null) {
            throw new DatoInvalidoException("El videojuego es obligatorio");
        }

        try {
            log.info("Validando videojuego con ms-videojuego ID: {}", videojuegoId);

            VideoJuegoResponseDTO videojuego = videoJuegoClient.buscarVideojuegoPorId(videojuegoId);

            if (videojuego == null) {
                throw new ComunicacionVideojuegoException("El microservicio de videojuego no devolvió información");
            }

            if (videojuego.getEstado() == null || !videojuego.getEstado().trim().equalsIgnoreCase("DISPONIBLE")) {
                throw new DatoInvalidoException("El videojuego no está disponible");
            }

            log.info("Videojuego validado correctamente: {}", videojuego.getTitulo());

            return videojuego;

        } catch (FeignException.NotFound e) {
            log.warn("Videojuego no encontrado en ms-videojuego ID: {}", videojuegoId);
            throw new DatoInvalidoException("El videojuego ingresado no existe");

        } catch (FeignException e) {
            log.error("No se pudo comunicar con ms-videojuego para validar videojuego ID: {}", videojuegoId);
            throw new ComunicacionVideojuegoException("No se pudo comunicar con ms-videojuego");
        }
    }

    private void validarCantidades(Integer cantidadTotal, Integer cantidadDisponible) {

        if (cantidadTotal == null || cantidadDisponible == null) {
            throw new DatoInvalidoException("Las cantidades son obligatorias");
        }

        if (cantidadTotal < 0 || cantidadDisponible < 0) {
            throw new DatoInvalidoException("Las cantidades no pueden ser negativas");
        }

        if (cantidadDisponible > cantidadTotal) {
            throw new DatoInvalidoException("La cantidad disponible no puede ser mayor que la cantidad total");
        }
    }

    private String validarEstado(String estado) {

        if (estado == null || estado.trim().isEmpty()) {
            return "ACTIVO";
        }

        String estadoLimpio = estado.trim().toUpperCase();

        if (!estadoLimpio.equals("ACTIVO") && !estadoLimpio.equals("INACTIVO")) {
            throw new DatoInvalidoException("El estado debe ser ACTIVO o INACTIVO");
        }

        return estadoLimpio;
    }

    private StockResponseDTO convertirAResponseDTO(StockModel stock) {

        return new StockResponseDTO(
                stock.getId(),
                stock.getVideojuegoId(),
                stock.getCantidadTotal(),
                stock.getCantidadDisponible(),
                stock.getEstado());
    }

    
}

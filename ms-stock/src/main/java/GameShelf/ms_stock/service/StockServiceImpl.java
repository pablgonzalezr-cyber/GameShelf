package GameShelf.ms_stock.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import GameShelf.ms_stock.client.VideoJuegoClient;
import GameShelf.ms_stock.dto.StockRequestDTO;
import GameShelf.ms_stock.dto.StockResponseDTO;
import GameShelf.ms_stock.dto.VideoJuegoResponseDTO;
import GameShelf.ms_stock.model.StockModel;
import GameShelf.ms_stock.repository.StockRepository;
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
            throw new RuntimeException("Ya existe stock para este videojuego");
        }

        validarCantidades(stockRequestDTO.getCantidadTotal(), stockRequestDTO.getCantidadDisponible());

        StockModel stock = new StockModel();
        stock.setVideojuegoId(stockRequestDTO.getVideojuegoId());
        stock.setCantidadTotal(stockRequestDTO.getCantidadTotal());
        stock.setCantidadDisponible(stockRequestDTO.getCantidadDisponible());

        if (stockRequestDTO.getEstado() == null || stockRequestDTO.getEstado().isEmpty()) {
            stock.setEstado("ACTIVO");
        } else {
            stock.setEstado(stockRequestDTO.getEstado());
        }

        StockModel stockGuardado = stockRepository.save(stock);

        log.info("Stock creado con ID: {}", stockGuardado.getId());

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
                .orElseThrow(() -> new RuntimeException("Stock no encontrado"));

        return convertirAResponseDTO(stock);
    }

    @Override
    public StockResponseDTO buscarPorVideojuego(Long videojuegoId) {

        log.info("Buscando stock por videojuego ID: {}", videojuegoId);

        StockModel stock = stockRepository.findByVideojuegoId(videojuegoId)
                .orElseThrow(() -> new RuntimeException("No hay stock para este videojuego"));

        return convertirAResponseDTO(stock);
    }

    @Override
    public StockResponseDTO actualizarStock(Long id, StockRequestDTO stockRequestDTO) {

        log.info("Actualizando stock ID: {}", id);

        validarVideojuego(stockRequestDTO.getVideojuegoId());
        validarCantidades(stockRequestDTO.getCantidadTotal(), stockRequestDTO.getCantidadDisponible());

        StockModel stock = stockRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Stock no encontrado"));

        stock.setVideojuegoId(stockRequestDTO.getVideojuegoId());
        stock.setCantidadTotal(stockRequestDTO.getCantidadTotal());
        stock.setCantidadDisponible(stockRequestDTO.getCantidadDisponible());

        if (stockRequestDTO.getEstado() != null && !stockRequestDTO.getEstado().isEmpty()) {
            stock.setEstado(stockRequestDTO.getEstado());
        }

        StockModel stockActualizado = stockRepository.save(stock);

        log.info("Stock actualizado con ID: {}", stockActualizado.getId());

        return convertirAResponseDTO(stockActualizado);
    }

    @Override
    public StockResponseDTO reducirStock(Long videojuegoId) {

        log.info("Reduciendo stock para videojuego ID: {}", videojuegoId);

        StockModel stock = stockRepository.findByVideojuegoId(videojuegoId)
                .orElseThrow(() -> new RuntimeException("No hay stock para este videojuego"));

        if (!stock.getEstado().equalsIgnoreCase("ACTIVO")) {
            throw new RuntimeException("El stock no está activo");
        }

        if (stock.getCantidadDisponible() <= 0) {
            throw new RuntimeException("No hay copias disponibles para préstamo");
        }

        stock.setCantidadDisponible(stock.getCantidadDisponible() - 1);

        StockModel stockActualizado = stockRepository.save(stock);

        log.info("Stock reducido para videojuego ID: {}", videojuegoId);

        return convertirAResponseDTO(stockActualizado);
    }

    @Override
    public StockResponseDTO aumentarStock(Long videojuegoId) {

        log.info("Aumentando stock para videojuego ID: {}", videojuegoId);

        StockModel stock = stockRepository.findByVideojuegoId(videojuegoId)
                .orElseThrow(() -> new RuntimeException("No hay stock para este videojuego"));

        if (stock.getCantidadDisponible() >= stock.getCantidadTotal()) {
            throw new RuntimeException("La cantidad disponible no puede superar la cantidad total");
        }

        stock.setCantidadDisponible(stock.getCantidadDisponible() + 1);

        StockModel stockActualizado = stockRepository.save(stock);

        log.info("Stock aumentado para videojuego ID: {}", videojuegoId);

        return convertirAResponseDTO(stockActualizado);
    }

    @Override
    public List<StockResponseDTO> listarPorEstado(String estado) {

        log.info("Listando stock por estado: {}", estado);

        List<StockModel> stocks = stockRepository.findByEstado(estado);
        List<StockResponseDTO> respuesta = new ArrayList<>();

        for (StockModel stock : stocks) {
            respuesta.add(convertirAResponseDTO(stock));
        }

        return respuesta;
    }

    private void validarVideojuego(Long videojuegoId) {

        if (videojuegoId == null) {
            throw new RuntimeException("El videojuego es obligatorio");
        }

        VideoJuegoResponseDTO videojuego = videoJuegoClient.buscarVideojuegoPorId(videojuegoId);

        if (!videojuego.getEstado().equalsIgnoreCase("DISPONIBLE")) {
            throw new RuntimeException("El videojuego no está disponible");
        }
    }

    private void validarCantidades(Integer cantidadTotal, Integer cantidadDisponible) {

        if (cantidadDisponible > cantidadTotal) {
            throw new RuntimeException("La cantidad disponible no puede ser mayor que la cantidad total");
        }
    }

    private StockResponseDTO convertirAResponseDTO(StockModel stock) {
        return new StockResponseDTO(
                stock.getId(),
                stock.getVideojuegoId(),
                stock.getCantidadTotal(),
                stock.getCantidadDisponible(),
                stock.getEstado()
        );
    }
}
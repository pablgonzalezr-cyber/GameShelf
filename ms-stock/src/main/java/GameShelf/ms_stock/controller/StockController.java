package GameShelf.ms_stock.controller;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import GameShelf.ms_stock.dto.StockRequestDTO;
import GameShelf.ms_stock.dto.StockResponseDTO;
import GameShelf.ms_stock.service.StockService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController 
@RequestMapping("/api/stocks")
public class StockController {

    private final StockService stockService;

    public StockController(StockService stockService) {
        this.stockService = stockService;
    }

    @PostMapping
    public ResponseEntity<StockResponseDTO> crearStock(@Valid @RequestBody StockRequestDTO stockRequestDTO) {

        log.info("Petición POST para crear stock");

        StockResponseDTO stockCreado = stockService.crearStock(stockRequestDTO);

        return new ResponseEntity<>(stockCreado, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<StockResponseDTO>> listarStocks() {

        log.info("Petición GET para listar stocks");

        return ResponseEntity.ok(stockService.listarStocks());
    }

    @GetMapping("/{id}")
    public ResponseEntity<StockResponseDTO> buscarStock(@PathVariable Long id) {

        log.info("Petición GET para buscar stock ID: {}", id);

        return ResponseEntity.ok(stockService.buscarPorId(id));
    }

    @GetMapping("/videojuego/{videojuegoId}")
    public ResponseEntity<StockResponseDTO> buscarPorVideojuego(@PathVariable Long videojuegoId) {

        log.info("Petición GET para buscar stock por videojuego ID: {}", videojuegoId);

        return ResponseEntity.ok(stockService.buscarPorVideojuego(videojuegoId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<StockResponseDTO> actualizarStock(
            @PathVariable Long id,
            @Valid @RequestBody StockRequestDTO stockRequestDTO) {

        log.info("Petición PUT para actualizar stock ID: {}", id);

        return ResponseEntity.ok(stockService.actualizarStock(id, stockRequestDTO));
    }

    @PutMapping("/reducir/{videojuegoId}")
    public ResponseEntity<StockResponseDTO> reducirStock(@PathVariable Long videojuegoId) {

        log.info("Petición PUT para reducir stock del videojuego ID: {}", videojuegoId);

        return ResponseEntity.ok(stockService.reducirStock(videojuegoId));
    }

    @PutMapping("/aumentar/{videojuegoId}")
    public ResponseEntity<StockResponseDTO> aumentarStock(@PathVariable Long videojuegoId) {

        log.info("Petición PUT para aumentar stock del videojuego ID: {}", videojuegoId);

        return ResponseEntity.ok(stockService.aumentarStock(videojuegoId));
    }

    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<StockResponseDTO>> listarPorEstado(@PathVariable String estado) {

        log.info("Petición GET para listar stock por estado: {}", estado);

        return ResponseEntity.ok(stockService.listarPorEstado(estado));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminarStock(@PathVariable Long id) {

        log.info("Petición DELETE para desactivar stock ID: {}", id);

        stockService.eliminarStock(id);

        return ResponseEntity.ok("Stock desactivado correctamente");
    }
}
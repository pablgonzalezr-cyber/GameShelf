package GameShelf.ms_stock.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import GameShelf.ms_stock.model.StockModel;
import GameShelf.ms_stock.service.StockService;

@RestController
@RequestMapping("/api/stock")
public class StockController {
    
    @Autowired
    private StockService stockService;

    @PostMapping
    public ResponseEntity<StockModel> crear(@RequestBody StockModel stock) {
        return new ResponseEntity<>(stockService.guardar(stock), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<StockModel>> listar() {
        return ResponseEntity.ok(stockService.listarTodo());
    }

    @GetMapping("/videojuego/{id}")
    public ResponseEntity<StockModel> obtenerPorVideojuego(@PathVariable Long id) {
        return ResponseEntity.ok(stockService.buscarPorVideojuego(id));
    }

    // Endpoint especial para procesos de préstamo
    @PutMapping("/reducir/{videojuegoId}")
    public ResponseEntity<StockModel> reducir(@PathVariable Long videojuegoId) {
        return ResponseEntity.ok(stockService.reducirStock(videojuegoId));
    }
}

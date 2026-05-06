package GameShelf.ms_stock.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import GameShelf.ms_stock.model.StockModel;
import GameShelf.ms_stock.repository.StockRepository;

@Service
public class StockService {
    @Autowired
    private StockRepository stockRepository;

    public StockModel guardar(StockModel stock) {
        // Si ya existe stock para ese videojuego, podríamos lanzar error o actualizar
        if(stockRepository.findByVideojuegoId(stock.getVideojuegoId()).isPresent()){
            throw new RuntimeException("Ya existe un registro de stock para este videojuego.");
        }
        return stockRepository.save(stock);
    }

    public List<StockModel> listarTodo() {
        return stockRepository.findAll();
    }

    public StockModel buscarPorVideojuego(Long videojuegoId) {
        return stockRepository.findByVideojuegoId(videojuegoId)
                .orElseThrow(() -> new RuntimeException("No hay registro de stock para el videojuego: " + videojuegoId));
    }

    // Método para cuando se presta un juego 
    public StockModel reducirStock(Long videojuegoId) {
        StockModel stock = buscarPorVideojuego(videojuegoId);
        if (stock.getCantidadDisponible() <= 0) {
            throw new RuntimeException("No hay copias disponibles para préstamo.");
        }
        stock.setCantidadDisponible(stock.getCantidadDisponible() - 1);
        return stockRepository.save(stock);
    }

}

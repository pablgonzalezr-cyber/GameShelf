package GameShelf.ms_reserva.client;

import GameShelf.ms_reserva.dto.StockResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

@FeignClient(name = "ms-stock")
public interface StockClient {

    @GetMapping("/api/stocks/videojuego/{videojuegoId}")
    StockResponseDTO obtenerStockPorVideojuego(@PathVariable("videojuegoId") Long videojuegoId);

    @PutMapping("/api/stocks/reducir/{videojuegoId}")
    StockResponseDTO reducirStock(@PathVariable("videojuegoId") Long videojuegoId);

    @PutMapping("/api/stocks/aumentar/{videojuegoId}")
    StockResponseDTO aumentarStock(@PathVariable("videojuegoId") Long videojuegoId);
}

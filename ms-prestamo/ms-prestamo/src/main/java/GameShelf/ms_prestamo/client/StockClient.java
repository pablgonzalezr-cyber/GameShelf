package GameShelf.ms_prestamo.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

import GameShelf.ms_prestamo.dto.StockResponseDTO;

@FeignClient(name = "ms-stock")
public interface StockClient {

    @GetMapping("/api/stocks/videojuego/{videojuegoId}")
    StockResponseDTO buscarPorVideojuego(@PathVariable Long videojuegoId);

    @PutMapping("/api/stocks/reducir/{videojuegoId}")
    StockResponseDTO reducirStock(@PathVariable Long videojuegoId);

    @PutMapping("/api/stocks/aumentar/{videojuegoId}")
    StockResponseDTO aumentarStock(@PathVariable Long videojuegoId);
}

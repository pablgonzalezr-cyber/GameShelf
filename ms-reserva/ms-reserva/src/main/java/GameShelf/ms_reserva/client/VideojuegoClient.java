package GameShelf.ms_reserva.client;

import GameShelf.ms_reserva.dto.VideojuegoResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "ms-videojuego")
public interface VideojuegoClient {

    @GetMapping("/api/videojuegos/{id}")
    VideojuegoResponseDTO obtenerVideojuegoPorId(@PathVariable("id") Long id);
}

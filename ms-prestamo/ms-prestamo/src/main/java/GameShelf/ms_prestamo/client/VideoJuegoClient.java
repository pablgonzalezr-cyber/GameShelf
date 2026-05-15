package GameShelf.ms_prestamo.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import GameShelf.ms_prestamo.dto.VideoJuegoResponseDTO;

@FeignClient(name = "ms-videojuego")
public interface VideoJuegoClient {

    @GetMapping("/api/videojuegos/{id}")
    VideoJuegoResponseDTO buscarVideojuegoPorId(@PathVariable Long id);
}

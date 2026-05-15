package GameShelf.ms_videojuego.client;

import GameShelf.ms_videojuego.dto.CategoriaResponseDTO;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "ms-categoria")
public interface CategoriaClient {

    @GetMapping("/api/categorias/{id}")
    CategoriaResponseDTO buscarCategoriaPorId(@PathVariable Long id);
}

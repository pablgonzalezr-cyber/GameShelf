package GameShelf.ms_multa.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import GameShelf.ms_multa.dto.PrestamoResponseDTO;

@FeignClient(name = "ms-prestamo")
public interface PrestamoClient {

    @GetMapping("/api/prestamos/{id}")
    PrestamoResponseDTO buscarPrestamoPorId(@PathVariable Long id);
}
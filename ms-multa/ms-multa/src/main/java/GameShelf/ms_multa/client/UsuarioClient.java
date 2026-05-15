package GameShelf.ms_multa.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import GameShelf.ms_multa.dto.UsuarioResponseDTO;

@FeignClient(name = "ms-usuario")
public interface UsuarioClient {

    @GetMapping("/api/usuarios/{id}")
    UsuarioResponseDTO buscarUsuarioPorId(@PathVariable Long id);
}
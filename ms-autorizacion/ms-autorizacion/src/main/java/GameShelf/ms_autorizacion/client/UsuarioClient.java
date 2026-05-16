package GameShelf.ms_autorizacion.client;

import GameShelf.ms_autorizacion.dto.UsuarioResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "ms-usuario")
public interface UsuarioClient {

    @GetMapping("/api/usuarios/{id}")
    UsuarioResponseDTO obtenerUsuarioPorId(@PathVariable Long id);
}
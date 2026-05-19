package GameShelf.ms_usuario.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import GameShelf.ms_usuario.dto.RolResponseDTO;

@FeignClient(name = "ms-roles")
public interface RolClient {

    @GetMapping("/api/roles/nombre/{nombre}")
    RolResponseDTO buscarRolPorNombre(@PathVariable String nombre);
    

}
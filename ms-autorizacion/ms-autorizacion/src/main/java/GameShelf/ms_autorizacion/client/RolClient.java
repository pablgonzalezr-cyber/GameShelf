package GameShelf.ms_autorizacion.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "ms-roles")
public interface RolClient {

    @GetMapping("/api/roles/validar/{nombre}")
    Boolean validarRol(@PathVariable String nombre);
}
package GameShelf.ms_prestamo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI prestamoOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("GameShelf - Microservicio de Préstamos")
                        .version("1.0.0")
                        .description("Documentación Swagger/OpenAPI del microservicio de préstamos. "
                                + "Permite crear, listar, buscar, devolver, cancelar y renovar préstamos de videojuegos."));
    }
}
package GameShelf.ms_stock.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI stockOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("GameShelf - Microservicio de Stock")
                        .version("1.0.0")
                        .description("Documentación Swagger/OpenAPI del microservicio de stock. "
                                + "Permite gestionar unidades disponibles, validar disponibilidad, aumentar y disminuir stock de videojuegos."));
    }
}
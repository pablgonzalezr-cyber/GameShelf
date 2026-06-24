package GameShelf.ms_videojuego.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI msVideojuegoOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API ms-videojuego")
                        .version("1.0")
                        .description("Microservicio encargado de gestionar el catálogo de videojuegos, sus categorías, plataformas, precios y estados dentro de GameShelf."));
    }
}
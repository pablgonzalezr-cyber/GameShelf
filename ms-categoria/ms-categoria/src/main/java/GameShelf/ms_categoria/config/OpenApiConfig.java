package GameShelf.ms_categoria.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI msCategoriaOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API ms-categoria")
                        .version("1.0")
                        .description("Microservicio encargado de gestionar categorías de videojuegos, sus descripciones y estados dentro de GameShelf."));
    }
}

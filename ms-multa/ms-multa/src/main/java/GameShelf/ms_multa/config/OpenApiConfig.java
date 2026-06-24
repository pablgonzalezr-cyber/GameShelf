package GameShelf.ms_multa.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI multaOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("GameShelf - Microservicio de Multas")
                        .version("1.0.0")
                        .description("Documentación Swagger/OpenAPI del microservicio de multas. "
                                + "Permite crear, listar, actualizar, anular y pagar multas asociadas a préstamos de videojuegos."));
    }
}
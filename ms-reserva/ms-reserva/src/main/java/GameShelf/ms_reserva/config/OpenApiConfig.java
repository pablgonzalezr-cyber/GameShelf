package GameShelf.ms_reserva.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI reservaOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("GameShelf - Microservicio de Reservas")
                        .version("1.0.0")
                        .description("Documentación Swagger/OpenAPI del microservicio de reservas. "
                                + "Permite crear, listar, confirmar, cancelar y consultar el historial de reservas de videojuegos."));
    }
}
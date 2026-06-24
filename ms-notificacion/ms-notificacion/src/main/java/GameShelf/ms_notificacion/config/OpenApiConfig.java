package GameShelf.ms_notificacion.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI msNotificacionOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API ms-notificacion")
                        .version("1.0")
                        .description("Microservicio encargado de gestionar notificaciones de usuarios, estados, tipos y referencias asociadas dentro de GameShelf."));
    }
}
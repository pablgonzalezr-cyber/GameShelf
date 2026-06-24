package GameShelf.ms_autorizacion.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI msAutorizacionOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API ms-autorizacion")
                        .version("1.0")
                        .description("Microservicio encargado de gestionar autorizaciones, módulos y permisos de usuarios en GameShelf."));
    }
}
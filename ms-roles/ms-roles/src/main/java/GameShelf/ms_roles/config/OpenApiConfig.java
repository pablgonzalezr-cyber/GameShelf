package GameShelf.ms_roles.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI msRolesOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API ms-roles")
                        .version("1.0")
                        .description("Microservicio encargado de gestionar roles, estados y validación de roles activos dentro de GameShelf."));
    }
}

package GameShelf.ms_usuario.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI msUsuarioOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API ms-usuario")
                        .version("1.0")
                        .description("Microservicio encargado de gestionar usuarios, credenciales, correos y roles asociados dentro de GameShelf."));
    }
}

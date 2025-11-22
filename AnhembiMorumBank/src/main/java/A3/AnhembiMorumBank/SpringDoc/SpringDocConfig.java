package A3.AnhembiMorumBank.SpringDoc;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;


@Configuration
public class SpringDocConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .components(new Components()
                        .addSecuritySchemes("bearer-key",
                                new SecurityScheme().type(SecurityScheme.Type.HTTP).scheme("bearer").bearerFormat("JWT")))
                .info(new Info()
                        .title("AnhembiMorumBank")
                        .description("API Rest da aplicação AnhembiMorumBank, contendo as funcionalidades de CRUD.")
                        .contact(new Contact()
                                .name("Time Backend")
                                .email("diego.balieiro7@gmail.com")))
                .externalDocs(new ExternalDocumentation()
                        .url("https://github.com/DiegoBalieiroDev/A3--AnhembiMorumBank"));
    }


}

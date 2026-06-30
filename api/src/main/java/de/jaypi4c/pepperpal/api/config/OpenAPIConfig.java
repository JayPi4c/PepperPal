package de.jaypi4c.pepperpal.api.config;

import de.jaypi4c.pepperpal.api.autoconfigure.OpenAPIProperties;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * @see <a href="http://localhost:8080/swagger-ui/index.html">http://localhost:8080/swagger-ui/index.html</a>
 */
@Configuration
@RequiredArgsConstructor
public class OpenAPIConfig {

    private final OpenAPIProperties openAPIProperties;

    @Bean
    public OpenAPI myOpenAPI() {
        // https://www.bezkoder.com/spring-boot-swagger-3/
        Server devServer = new Server();
        devServer.setUrl(openAPIProperties.getDevUrl());
        devServer.setDescription("Server URL in Development environment");

        Server prodServer = new Server();
        prodServer.setUrl(openAPIProperties.getProdUrl());
        prodServer.setDescription("Server URL in Production environment");


        Contact contact = new Contact();
        contact.setEmail(openAPIProperties.getContact().getEmail());
        contact.setName(openAPIProperties.getContact().getName());
        contact.setUrl(openAPIProperties.getContact().getUrl());

        License mitLicense = new License().name("MIT License").url("https://choosealicense.com/licenses/mit/");

        Info info = new Info()
                .title("SoilData RET API")
                .version("v1")
                .contact(contact)
                .description("This API exposes endpoints to manage soil data.")
                // .termsOfService("https://www.bezkoder.com/terms")
                .license(mitLicense);

        return new OpenAPI().info(info).servers(List.of(devServer, prodServer));
    }
}
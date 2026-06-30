package de.jaypi4c.pepperpal.api.autoconfigure;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "pepperpal.api.openapi")
public class OpenAPIProperties {

    private String devUrl;
    private String prodUrl;

    private Contact contact = new Contact();

    @Data
    public static class Contact {
        private String email = "info@jaypi4c.de";
        private String name = "JayPi4c";
        private String url = "https://github.com/JayPi4c";
    }

}

package de.jaypi4c.pepperpal.dashboard.autoconfigure;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "pepperpal.dashboard.backend")
public class BackendProperties {

    private String baseUrl = "http://localhost:8080";

}

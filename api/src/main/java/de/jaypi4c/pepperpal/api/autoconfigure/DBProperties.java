package de.jaypi4c.pepperpal.api.autoconfigure;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "pepperpal.db")
public class DBProperties {
    private String url;
    private String driverClassName;
}

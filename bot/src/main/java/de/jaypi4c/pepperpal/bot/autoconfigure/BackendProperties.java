package de.jaypi4c.pepperpal.bot.autoconfigure;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "pepperpal.bot.backend")
public class BackendProperties {

    private String baseUrl = "http://localhost";
    private String port = "8080";
    private int gapInMinutes = 30;
    private int minWaterlevel = 60;

}

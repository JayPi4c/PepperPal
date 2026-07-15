package de.jaypi4c.pepperpal.bot.autoconfigure;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "pepperpal.bot.service")
public class ServiceProperties {

    private int gapInMinutes = 30;

    private int minWaterlevel = 60;
    private int maxTemperature = 25;

}

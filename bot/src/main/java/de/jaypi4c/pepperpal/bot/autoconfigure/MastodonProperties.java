package de.jaypi4c.pepperpal.bot.autoconfigure;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "pepperpal.bot.mastodon")
public class MastodonProperties {

    private String receiver;
    private String baseUrl = "mastodon.social";
    private String accessToken;

}

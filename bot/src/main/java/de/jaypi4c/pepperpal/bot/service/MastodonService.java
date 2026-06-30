package de.jaypi4c.pepperpal.bot.service;

import de.jaypi4c.pepperpal.bot.autoconfigure.BackendProperties;
import de.jaypi4c.pepperpal.bot.autoconfigure.MastodonProperties;
import de.jaypi4c.pepperpal.bot.model.SoilData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class MastodonService {

    private final RestTemplate restTemplate;
    private final MyMastodonClient mastodonClient;
    private final SimpleDateFormat sdf;

    private final MastodonProperties mastodonProperties;
    private final BackendProperties backendProperties;

    private boolean dry = false;
    private boolean gapFound = false;


    public MastodonService(RestTemplate restTemplate, MyMastodonClient mastodonClient, MastodonProperties mastodonProperties, BackendProperties backendProperties) {
        this.restTemplate = restTemplate;
        this.mastodonClient = mastodonClient;
        this.mastodonProperties = mastodonProperties;
        this.backendProperties = backendProperties;
        this.sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SSS");
    }

    /**
     *
     */
    // @Scheduled(cron = "0 0 0 * * *")
    @Scheduled(fixedRate = 15, timeUnit = TimeUnit.MINUTES)
    public void checkAndNotify() {
        log.info("Checking for new data");
        String latestUrl = MessageFormat.format("{0}:{1}/chili-app/v1/soilData/latest", backendProperties.getBaseUrl(), backendProperties.getPort());
        Optional<SoilData> latest = Optional.ofNullable(restTemplate.getForObject(latestUrl, SoilData.class));

        if (latest.isPresent()) {
            log.info("Latest data: {}", latest.get());

            if (isGapTooBig(LocalDateTime.now(), latest.get().getCreated(), backendProperties.getGapInMinutes())) {
                if (!gapFound) {
                    log.info("Gap is bigger than {} minutes", backendProperties.getGapInMinutes());
                    gapFound = true;

                    final String message = "Last data was from {0}! Please check the sensor!";
                    final String statusText = MessageFormat.format(message, sdf.format(latest.get().getCreated()));
                    mastodonClient.sendMessage(statusText, mastodonProperties.getReceiver());
                }
                return; // don't do further check, data is too old
            } else {
                if (gapFound) {
                    mastodonClient.sendMessage("Retrieved new Sensor data!", mastodonProperties.getReceiver());
                    gapFound = false;
                }
            }
            if (latest.get().getMoistureLevel() < backendProperties.getMinWaterlevel()) {
                if (!dry) {
                    dry = true;
                    final String message = "Water level is below {0}! Please water the chili!";
                    final String statusText = MessageFormat.format(message, backendProperties.getMinWaterlevel());
                    mastodonClient.sendMessage(statusText, mastodonProperties.getReceiver());
                }
            } else {
                if (dry) {
                    dry = false;
                    mastodonClient.sendMessage("Water level is back to normal!", mastodonProperties.getReceiver());
                }
            }
        } else {
            log.info("No data found");
        }
    }

    private boolean isGapTooBig(LocalDateTime now, LocalDateTime latest, int gapInMinutes) {
        int allowedGap = gapInMinutes * 60 * 1000;
        return ChronoUnit.MILLIS.between(now, latest) > allowedGap;
    }
}

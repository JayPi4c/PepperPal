package de.jaypi4c.pepperpal.bot.service;

import de.jaypi4c.pepperpal.bot.autoconfigure.BackendProperties;
import de.jaypi4c.pepperpal.bot.autoconfigure.MastodonProperties;
import de.jaypi4c.pepperpal.bot.autoconfigure.ServiceProperties;
import de.jaypi4c.pepperpal.bot.model.SoilData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class MastodonService {

    private final RestTemplate restTemplate;
    private final MyMastodonClient mastodonClient;
    private final SimpleDateFormat sdf;

    private final MastodonProperties mastodonProperties;
    private final ServiceProperties serviceProperties;
    private final String backendUrl;

    public MastodonService(RestTemplate restTemplate, MyMastodonClient mastodonClient, MastodonProperties mastodonProperties, BackendProperties backendProperties, ServiceProperties serviceProperties) {
        this.restTemplate = restTemplate;
        this.mastodonClient = mastodonClient;
        this.mastodonProperties = mastodonProperties;
        this.serviceProperties = serviceProperties;
        this.sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SSS");
        this.backendUrl = MessageFormat.format("{0}:{1}/pepperpal/v1/soilData/latest", backendProperties.getBaseUrl(), backendProperties.getPort());
    }

    private boolean dataTooOld = false;

    @Scheduled(fixedRate = 15, timeUnit = TimeUnit.MINUTES)
    public void checkAndNotify() {
        log.info("Checking for new data");

        SoilData latestData = restTemplate.getForObject(backendUrl, SoilData.class);

        if (latestData == null) {
            log.warn("Failed to fetch latest data entry. Are the configurations correct?");
            return;
        }

        if (isDataTooOld(latestData)) {
            // only toot if user has not yet been told about it
            if (!dataTooOld) {
                dataTooOld = true;
                final String message = "Last data was from {0}! Please check the sensor!";
                final String statusText = MessageFormat.format(message, sdf.format(latestData.getCreated()));
                mastodonClient.sendMessage(statusText, mastodonProperties.getReceiver());
            }
        } else {
            // the latestData is within our expected timeframe, probably everything goes as planned
            dataTooOld = false;

            // go on with data processing and informing as desired
            handleTemperature(latestData);

            handleWaterlevel(latestData);
        }
    }

    private boolean isDataTooOld(SoilData data) {
        int allowedGap = serviceProperties.getGapInMinutes() * 60 * 1000;
        return ChronoUnit.MILLIS.between(LocalDateTime.now(), data.getCreated()) > allowedGap;
    }

    private boolean tooHot = false;

    private void handleTemperature(SoilData data) {
        float temperature = data.getTemperature();
        if (temperature >= serviceProperties.getMaxTemperature() && !tooHot) {
            // we exceed the max temperature for the first time
            tooHot = true;
            final String message = "Temperature exceeds {0}! Currently at {1}!";
            final String statusText = MessageFormat.format(message, serviceProperties.getMaxTemperature(), temperature);
            mastodonClient.sendMessage(statusText, mastodonProperties.getReceiver());
        } else if (temperature < serviceProperties.getMaxTemperature() && tooHot) {
            // we go below the limit for the first time
            tooHot = false;
            final String message = "Temperature below {0}! Currently at {1}!";
            final String statusText = MessageFormat.format(message, serviceProperties.getMaxTemperature(), temperature);
            mastodonClient.sendMessage(statusText, mastodonProperties.getReceiver());
        }
    }

    private boolean waterTooHigh = false;

    private void handleWaterlevel(SoilData data) {
        float waterlevel = data.getMoistureLevel();
        if (waterlevel >= serviceProperties.getMinWaterlevel() && !waterTooHigh) {
            // we exceed the max waterlevel for the first time
            waterTooHigh = true;
            mastodonClient.sendMessage("Waterlevel exceeds " + serviceProperties.getMinWaterlevel(), mastodonProperties.getReceiver());
        } else if (waterlevel < serviceProperties.getMinWaterlevel() && waterTooHigh) {
            // we go below the limit for the first time
            waterTooHigh = false;
            mastodonClient.sendMessage("Waterlevel below " + serviceProperties.getMinWaterlevel(), mastodonProperties.getReceiver());
        }
    }
}

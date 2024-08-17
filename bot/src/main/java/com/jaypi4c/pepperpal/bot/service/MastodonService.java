package com.jaypi4c.pepperpal.bot.service;

import com.jaypi4c.pepperpal.bot.model.SoilData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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
    @Value("${jaypi4c.chili-app.base-url}")
    private String baseUrl;
    @Value("${jaypi4c.chili-app.port}")
    private String port;
    @Value("${jaypi4c.mastodon.receiver}")
    private String receiver;
    @Value("${jaypi4c.chili-app.gap-in-minutes}")
    private int gapInMinutes;
    @Value("${jaypi4c.chili-app.min-waterlevel}")
    private int minMoistureLevel;
    private boolean dry = false;
    private boolean gapFound = false;


    public MastodonService(RestTemplate restTemplate, MyMastodonClient mastodonClient) {
        this.restTemplate = restTemplate;
        this.mastodonClient = mastodonClient;
        this.sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SSS");
    }

    /**
     *
     */
    // @Scheduled(cron = "0 0 0 * * *")
    @Scheduled(fixedRate = 15, timeUnit = TimeUnit.MINUTES)
    public void checkAndNotify() {
        log.info("Checking for new data");
        String latestUrl = MessageFormat.format("{0}:{1}/chili-app/v1/soilData/latest", baseUrl, port);
        Optional<SoilData> latest = Optional.ofNullable(restTemplate.getForObject(latestUrl, SoilData.class));

        if (latest.isPresent()) {
            log.info("Latest data: {}", latest.get());

            if (isGapTooBig(LocalDateTime.now(), latest.get().getCreated(), gapInMinutes)) {
                if (!gapFound) {
                    log.info("Gap is bigger than {} minutes", gapInMinutes);
                    gapFound = true;

                    final String message = "Last data was from {0}! Please check the sensor!";
                    final String statusText = MessageFormat.format(message, sdf.format(latest.get().getCreated()));
                    mastodonClient.sendMessage(statusText, receiver);
                }
                return; // don't do further check, data is too old
            } else {
                if (gapFound) {
                    mastodonClient.sendMessage("Retrieved new Sensor data!", receiver);
                    gapFound = false;
                }
            }
            if (latest.get().getMoistureLevel() < minMoistureLevel) {
                if (!dry) {
                    dry = true;
                    final String message = "Water level is below {0}! Please water the chili!";
                    final String statusText = MessageFormat.format(message, minMoistureLevel);
                    mastodonClient.sendMessage(statusText, receiver);
                }
            } else {
                if (dry) {
                    dry = false;
                    mastodonClient.sendMessage("Water level is back to normal!", receiver);
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

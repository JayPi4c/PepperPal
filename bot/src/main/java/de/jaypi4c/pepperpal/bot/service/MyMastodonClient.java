package de.jaypi4c.pepperpal.bot.service;

import de.jaypi4c.pepperpal.bot.autoconfigure.MastodonProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import social.bigbone.MastodonClient;
import social.bigbone.api.entity.data.Visibility;
import social.bigbone.api.exception.BigBoneRequestException;

import java.text.MessageFormat;
import java.util.Collections;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class MyMastodonClient {

    private final MastodonProperties mastodonProperties;

    public void sendMessage(String message, String receiver) {
        sendMessage(message, receiver, Visibility.DIRECT, null, Collections.emptyList(), false, null, "en");
    }

    public void sendMessage(String message, String receiver, Visibility visibility, String inReplyToId, List<String> mediaIds, boolean sensitive, String spoilerText, String language) {
        final String statusText = MessageFormat.format("{0} @{1}", message, receiver);

        // https://github.com/andregasser/bigbone/blob/master/sample-java/src/main/java/social/bigbone/sample/PostStatusWithMediaAttached.java
        final MastodonClient client = new MastodonClient
                .Builder(mastodonProperties.getBaseUrl())
                .accessToken(mastodonProperties.getAccessToken())
                .build();
        try {
            client.statuses().postStatus(statusText, mediaIds, visibility, inReplyToId, sensitive, spoilerText, language).execute();
            log.info("successfully posted status");
        } catch (BigBoneRequestException e) {
            log.error("Error posting status! ErrorCode: {} | {}", e.getHttpStatusCode(), e.getErrorDetails(), e);
        }
    }

}

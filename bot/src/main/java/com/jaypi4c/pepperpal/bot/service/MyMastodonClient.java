package com.jaypi4c.pepperpal.bot.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import social.bigbone.MastodonClient;
import social.bigbone.api.entity.data.Visibility;
import social.bigbone.api.exception.BigBoneRequestException;

import java.text.MessageFormat;
import java.util.Collections;
import java.util.List;

@Slf4j
@Component
public class MyMastodonClient {

    @Value("${jaypi4c.mastodon.baseUrl}")
    private String baseUrl;

    @Value("${jaypi4c.mastodon.accessToken}")
    private String accessToken;

    public void sendMessage(String message, String receiver) {
        sendMessage(message, receiver, Visibility.DIRECT, null, Collections.emptyList(), false, null, "en");
    }

    public void sendMessage(String message, String receiver, Visibility visibility, String inReplyToId, List<String> mediaIds, boolean sensitive, String spoilerText, String language) {
        final String statusText = MessageFormat.format("{0} @{1}", message, receiver);

        // https://github.com/andregasser/bigbone/blob/master/sample-java/src/main/java/social/bigbone/sample/PostStatusWithMediaAttached.java
        final MastodonClient client = new MastodonClient.Builder(baseUrl).accessToken(accessToken).build();
        try {
            client.statuses().postStatus(statusText, mediaIds, visibility, inReplyToId, sensitive, spoilerText, language).execute();
            log.info("successfully posted status");
        } catch (BigBoneRequestException e) {
            log.error("Error posting status", e);
        }
    }

}

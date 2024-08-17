package com.jaypi4c.mastodonbot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class MastodonBotApplication {

	public static void main(String[] args) {
		SpringApplication.run(MastodonBotApplication.class, args);
	}

}

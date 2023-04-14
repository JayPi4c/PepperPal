# Mastodon Bot

## Setup

In order to get the Bot started an AccessToken must be obtained from Mastodon and stored as an environment variable
with the name `MASTODON_ACCESS_TOKEN`.

This can be done by adding the following line to your `/etc/environment` file:

```
MASTODON_ACCESS_TOKEN=YOUR-MASTODON-ACCESS-TOKEN
```

After logging out and back in again the environment variable should be available.

Alternatively, you can set the value in another profile file like `application-dev.properties` and use
the `--spring.profiles.active=dev` flag when starting the application.
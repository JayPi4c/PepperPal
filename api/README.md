# PepperPal REST API

## Build

```bash
./mvnw clean install package -pl api
```

## Deploy

The jar file might be started with the following command:

```bash
java -jar api/target/api-0.0.1.jar
```

or

```bash
./api/target/api-0.0.1.jar
```

Apparently, the jar file can be made executable by default.

However, you might want to use a more sophisticated way to start the jar file, e.g. using systemd.

### Systemd

Create the following file at `/etc/systemd/system/pepperpal-api.service`:

```ini
[Unit]
Description=PepperPal REST API
After=syslog.target

[Service]
User=pi # Change this to the user you want to run the service as
ExecStart=/home/pi/pepperpal/api/api-0.0.1.jar # Change this to the path of the jar
SuccessExitStatus=143
Restart=on-failure

[Install]
WantedBy=multi-user.target
```

Then run:

```bash
sudo systemctl daemon-reload
sudo systemctl enable pepperpal-api
sudo systemctl start pepperpal-api
```

See
the [Spring Boot documentation](https://docs.spring.io/spring-boot/docs/current/reference/html/deployment.html#deployment-systemd-service)
for more information.

## Usage

After correct deployment the REST API is available at `http://localhost:8080`.

You can test the API by sending a request:

```bash
curl -X POST -H "Content-Type: application/json" -d '{"temperature":"22.3", "moistureLevel":"3", "relativeHumidity":"7"}' localhost:8080/pepperpal/v1/soilData
```
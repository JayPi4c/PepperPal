# Chili-REST-API

## Build

```bash
./mvnw clean install package
```

## Deploy

The jar file might be started with the following command:

```bash
java -jar target/chili-rest-api.jar
```

or 

```bash
./target/chili-rest-api.jar
```

Apparently, the jar file can be made executable by default. 

However, you might want to use a more sophisticated way to start the jar file, e.g. using systemd.

### Systemd

Create the following file at `/etc/systemd/system/chili-rest-api.service`:

```ini
[Unit]
Description=Chili API
After=syslog.target

[Service]
User=pi # Change this to the user you want to run the service as
ExecStart=/home/pi/chili/rest-api/chili-rest-api.jar # Change this to the path of the jar
SuccessExitStatus=143
Restart=on-failure

[Install]
WantedBy=multi-user.target
```

Then run:

```bash
sudo systemctl daemon-reload
sudo systemctl enable chili-rest-api
sudo systemctl start chili-rest-api
```
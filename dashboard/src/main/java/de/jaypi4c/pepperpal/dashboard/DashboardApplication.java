package de.jaypi4c.pepperpal.dashboard;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class DashboardApplication {

    static void main(String[] args) {
        SpringApplication.run(DashboardApplication.class, args);
    }

}

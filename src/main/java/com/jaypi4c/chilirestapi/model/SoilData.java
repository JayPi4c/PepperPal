package com.jaypi4c.chilirestapi.model;


import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
@ToString
@NoArgsConstructor
public class SoilData {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime created;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime updated;

    private float temperature;
    private float relativeHumidity;
    private float waterlevel;

    public SoilData(float temperature, float relativeHumidity, float waterlevel) {
        this.temperature = temperature;
        this.relativeHumidity = relativeHumidity;
        this.waterlevel = waterlevel;
    }

    @PrePersist
    protected void onCreate() {
        created = LocalDateTime.now();
        updated = created;
    }

    @PreUpdate
    protected void onUpdate() {
        updated = LocalDateTime.now();
    }
}
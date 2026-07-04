package de.jaypi4c.pepperpal.api.exception;

import java.util.UUID;

public class SoilDataNotFoundException extends RuntimeException {

    public SoilDataNotFoundException(UUID id) {
        super("Could not find entry with ID: " + id);
    }
}

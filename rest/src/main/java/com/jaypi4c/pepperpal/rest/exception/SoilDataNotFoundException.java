package com.jaypi4c.pepperpal.rest.exception;

import java.util.UUID;

public class SoilDataNotFoundException extends RuntimeException {

    public SoilDataNotFoundException(UUID id) {
        super("Could not find employee " + id);
    }
}
package com.github.old_horizon.selenium.hub;

import org.openqa.grid.internal.ExternalSessionKey;
import org.openqa.grid.internal.GridRegistry;

import java.net.URL;
import java.util.Optional;

public class BaseUrlDriver implements BaseUrlPort {
    private final GridRegistry registry;

    public BaseUrlDriver(GridRegistry registry) {
        this.registry = registry;
    }

    @Override
    public URL find(String sessionKey) {
        return Optional.ofNullable(registry.getSession(ExternalSessionKey.fromString(sessionKey)))
                .map(session -> session.getSlot().getProxy().getRemoteHost())
                .orElseThrow(() -> new SessionNotFoundException("session associated with key " + sessionKey + " not found"));
    }
}

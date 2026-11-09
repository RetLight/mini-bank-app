package com.project.minibank.common.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.Arrays;

// Al arrancar dice de dónde salió la configuración: "config-server" si la sirvió el Config Server,
// "local" si este no respondió y el servicio arrancó con los valores de respaldo de su application.properties.
@Component
class ConfigOriginLogger {

    private static final Logger log = LoggerFactory.getLogger(ConfigOriginLogger.class);

    private final String origin;
    private final Environment environment;

    ConfigOriginLogger(@Value("${minibank.config-origin:local}") String origin, Environment environment) {
        this.origin = origin;
        this.environment = environment;
    }

    @EventListener(ApplicationReadyEvent.class)
    void logOrigin() {
        log.info("Configuración cargada desde: {} (perfiles activos: {}, puerto: {})", origin,
                Arrays.toString(environment.getActiveProfiles()), environment.getProperty("server.port"));
    }
}

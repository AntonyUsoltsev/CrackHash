package ru.nsu.usoltsev.worker.config;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app")
@RequiredArgsConstructor
@Getter
public class AppConfigs {
    private final ManagerProperties manager;

    @ConfigurationProperties(prefix = "app.manager")
    @RequiredArgsConstructor
    @Getter
    public static class ManagerProperties {
        private final String baseUrl;
    }
}

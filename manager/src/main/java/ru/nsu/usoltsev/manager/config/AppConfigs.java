package ru.nsu.usoltsev.manager.config;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app")
@RequiredArgsConstructor
@Getter
public class AppConfigs {
    private final WorkersProperties workers;

    @ConfigurationProperties(prefix = "app.workers")
    @RequiredArgsConstructor
    @Getter
    public static class WorkersProperties {
        private final String baseUrl;
        private final Integer count;
    }

}

package ru.nsu.usoltsev.worker.config;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@ConfigurationProperties(prefix = "app", ignoreUnknownFields = false)
@Configuration
public class AppConfigs {
    private ManagerProperties manager;

    @Data
    public static class ManagerProperties {
        private String baseUrl;
    }
}

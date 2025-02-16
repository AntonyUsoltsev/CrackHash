package ru.nsu.usoltsev.manager.config;

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
    private WorkersProperties workers;

    @Data
    public static class WorkersProperties {
        private String baseUrl;
        private Integer count;
    }

}

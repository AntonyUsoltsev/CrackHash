package ru.nsu.usoltsev.manager.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class WebClientConfig {
    private final AppConfigs appConfigs;

    @Bean("managerWebClient")
    public WebClient managerWebClient() {
        AppConfigs.WorkersProperties workersProperties = appConfigs.getWorkers();
        return WebClient
                .builder()
                .baseUrl(workersProperties.getBaseUrl())
                .build();
    }
}

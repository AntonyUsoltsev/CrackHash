package ru.nsu.usoltsev.worker.config;

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
        AppConfigs.ManagerProperties managerProperties = appConfigs.getManager();
        return WebClient
                .builder()
                .baseUrl(managerProperties.getBaseUrl())
                .build();
    }
}

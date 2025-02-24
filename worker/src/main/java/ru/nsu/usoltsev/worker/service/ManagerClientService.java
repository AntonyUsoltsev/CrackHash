package ru.nsu.usoltsev.worker.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import ru.nsu.usoltsev.worker.model.response.WorkerTaskResponse;

@Slf4j
@Service
@RequiredArgsConstructor
public class ManagerClientService {
    private final WebClient managerWebClient;
    private final ObjectMapperClient objectMapperClient;

    public void sendResultToManger(WorkerTaskResponse result) {
        log.info("sendResultToManger(): result: {}", objectMapperClient.objectToJson(result));
        managerWebClient.patch()
                .uri("/internal/api/manager/hash/crack/request")
                .bodyValue(result)
                .retrieve()
                .toBodilessEntity()
                .block();
    }
}

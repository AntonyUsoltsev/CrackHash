package ru.nsu.usoltsev.manager.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import ru.nsu.usoltsev.manager.model.request.WorkerTaskRequest;

@Slf4j
@Service
@RequiredArgsConstructor
public class WorkerClientService {
    private final WebClient workerWebClient;
    private final ObjectMapperClient objectMapperClient;

    public void sendTaskToWorker(WorkerTaskRequest task) {
        log.info("sendTaskWorker(): task: {}", objectMapperClient.objectToJson(task));
        workerWebClient.post()
                .uri("/internal/api/worker/hash/crack/task")
                .bodyValue(task)
                .retrieve()
                .toBodilessEntity()
                .block();
    }
}

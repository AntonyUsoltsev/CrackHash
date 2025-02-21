package ru.nsu.usoltsev.manager.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import ru.nsu.usoltsev.manager.model.request.WorkerTaskRequest;
import ru.nsu.usoltsev.manager.model.response.WorkerTaskResponse;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class WorkerClientService {
    private final WebClient managerWebClient;
    private final ObjectMapperClient objectMapperClient;

    public CompletableFuture<List<String>> sendTaskToWorker(WorkerTaskRequest task) {
        log.info("sendTaskWorker(): task: {}", objectMapperClient.objectToJson(task));
        return managerWebClient.post()
                .uri("/internal/api/worker/hash/crack/task")
                .bodyValue(task)
                .retrieve()
                .bodyToMono(WorkerTaskResponse.class)
                .map(WorkerTaskResponse::getMatchingWords)
                .toFuture();
    }
}

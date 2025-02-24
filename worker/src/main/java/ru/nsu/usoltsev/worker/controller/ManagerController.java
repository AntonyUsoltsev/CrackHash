package ru.nsu.usoltsev.worker.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.nsu.usoltsev.worker.model.request.WorkerTaskRequest;
import ru.nsu.usoltsev.worker.model.response.WorkerTaskResponse;
import ru.nsu.usoltsev.worker.service.ObjectMapperClient;
import ru.nsu.usoltsev.worker.service.WorkerTaskProcessorService;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ManagerController {
    private final WorkerTaskProcessorService workerTaskProcessorService;
    private final ObjectMapperClient objectMapperClient;

    @PostMapping("/internal/api/worker/hash/crack/task")
    public ResponseEntity<WorkerTaskResponse> startCrackTask(@RequestBody WorkerTaskRequest workerTaskRequest) {
        List<String> words = workerTaskProcessorService.processTask(
                workerTaskRequest.getMaxLength(),
                workerTaskRequest.getStartIndex(),
                workerTaskRequest.getEndIndex(),
                workerTaskRequest.getHash()
        );
        log.info("Matching words: {}", objectMapperClient.objectToJson(words));
        return ResponseEntity.ok(new WorkerTaskResponse(words));
    }
}

package ru.nsu.usoltsev.worker.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.nsu.usoltsev.worker.model.request.WorkerTaskRequest;
import ru.nsu.usoltsev.worker.service.WorkerTaskProcessorService;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ManagerController {
    private final WorkerTaskProcessorService workerTaskProcessorService;

    @PostMapping("/internal/api/worker/hash/crack/task")
    public ResponseEntity<Void> startCrackTask(@RequestBody WorkerTaskRequest workerTaskRequest) {
        workerTaskProcessorService.processTaskAsync(workerTaskRequest);
        return ResponseEntity.ok().build();
    }
}

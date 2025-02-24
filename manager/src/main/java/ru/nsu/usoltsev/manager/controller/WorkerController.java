package ru.nsu.usoltsev.manager.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.nsu.usoltsev.manager.model.response.WorkerTaskResponse;
import ru.nsu.usoltsev.manager.service.CrackHashService;

@RestController
@RequiredArgsConstructor
public class WorkerController {
    private final CrackHashService crackHashService;

    @PatchMapping("/internal/api/manager/hash/crack/request")
    public ResponseEntity<Void> getResultFromWorker(@RequestBody WorkerTaskResponse response) {
        crackHashService.processWorkerResult(response);
        return ResponseEntity.ok().build();
    }
}

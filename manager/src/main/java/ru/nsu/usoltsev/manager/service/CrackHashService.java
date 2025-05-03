package ru.nsu.usoltsev.manager.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import ru.nsu.usoltsev.manager.config.AppConfigs;
import ru.nsu.usoltsev.manager.model.Status;
import ru.nsu.usoltsev.manager.model.entity.Task;
import ru.nsu.usoltsev.manager.model.request.WorkerTaskRequest;
import ru.nsu.usoltsev.manager.model.response.WorkerTaskResponse;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class CrackHashService {
    private final WorkerClientService workerClientService;
    private final AppConfigs appConfigs;
    private final MongoService mongoService;
    private final PendingTaskService pendingTaskService;


    @Async("taskHandlerExecutor")
    public void processCrackHashRequest(UUID requestId, String hash, int maxLength) {
        int workerCount = appConfigs.getWorkers().getCount();

        for (int i = 0; i < workerCount; i++) {
            WorkerTaskRequest taskRequest = WorkerTaskRequest.builder()
                    .taskId(UUID.randomUUID())
                    .requestId(requestId)
                    .hash(hash)
                    .maxLength(maxLength)
                    .chunkNumber(i + 1)
                    .totalChunks(workerCount)
                    .build();
            try {
                workerClientService.sendTaskToWorker(taskRequest);
            } catch (Exception ex) {
                log.error("Error sending task {} to worker: {}. Saving to pending tasks.", taskRequest.getRequestId(), ex.getMessage());
                pendingTaskService.savePendingTask(taskRequest);
            }
        }
    }

    public void processWorkerResult(WorkerTaskResponse response) {
        UUID requestId = response.getRequestId();
        Task task = mongoService.getTask(requestId);
        if (task != null) {
            mongoService.appendResults(requestId, response.getMatchingWords(), response.getChunkNumber());
        } else {
            log.warn("Get unknown requestId: {} in workers response", requestId);
        }

        if (mongoService.isTaskCompleted(requestId)) {
            log.info("Finished computing matching words for request: {}", requestId);
            mongoService.updateTaskStatus(requestId, Status.READY.toString());
        } else {
            log.info("Received chunk {} for request: {}. Awaiting remaining parts.", response.getChunkNumber(), requestId);
        }
    }
}

package ru.nsu.usoltsev.manager.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Service;
import ru.nsu.usoltsev.manager.config.AppConfigs;
import ru.nsu.usoltsev.manager.model.Status;
import ru.nsu.usoltsev.manager.model.entity.Task;
import ru.nsu.usoltsev.manager.model.request.WorkerTaskRequest;
import ru.nsu.usoltsev.manager.model.response.StatusResponseDto;
import ru.nsu.usoltsev.manager.model.response.WorkerTaskResponse;

import java.time.Instant;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class CrackHashService {
    private final WorkerClientService workerClientService;
    private final CacheService cacheService;
    private final AppConfigs appConfigs;
    private final ThreadPoolTaskScheduler scheduler;
    private final MongoService mongoService;
    private final PendingTaskService pendingTaskService;

    private final ConcurrentMap<UUID, TaskCollectorService> collectorsMap = new ConcurrentHashMap<>();
    private final static Integer TIMEOUT = 100;

    @Async("taskHandlerExecutor")
    public void processCrackHashRequest(UUID requestId, String hash, int maxLength) {
        int workerCount = appConfigs.getWorkers().getCount();

        TaskCollectorService collector = new TaskCollectorService(workerCount);
        collectorsMap.put(requestId, collector);
        cacheService.updateTaskStatus(requestId, new StatusResponseDto(Status.IN_PROGRESS, null));

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

        Instant timeoutDate = new Date(System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(TIMEOUT)).toInstant();
        scheduler.schedule(() -> {

            if (!collector.isCompleted()) {
                log.error("Timeout for request {}", requestId);
                cacheService.updateTaskStatus(requestId, new StatusResponseDto(Status.ERROR, null));
                collectorsMap.remove(requestId);
            }
        }, timeoutDate);
    }

    public void processWorkerResult(WorkerTaskResponse response) {
        UUID requestId = response.getRequestId();
        mongoService.appendResults(requestId, response.getMatchingWords(), response.getChunkNumber());
        TaskCollectorService collector = collectorsMap.get(requestId);
        if (collector != null) {
            collector.addResult(response.getMatchingWords());
        } else {
            log.warn("Get unknown requestId: {} in workers response", requestId);
        }

        Task task = mongoService.getTask(requestId);
        if (task != null && mongoService.isTaskCompleted(requestId)) {
            log.info("Finished computing matching words for request: {}", requestId);
            cacheService.updateTaskStatus(requestId, new StatusResponseDto(Status.READY, task.getTaskResults()));
            mongoService.updateTaskStatus(requestId, Status.READY.toString());
            collectorsMap.remove(requestId);
        } else {
            log.info("Received chunk {} for request: {}. Awaiting remaining parts.", response.getChunkNumber(), requestId);
        }
    }
}

package ru.nsu.usoltsev.manager.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Service;
import ru.nsu.usoltsev.manager.config.AppConfigs;
import ru.nsu.usoltsev.manager.model.Status;
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

    private final ConcurrentMap<UUID, TaskCollectorService> collectorsMap = new ConcurrentHashMap<>();
    private final static Integer TIMEOUT = 100;

    public void processCrackHashRequest(UUID requestId, String hash, int maxLength) {
        int workerCount = appConfigs.getWorkers().getCount();

        TaskCollectorService collector = new TaskCollectorService(workerCount);
        collectorsMap.put(requestId, collector);
        cacheService.updateTaskStatus(requestId, new StatusResponseDto(Status.IN_PROGRESS, null));

        for (int i = 0; i < workerCount; i++) {
            WorkerTaskRequest taskRequest = WorkerTaskRequest.builder()
                    .requestId(requestId)
                    .hash(hash)
                    .maxLength(maxLength)
                    .chunkNumber(i + 1)
                    .totalChunks(workerCount)
                    .build();
            workerClientService.sendTaskToWorker(taskRequest);
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
        TaskCollectorService collector = collectorsMap.get(requestId);
        if (collector != null) {
            collector.addResult(response.getMatchingWords());
            if (collector.isCompleted()) {
                log.info("Finished computing matching words for request: {}", requestId);
                cacheService.updateTaskStatus(requestId, new StatusResponseDto(Status.READY, collector.getResults()));
                collectorsMap.remove(requestId);
            }
        } else {
            log.warn("Get unknown requestId: {} in workers response", requestId);
        }
    }
}

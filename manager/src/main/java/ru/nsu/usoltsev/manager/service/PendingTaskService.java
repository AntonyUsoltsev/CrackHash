package ru.nsu.usoltsev.manager.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.nsu.usoltsev.manager.model.entity.PendingTask;
import ru.nsu.usoltsev.manager.model.request.WorkerTaskRequest;
import ru.nsu.usoltsev.manager.repository.PendingTaskRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PendingTaskService {

    private final PendingTaskRepository pendingTaskRepository;
    private final @Lazy WorkerClientService workerClientService;

    public void savePendingTask(WorkerTaskRequest task) {
        PendingTask pendingTask = PendingTask.builder()
                .id(task.getTaskId())
                .requestId(task.getRequestId())
                .hash(task.getHash())
                .maxLength(task.getMaxLength())
                .chunkNumber(task.getChunkNumber())
                .totalChunks(task.getTotalChunks())
                .timestamp(System.currentTimeMillis())
                .build();
        pendingTaskRepository.save(pendingTask);
        log.info("Saved pending task for request {} chunk {}", task.getRequestId(), task.getChunkNumber());
    }

    @Scheduled(fixedDelay = 10000)
    public void resendPendingTasks() {
        List<PendingTask> pendingTasks = pendingTaskRepository.findAll();
        if (!pendingTasks.isEmpty()) {
            log.info("Resending {} pending tasks", pendingTasks.size());
        }
        for (PendingTask pt : pendingTasks) {
            WorkerTaskRequest task = WorkerTaskRequest.builder()
                    .taskId(pt.getId())
                    .requestId(pt.getRequestId())
                    .hash(pt.getHash())
                    .maxLength(pt.getMaxLength())
                    .chunkNumber(pt.getChunkNumber())
                    .totalChunks(pt.getTotalChunks())
                    .build();
            try {
                workerClientService.sendTaskToWorker(task);
                pendingTaskRepository.delete(pt);
                log.info("Pending task {} chunk {} resent successfully", pt.getRequestId(), pt.getChunkNumber());
            } catch (Exception ex) {
                log.error("Error resending pending task {} chunk {}: {}", pt.getRequestId(), pt.getChunkNumber(), ex.getMessage());
            }
        }
    }
}

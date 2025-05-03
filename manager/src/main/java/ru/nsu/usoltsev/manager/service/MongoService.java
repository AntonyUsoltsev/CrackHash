package ru.nsu.usoltsev.manager.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.nsu.usoltsev.manager.config.AppConfigs;
import ru.nsu.usoltsev.manager.model.Status;
import ru.nsu.usoltsev.manager.model.entity.Task;
import ru.nsu.usoltsev.manager.repository.TaskRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class MongoService {
    private final AppConfigs appConfigs;
    private final TaskRepository taskRepository;

    public void saveTask(Task task) {
        taskRepository.save(task);
    }

    public Task getTask(UUID id) {
        return taskRepository.findById(id).orElse(null);
    }

    public void updateTaskStatus(UUID id, String status) {
        Task task = getTask(id);
        if (task != null) {
            task.setStatus(status);
            taskRepository.save(task);
        }
    }

    public Task createNewTask(String hash, int maxLength) {
        Task task = new Task();
        UUID requestId = UUID.randomUUID();
        task.setId(requestId);
        task.setStatus(Status.IN_PROGRESS.toString());
        task.setTaskDetails(new Task.TaskDetails(hash, maxLength, appConfigs.getWorkers().getCount()));
        task.setReplicated(false);
        saveTask(task);
        markReplicated(requestId);
        log.info("Task created in mongo");
        return getTask(requestId);
    }

    public void markReplicated(UUID id) {
        Task task = getTask(id);
        if (task != null) {
            task.setReplicated(true);
            saveTask(task);
        }
    }

    public void appendResults(UUID id, List<String> results, int chunkNumber) {
        Task task = getTask(id);
        if (task != null) {
            if (!task.getReceivedChunks().contains(chunkNumber)) {
                if (task.getTaskResults() == null) {
                    task.setTaskResults(new ArrayList<>());
                }
                task.getTaskResults().addAll(results);
                task.getReceivedChunks().add(chunkNumber);
                saveTask(task);
            }
        }
    }

    public boolean isTaskCompleted(UUID id) {
        Task task = getTask(id);
        if (task == null) return false;
        return task.getReceivedChunks().size() >= task.getTaskDetails().getWorkerCount();
    }
}

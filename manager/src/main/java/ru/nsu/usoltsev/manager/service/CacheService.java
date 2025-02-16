package ru.nsu.usoltsev.manager.service;

import org.springframework.stereotype.Service;
import ru.nsu.usoltsev.manager.model.response.StatusResponseDto;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class CacheService {
    private final Map<UUID, StatusResponseDto> taskStateMap = new ConcurrentHashMap<>();

    public void updateTaskStatus(UUID taskId, StatusResponseDto status) {
        taskStateMap.put(taskId, status);
    }

    public StatusResponseDto getTaskStatus(UUID taskId) {
        return taskStateMap.get(taskId);
    }

    public void removeTask(UUID taskId) {
        taskStateMap.remove(taskId);
    }
}

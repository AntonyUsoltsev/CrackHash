package ru.nsu.usoltsev.manager.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import ru.nsu.usoltsev.manager.model.entity.PendingTask;

import java.util.UUID;

public interface PendingTaskRepository extends MongoRepository<PendingTask, UUID> {
}

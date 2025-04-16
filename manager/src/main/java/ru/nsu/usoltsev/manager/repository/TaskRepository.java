package ru.nsu.usoltsev.manager.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import ru.nsu.usoltsev.manager.model.entity.Task;

import java.util.UUID;

public interface TaskRepository extends MongoRepository<Task, UUID> {
}


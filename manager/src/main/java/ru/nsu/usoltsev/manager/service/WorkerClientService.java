package ru.nsu.usoltsev.manager.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import ru.nsu.usoltsev.manager.model.request.WorkerTaskRequest;

import static ru.nsu.usoltsev.manager.config.RabbitMqConfig.MANAGER_TO_WORKER_EXCHANGE;
import static ru.nsu.usoltsev.manager.config.RabbitMqConfig.MANAGER_TO_WORKER_ROUTING_KEY;

@Slf4j
@Service
@RequiredArgsConstructor
public class WorkerClientService {
    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapperClient objectMapperClient;

    public void sendTaskToWorker(WorkerTaskRequest task) {
        log.info("sendTaskWorker(): task: {}", objectMapperClient.objectToJson(task));
        log.info("Sending task to worker via RabbitMQ: {}", objectMapperClient.objectToJson(task));
        rabbitTemplate.convertAndSend(MANAGER_TO_WORKER_EXCHANGE, MANAGER_TO_WORKER_ROUTING_KEY, task);
    }
}

package ru.nsu.usoltsev.worker.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import ru.nsu.usoltsev.worker.model.response.WorkerTaskResponse;

import static ru.nsu.usoltsev.worker.config.RabbitMqConfig.WORKER_TO_MANAGER_EXCHANGE;
import static ru.nsu.usoltsev.worker.config.RabbitMqConfig.WORKER_TO_MANAGER_ROUTING_KEY;

@Slf4j
@Service
@RequiredArgsConstructor
public class ManagerClientService {

    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapperClient objectMapperClient;

    public void sendResultToManger(WorkerTaskResponse response) {
        log.info("Sending result to manager via RabbitMQ: {}", objectMapperClient.objectToJson(response));
        rabbitTemplate.convertAndSend(WORKER_TO_MANAGER_EXCHANGE, WORKER_TO_MANAGER_ROUTING_KEY, response);
    }
}

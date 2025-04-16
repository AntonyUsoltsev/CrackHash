package ru.nsu.usoltsev.worker.controller;

import com.rabbitmq.client.Channel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;
import ru.nsu.usoltsev.worker.model.request.WorkerTaskRequest;
import ru.nsu.usoltsev.worker.service.WorkerTaskProcessorService;

import static ru.nsu.usoltsev.worker.config.RabbitMqConfig.MANAGER_TO_WORKER_QUEUE;

@Slf4j
@Service
@RequiredArgsConstructor
public class ManagerController {
    private final WorkerTaskProcessorService taskProcessorService;

    @RabbitListener(queues = MANAGER_TO_WORKER_QUEUE)
    public void handleTask(WorkerTaskRequest taskRequest,
                           @Header(AmqpHeaders.DELIVERY_TAG) long tag,
                           Channel channel) throws Exception {
        try {
            log.info("Received task: {} from manager request: {}", taskRequest.getTaskId(), taskRequest.getRequestId());
            taskProcessorService.processTaskAsync(taskRequest, tag, channel);
        } catch (Exception e) {
            log.error("Failed to process task", e);
            channel.basicNack(tag, false, true);
        }
    }
}

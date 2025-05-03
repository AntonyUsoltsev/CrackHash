package ru.nsu.usoltsev.manager.controller;

import com.rabbitmq.client.Channel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;
import ru.nsu.usoltsev.manager.model.response.WorkerTaskResponse;
import ru.nsu.usoltsev.manager.service.CrackHashService;

import static ru.nsu.usoltsev.manager.config.RabbitMqConfig.WORKER_TO_MANAGER_QUEUE;

@Slf4j
@Service
@RequiredArgsConstructor
public class WorkerController {
    private final CrackHashService crackHashService;

    @RabbitListener(
            queues = WORKER_TO_MANAGER_QUEUE,
            containerFactory = "rabbitListenerContainerFactory"
    )
    public void handleWorkerResult(WorkerTaskResponse response,
                                   @Header(AmqpHeaders.DELIVERY_TAG) long tag,
                                   Channel channel) throws Exception {
        log.info("Received result from worker: {}", response.getRequestId());
        crackHashService.processWorkerResult(response);
        channel.basicAck(tag, false);
    }
}

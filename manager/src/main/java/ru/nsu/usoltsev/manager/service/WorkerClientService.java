package ru.nsu.usoltsev.manager.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@RequiredArgsConstructor
public class WorkerClientService {
    private final WebClient managerWebClient;

    public void sendTaskToWorker(){

    }
}

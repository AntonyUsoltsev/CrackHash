package ru.nsu.usoltsev.manager.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.nsu.usoltsev.manager.config.AppConfigs;
import ru.nsu.usoltsev.manager.model.Status;
import ru.nsu.usoltsev.manager.model.request.WorkerTaskRequest;
import ru.nsu.usoltsev.manager.model.response.StatusResponseDto;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CrackHashService {
    private final WorkerClientService workerClientService;
    private final CacheService cacheService;
    private final AppConfigs appConfigs;

    public void processCrackHashRequest(UUID requestId, String hash, int maxLength) {
        BigInteger totalCombinations = BigInteger.ZERO;
        BigInteger base = BigInteger.valueOf(36);
        for (int k = 1; k <= maxLength; k++) {
            totalCombinations = totalCombinations.add(base.pow(k));
        }

        int workerCount = appConfigs.getWorkers().getCount();
        BigInteger chunk = totalCombinations.divide(BigInteger.valueOf(workerCount));

        List<CompletableFuture<List<String>>> futures = new ArrayList<>();
        BigInteger start = BigInteger.ZERO;
        for (int i = 0; i < workerCount; i++) {
            BigInteger end;
            if (i == workerCount - 1) {
                end = totalCombinations;
            } else {
                end = start.add(chunk);
            }

            WorkerTaskRequest taskRequest = new WorkerTaskRequest(requestId, hash, maxLength, start, end);
            CompletableFuture<List<String>> future = workerClientService.sendTaskToWorker(taskRequest);
            futures.add(future);
            start = end;
        }

        try {
            CompletableFuture<Void> allFutures = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
            allFutures.get(60, TimeUnit.SECONDS);

            List<String> results = futures.stream()
                    .map(CompletableFuture::join)
                    .flatMap(List::stream)
                    .collect(Collectors.toList());
            cacheService.updateTaskStatus(requestId, new StatusResponseDto(Status.READY, results));
        } catch (TimeoutException e) {
            log.error("Worker request timed out", e);
            cacheService.updateTaskStatus(requestId, new StatusResponseDto(Status.ERROR, null));
        } catch (Exception e) {
            log.error("Got random exception while waiting answers from workers", e);
            cacheService.updateTaskStatus(requestId, new StatusResponseDto(Status.ERROR, null));
        }
    }
}

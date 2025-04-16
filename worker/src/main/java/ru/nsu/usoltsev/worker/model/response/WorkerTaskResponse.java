package ru.nsu.usoltsev.worker.model.response;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.UUID;

@Getter
@Builder
@RequiredArgsConstructor
public class WorkerTaskResponse {
    private final UUID requestId;
    private final UUID taskId;
    private final int chunkNumber;
    private final List<String> matchingWords;
}

package ru.nsu.usoltsev.manager.model.request;

import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@Data
@Builder
@RequiredArgsConstructor
public class WorkerTaskRequest {
    private final UUID requestId;
    private final String hash;
    private final int maxLength;
    private final int chunkNumber;
    private final int totalChunks;
}

package ru.nsu.usoltsev.manager.model.request;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.math.BigInteger;
import java.util.UUID;

@Data
@RequiredArgsConstructor
public class WorkerTaskRequest {
    private final UUID requestId;
    private final String hash;
    private final int maxLength;
    private final BigInteger startIndex;
    private final BigInteger endIndex;
    private final int chunkNumber;
    private final int totalChunks;
}

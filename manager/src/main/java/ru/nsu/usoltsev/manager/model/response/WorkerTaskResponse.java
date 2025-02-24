package ru.nsu.usoltsev.manager.model.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.UUID;

@Getter
@RequiredArgsConstructor
public class WorkerTaskResponse {
    private final UUID requestId;
    private final List<String> matchingWords;
}

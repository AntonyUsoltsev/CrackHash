package ru.nsu.usoltsev.manager.model.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class WorkerTaskResponse {
    private final List<String> matchingWords;
}

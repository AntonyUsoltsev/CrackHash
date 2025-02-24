package ru.nsu.usoltsev.manager.service;

import java.util.ArrayList;
import java.util.List;

public class TaskCollectorService {
    private final int totalChunks;
    private final List<String> results = new ArrayList<>();
    private int receivedChunks = 0;

    public TaskCollectorService(int totalChunks) {
        this.totalChunks = totalChunks;
    }

    public synchronized void addResult(List<String> partialResults) {
        results.addAll(partialResults);
        receivedChunks++;
    }

    public synchronized boolean isCompleted() {
        return receivedChunks >= totalChunks;
    }

    public synchronized List<String> getResults() {
        return new ArrayList<>(results);
    }
}

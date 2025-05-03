package ru.nsu.usoltsev.manager.model.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Document(collection = "tasks")
public class Task {
    @Id
    private UUID id;
    private String status;
    private TaskDetails taskDetails;
    private List<String> taskResults;
    private String timestamp;
    private boolean replicated;
    private List<Integer> receivedChunks = new ArrayList<>();

    @Getter
    @Setter
    @AllArgsConstructor
    public static class TaskDetails {
        private String hash;
        private int maxLength;
        private int workerCount;
    }
}

package ru.nsu.usoltsev.manager.model.entity;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.UUID;

@Data
@Builder
@Document(collection = "pending_tasks")
public class PendingTask {
    @Id
    private UUID id;
    private UUID requestId;
    private String hash;
    private int maxLength;
    private int chunkNumber;
    private int totalChunks;
    private long timestamp;
}

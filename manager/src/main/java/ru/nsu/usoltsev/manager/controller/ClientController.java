package ru.nsu.usoltsev.manager.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.nsu.usoltsev.manager.model.Status;
import ru.nsu.usoltsev.manager.model.request.StartCrackRequestDto;
import ru.nsu.usoltsev.manager.model.response.StartCrackResponseDto;
import ru.nsu.usoltsev.manager.model.response.StatusResponseDto;
import ru.nsu.usoltsev.manager.service.CacheService;

import java.util.UUID;

@RestController("api/hash")
@RequiredArgsConstructor
public class ClientController {

    private final CacheService cacheService;

    @PostMapping("/crack")
    public ResponseEntity<StartCrackResponseDto> startCrackHash(@RequestBody StartCrackRequestDto startCrackRequestDto) {
        UUID uuid = UUID.randomUUID();
        cacheService.updateTaskStatus(uuid, new StatusResponseDto(Status.IN_PROGRESS, null));
        // send to workers
        return ResponseEntity.ok(new StartCrackResponseDto(uuid));
    }

    @GetMapping("/status")
    public ResponseEntity<StatusResponseDto> getRequestInfo(@RequestParam UUID requestId) {
        return ResponseEntity.ok(cacheService.getTaskStatus(requestId));
    }
}

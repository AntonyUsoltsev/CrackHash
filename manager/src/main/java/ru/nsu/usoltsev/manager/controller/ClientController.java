package ru.nsu.usoltsev.manager.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import ru.nsu.usoltsev.manager.service.CrackHashService;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ClientController {
    private final CacheService cacheService;
    private final CrackHashService crackHashService;

    @PostMapping("/api/hash/crack")
    public ResponseEntity<StartCrackResponseDto> startCrackHash(@RequestBody StartCrackRequestDto startCrackRequestDto) {
        UUID requestId = UUID.randomUUID();
        cacheService.updateTaskStatus(requestId, new StatusResponseDto(Status.IN_PROGRESS, null));
        CompletableFuture.runAsync(() ->
                crackHashService.processCrackHashRequest(requestId, startCrackRequestDto.getHash(), startCrackRequestDto.getMaxLength()));
        return ResponseEntity.ok(new StartCrackResponseDto(requestId));
    }

    @GetMapping("/api/hash/status")
    public ResponseEntity<StatusResponseDto> getRequestInfo(@RequestParam UUID requestId) {
        return ResponseEntity.ok(cacheService.getTaskStatus(requestId));
    }
}

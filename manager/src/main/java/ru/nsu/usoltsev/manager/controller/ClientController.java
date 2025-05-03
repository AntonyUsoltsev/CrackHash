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
import ru.nsu.usoltsev.manager.model.entity.Task;
import ru.nsu.usoltsev.manager.model.request.StartCrackRequestDto;
import ru.nsu.usoltsev.manager.model.response.StartCrackResponseDto;
import ru.nsu.usoltsev.manager.model.response.StatusResponseDto;
import ru.nsu.usoltsev.manager.service.CrackHashService;
import ru.nsu.usoltsev.manager.service.MongoService;

import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ClientController {
    private final CrackHashService crackHashService;
    private final MongoService mongoService;

    @PostMapping("/api/hash/crack")
    public ResponseEntity<StartCrackResponseDto> startCrackHash(@RequestBody StartCrackRequestDto startCrackRequestDto) {
        Task task = mongoService.createNewTask(startCrackRequestDto.getHash(), startCrackRequestDto.getMaxLength());
        crackHashService.processCrackHashRequest(task.getId(), startCrackRequestDto.getHash(), startCrackRequestDto.getMaxLength());
        return ResponseEntity.ok(new StartCrackResponseDto(task.getId()));
    }

    @GetMapping("/api/hash/status")
    public ResponseEntity<StatusResponseDto> getRequestInfo(@RequestParam UUID requestId) {
        Task task = mongoService.getTask(requestId);
        return ResponseEntity.ok(StatusResponseDto.builder()
                .status(Status.forStr(task.getStatus()))
                .data(task.getTaskResults())
                .build());
    }
}

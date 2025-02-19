package ru.nsu.usoltsev.manager.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.nsu.usoltsev.manager.model.request.StartCrackRequestDto;
import ru.nsu.usoltsev.manager.model.response.StartCrackResponseDto;
import ru.nsu.usoltsev.manager.model.response.StatusResponseDto;

import java.util.UUID;

@RestController
public class WorkerController {
    @PatchMapping("/internal/api/manager/hash/crack/request")
    public void getResultFromWorker() {
        return ;
    }

}

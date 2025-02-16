package ru.nsu.usoltsev.worker.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("/internal/api/worker/hash/crack")
public class ManagerController {

    @PostMapping("/task")
    public void startCrackTask() {

    }
}

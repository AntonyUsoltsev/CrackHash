package ru.nsu.usoltsev.manager.model.response;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.UUID;

@RequiredArgsConstructor
@Getter
@ToString
@Builder
public class StartCrackResponseDto {
    private final UUID requestId;
}

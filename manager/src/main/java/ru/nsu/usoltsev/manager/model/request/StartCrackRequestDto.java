package ru.nsu.usoltsev.manager.model.request;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@RequiredArgsConstructor
@Getter
@ToString
public class StartCrackRequestDto {
    private final String hash;
    private final Integer maxLength;
}

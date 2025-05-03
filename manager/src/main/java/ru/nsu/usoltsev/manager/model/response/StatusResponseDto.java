package ru.nsu.usoltsev.manager.model.response;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import ru.nsu.usoltsev.manager.model.Status;

import java.util.List;

@RequiredArgsConstructor
@Getter
@ToString
@Builder
public class StatusResponseDto {
    private final Status status;
    private final List<String> data;
}

package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.RequestRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
@Validated
public class RequestController {
    private final RequestClient requestClient;
    private static final String USER_ID_HEADER = "X-Sharer-User-Id";

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getRequest(@RequestHeader(USER_ID_HEADER) long userId,
                                             @PathVariable long requestId) {
        log.info("Get request {}, userId={}", requestId, userId);
        return requestClient.getRequest(requestId, userId);
    }

    @PostMapping
    public ResponseEntity<Object> addRequest(@RequestHeader(USER_ID_HEADER) long userId,
                                             @Valid @RequestBody RequestRequestDto requestDto) {
        log.info("Creating request {}, userId={}", requestDto, userId);
        return requestClient.addRequest(userId, requestDto);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getRequests(@RequestHeader(USER_ID_HEADER) long userId,
                                              @PositiveOrZero @RequestParam(name = "from", defaultValue = "0")
                                              Integer from,
                                              @Positive @RequestParam(name = "size", defaultValue = "10")
                                              Integer size) {
        log.info("Get requests not by userId={}, from={}, size={}", userId, from, size);
        return requestClient.getRequests(userId, from, size);
    }

    @GetMapping()
    public ResponseEntity<Object> getRequestsByUserId(@RequestHeader(USER_ID_HEADER) long userId) {
        log.info("Get requests by userId {}", userId);
        return requestClient.getRequestsByUserId(userId);
    }
}

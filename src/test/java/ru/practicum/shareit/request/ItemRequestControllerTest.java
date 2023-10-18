package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.ItemDtoWithRequest;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemRequestController.class)
@AutoConfigureMockMvc
public class ItemRequestControllerTest {
    @MockBean
    private ItemRequestService itemRequestService;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper mapper;
    private static ItemRequestDto itemRequestDto;
    private static ItemRequestResponseDto itemRequestResponseDto;
    private static final String USER_ID_HEADER = "X-Sharer-User-Id";

    @BeforeAll
    static void setUp() {
        List<ItemDtoWithRequest> items = Collections.singletonList(new ItemDtoWithRequest(1L, "item",
                "description", Boolean.TRUE, 1L));
        itemRequestDto = new ItemRequestDto("description");
        itemRequestResponseDto = new ItemRequestResponseDto(1L, "description",
                LocalDateTime.now(), items);
    }

    @Test
    @SneakyThrows
    void should_create_request() {
        when(itemRequestService.addItemRequest(any(ItemRequestDto.class), anyLong()))
                .thenReturn(itemRequestResponseDto);

        mockMvc.perform(post("/requests")
                        .header(USER_ID_HEADER, "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(itemRequestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemRequestResponseDto.getId()))
                .andExpect(jsonPath("$.description").value(itemRequestResponseDto.getDescription()));
        verify(itemRequestService, times(1)).addItemRequest(any(ItemRequestDto.class), anyLong());
    }

    @Test
    @SneakyThrows
    void should_get_request_by_id() {
        when(itemRequestService.getItemRequestById(anyLong(), anyLong()))
                .thenReturn(itemRequestResponseDto);

        mockMvc.perform(get("/requests/1")
                        .header(USER_ID_HEADER, "1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemRequestResponseDto.getId()))
                .andExpect(jsonPath("$.description").value(itemRequestResponseDto.getDescription()));
        verify(itemRequestService, times(1)).getItemRequestById(anyLong(), anyLong());
    }

    @Test
    @SneakyThrows
    void should_get_all_requests() {
        List<ItemRequestResponseDto> expectedResult = Collections.singletonList(itemRequestResponseDto);
        when(itemRequestService.getAllRequests(anyLong(), anyInt(), anyInt()))
                .thenReturn(expectedResult);

        mockMvc.perform(get("/requests/all")
                        .header(USER_ID_HEADER, "1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1));
        verify(itemRequestService, times(1)).getAllRequests(anyLong(), anyInt(), anyInt());
    }

    @Test
    @SneakyThrows
    void should_get_all_requests_by_user_id() {
        List<ItemRequestResponseDto> expectedResult = Collections.emptyList();
        when(itemRequestService.getAllRequestsByUserId(anyLong()))
                .thenReturn(expectedResult);

        mockMvc.perform(get("/requests")
                        .header(USER_ID_HEADER, "1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(0));
        verify(itemRequestService, times(1)).getAllRequestsByUserId(anyLong());
    }

}

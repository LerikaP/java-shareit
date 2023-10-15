package ru.practicum.shareit.item;

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
import ru.practicum.shareit.item.dto.*;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemController.class)
@AutoConfigureMockMvc
public class ItemControllerTest {
    @MockBean
    private ItemService itemService;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper mapper;
    private static ItemDtoRequest itemDtoRequest;
    private static ItemDto itemDto;
    private static ItemDtoWithBooking itemDtoWithBooking;
    private static final String USER_ID_HEADER = "X-Sharer-User-Id";

    @BeforeAll
    static void setUp() {
        itemDtoRequest = new ItemDtoRequest("item", "description", Boolean.TRUE);
        itemDto = new ItemDto(1L, "item", "description", Boolean.TRUE);
        itemDtoWithBooking = new ItemDtoWithBooking(1L, "item", "description", Boolean.TRUE,
                null, null, Collections.emptyList());
    }

    @Test
    @SneakyThrows
    void should_create_item() {
        when(itemService.addItem(any(ItemDtoRequest.class), anyLong()))
                .thenReturn(itemDto);

        mockMvc.perform(post("/items")
                        .header(USER_ID_HEADER, "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(itemDtoRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemDto.getId()))
                .andExpect(jsonPath("$.name").value(itemDto.getName()))
                .andExpect(jsonPath("$.description").value(itemDto.getDescription()))
                .andExpect(jsonPath("$.available").value(itemDto.getAvailable()));
        verify(itemService, times(1)).addItem(any(ItemDtoRequest.class), anyLong());
    }

    @Test
    @SneakyThrows
    void should_update_item() {
        when(itemService.updateItem(any(ItemDto.class), anyLong(), anyLong()))
                .thenReturn(itemDto);

        mockMvc.perform(patch("/items/1")
                        .header(USER_ID_HEADER, "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(itemDtoRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemDto.getId()))
                .andExpect(jsonPath("$.name").value(itemDto.getName()))
                .andExpect(jsonPath("$.description").value(itemDto.getDescription()))
                .andExpect(jsonPath("$.available").value(itemDto.getAvailable()));
        verify(itemService, times(1)).updateItem(any(ItemDto.class), anyLong(), anyLong());
    }

    @Test
    @SneakyThrows
    void should_delete_item() {
        doNothing().when(itemService).deleteItemById(anyLong());

        mockMvc.perform(delete("/items/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        verify(itemService, times(1)).deleteItemById(anyLong());
    }

    @Test
    @SneakyThrows
    void should_get_item_by_id() {
        when(itemService.getItemById(anyLong(), anyLong()))
                .thenReturn(itemDtoWithBooking);

        mockMvc.perform(get("/items/1")
                        .header(USER_ID_HEADER, "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(itemDtoWithBooking)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemDto.getId()))
                .andExpect(jsonPath("$.name").value(itemDto.getName()))
                .andExpect(jsonPath("$.description").value(itemDto.getDescription()))
                .andExpect(jsonPath("$.available").value(itemDto.getAvailable()));
        verify(itemService, times(1)).getItemById(anyLong(), anyLong());
    }

    @Test
    @SneakyThrows
    void should_get_all_items_by_user_id() {
        List<ItemDtoWithBooking> expectedResult = Collections.singletonList(itemDtoWithBooking);
        when(itemService.getAllItemsByUserId(anyLong(), anyInt(), anyInt()))
                .thenReturn(expectedResult);

        mockMvc.perform(get("/items")
                        .header(USER_ID_HEADER, "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(expectedResult)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1));
        verify(itemService, times(1)).getAllItemsByUserId(anyLong(), anyInt(), anyInt());
    }

    @Test
    @SneakyThrows
    void should_search_items() {
        List<ItemDto> expectedResult = Collections.singletonList(itemDto);
        when(itemService.searchItem(anyString(), anyInt(), anyInt()))
                .thenReturn(expectedResult);

        mockMvc.perform(get("/items/search?text=All")
                        .header(USER_ID_HEADER, "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(expectedResult)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1));
        verify(itemService, times(1)).searchItem(anyString(), anyInt(), anyInt());
    }

    @Test
    @SneakyThrows
    void should_create_comment() {
        CommentRequestDto commentRequestDto = new CommentRequestDto("text");
        CommentResponseDto commentResponseDto = new CommentResponseDto(1L, "text", "user 1",
                LocalDateTime.now());
        when(itemService.addComment(anyLong(), any(CommentRequestDto.class), anyLong()))
                .thenReturn(commentResponseDto);

        mockMvc.perform(post("/items/1/comment")
                        .header(USER_ID_HEADER, "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(commentRequestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(commentResponseDto.getId()))
                .andExpect(jsonPath("$.text").value(commentResponseDto.getText()))
                .andExpect(jsonPath("$.authorName").value(commentResponseDto.getAuthorName()));
        verify(itemService, times(1)).addComment(anyLong(),
                any(CommentRequestDto.class), anyLong());
    }
}

package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookingController.class)
@AutoConfigureMockMvc
public class BookingControllerTest {
    @MockBean
    private BookingService bookingService;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper mapper;
    private static BookingDtoRequest bookingDtoRequest;
    private static BookingDtoResponse bookingDtoResponse;
    private static BookingDtoResponse approvedBookingDtoResponse;
    private static final String USER_ID_HEADER = "X-Sharer-User-Id";

    @BeforeAll
    static void setUp() {
        UserDto userDto = new UserDto(1L, "user 1", "user1@email.com");
        ItemDto itemDto = new ItemDto(1L, "item", "description", Boolean.TRUE);
        bookingDtoRequest = new BookingDtoRequest(1L,
                LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2));
        bookingDtoResponse = new BookingDtoResponse(1L, LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2), BookingStatus.WAITING, userDto, itemDto);
        approvedBookingDtoResponse = new BookingDtoResponse(1L, LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2), BookingStatus.APPROVED, userDto, itemDto);
    }

    @Test
    void should_create_booking() throws Exception {
        when(bookingService.addBooking(anyLong(), any(BookingDtoRequest.class)))
                .thenReturn(bookingDtoResponse);

        mockMvc.perform(post("/bookings")
                        .header(USER_ID_HEADER, "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(bookingDtoRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(bookingDtoResponse.getId()))
                .andExpect(jsonPath("$.status").value(bookingDtoResponse.getStatus().toString()))
                .andExpect(jsonPath("$.booker.id").value(bookingDtoResponse.getBooker().getId()))
                .andExpect(jsonPath("$.item.id").value(bookingDtoResponse.getItem().getId()));
        verify(bookingService, times(1)).addBooking(anyLong(), any(BookingDtoRequest.class));
    }

    @Test
    void should_change_booking_status() throws Exception {
        when(bookingService.changeBookingStatus(anyLong(), anyLong(), anyBoolean()))
                .thenReturn(bookingDtoResponse);

        mockMvc.perform(patch("/bookings/1?approved=true")
                        .header(USER_ID_HEADER, "1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(bookingDtoResponse.getId()))
                .andExpect(jsonPath("$.status").value(bookingDtoResponse.getStatus().toString()))
                .andExpect(jsonPath("$.booker.id").value(bookingDtoResponse.getBooker().getId()))
                .andExpect(jsonPath("$.item.id").value(bookingDtoResponse.getItem().getId()));
        verify(bookingService, times(1)).changeBookingStatus(anyLong(), anyLong(), anyBoolean());
    }

    @Test
    void should_get_booking_by_id() throws Exception {
        when(bookingService.getBookingById(anyLong(), anyLong()))
                .thenReturn(bookingDtoResponse);

        mockMvc.perform(get("/bookings/1")
                        .header(USER_ID_HEADER, "1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(bookingDtoResponse.getId()))
                .andExpect(jsonPath("$.status").value(bookingDtoResponse.getStatus().toString()))
                .andExpect(jsonPath("$.booker.id").value(bookingDtoResponse.getBooker().getId()))
                .andExpect(jsonPath("$.item.id").value(bookingDtoResponse.getItem().getId()));
        verify(bookingService, times(1)).getBookingById(anyLong(), anyLong());
    }

    @Test
    void should_get_bookings_by_user_id() throws Exception {
        List<BookingDtoResponse> expectedResult = Collections.singletonList(bookingDtoResponse);
        when(bookingService.getAllBookingsByUserId(anyLong(), anyString(), anyInt(), anyInt()))
                .thenReturn(expectedResult);

        mockMvc.perform(get("/bookings")
                        .header(USER_ID_HEADER, "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(expectedResult)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1));
        verify(bookingService, times(1)).getAllBookingsByUserId(anyLong(), anyString(),
                anyInt(), anyInt());
    }

    @Test
    void should_get_all_bookings_by_item_owner_id() throws Exception {
        List<BookingDtoResponse> expectedResult = Collections.singletonList(approvedBookingDtoResponse);
        when(bookingService.getAllBookingsByItemOwnerId(anyLong(), anyString(), anyInt(), anyInt()))
                .thenReturn(expectedResult);

        mockMvc.perform(get("/bookings/owner")
                        .header(USER_ID_HEADER, "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(expectedResult)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1));
        verify(bookingService, times(1)).getAllBookingsByItemOwnerId(anyLong(), anyString(),
                anyInt(), anyInt());
    }

}

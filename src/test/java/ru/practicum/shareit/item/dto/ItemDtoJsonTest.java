package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import org.springframework.boot.test.json.ObjectContent;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class ItemDtoJsonTest {
    @Autowired
    private JacksonTester<ItemDto> jacksonTester;
    private final ItemDto itemDto = new ItemDto(1L, "item 1",
            "description 1", Boolean.TRUE, 1L);

    @Test
    void shouldSerialize() throws IOException {
        JsonContent<ItemDto> itemDtoJsonContent = jacksonTester.write(itemDto);

        assertThat(itemDtoJsonContent).hasJsonPath("$.id");
        assertThat(itemDtoJsonContent).hasJsonPath("$.name");
        assertThat(itemDtoJsonContent).hasJsonPath("$.description");
        assertThat(itemDtoJsonContent).hasJsonPath("$.available");
        assertThat(itemDtoJsonContent).hasJsonPath("$.requestId");

        assertThat(itemDtoJsonContent).extractingJsonPathNumberValue(
                "$.id").isEqualTo(itemDto.getId().intValue());
        assertThat(itemDtoJsonContent).extractingJsonPathStringValue("$.name").isEqualTo(itemDto.getName());
        assertThat(itemDtoJsonContent).extractingJsonPathStringValue(
                "$.description").isEqualTo(itemDto.getDescription());
        assertThat(itemDtoJsonContent).extractingJsonPathBooleanValue(
                "$.available").isEqualTo(itemDto.getAvailable());
        assertThat(itemDtoJsonContent).extractingJsonPathNumberValue(
                "$.requestId").isEqualTo(itemDto.getRequestId().intValue());
    }

    @Test
    void shouldDeserialize() throws IOException {
        String dtoJson = "{\"id\":1,\"name\":\"item 1\",\"description\":\"description 1\"," +
                "\"available\":\"true\", \"requestId\":1}";
        ObjectContent<ItemDto> itemDtoObjectContent = jacksonTester.parse(dtoJson);

        assertThat(itemDtoObjectContent).extracting("id").isEqualTo(itemDto.getId());
        assertThat(itemDtoObjectContent).extracting("name").isEqualTo(itemDto.getName());
        assertThat(itemDtoObjectContent).extracting("description").isEqualTo(itemDto.getDescription());
        assertThat(itemDtoObjectContent).extracting("available").isEqualTo(itemDto.getAvailable());
        assertThat(itemDtoObjectContent).extracting("requestId").isEqualTo(itemDto.getRequestId());
    }
}

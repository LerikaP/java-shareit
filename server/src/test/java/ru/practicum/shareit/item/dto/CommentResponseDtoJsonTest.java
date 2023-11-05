package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import org.springframework.boot.test.json.ObjectContent;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class CommentResponseDtoJsonTest {
    @Autowired
    private JacksonTester<CommentResponseDto> jacksonTester;
    private static LocalDateTime created;

    @BeforeAll
    static void beforeAll() {
        created = LocalDateTime.now();
    }

    @Test
    void shouldSerialize() throws IOException {
        CommentResponseDto commentDto = new CommentResponseDto(1L, "search text", "author", created);
        JsonContent<CommentResponseDto> commentDtoJsonContent = jacksonTester.write(commentDto);

        assertThat(commentDtoJsonContent).hasJsonPath("$.id");
        assertThat(commentDtoJsonContent).hasJsonPath("$.text");
        assertThat(commentDtoJsonContent).hasJsonPath("$.authorName");
        assertThat(commentDtoJsonContent).hasJsonPath("$.created");

        assertThat(commentDtoJsonContent).extractingJsonPathNumberValue(
                "$.id").isEqualTo(commentDto.getId().intValue());
        assertThat(commentDtoJsonContent).extractingJsonPathStringValue(
                "$.text").isEqualTo(commentDto.getText());
        assertThat(commentDtoJsonContent).extractingJsonPathStringValue(
                "$.authorName").isEqualTo(commentDto.getAuthorName());
        assertThat(commentDtoJsonContent).extractingJsonPathValue(
                "$.created").isEqualTo(created.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
    }

    @Test
    void shouldDeserialize() throws IOException {
        String dtoJson = "{\"id\":1,\"text\":\"search text\",\"authorName\":\"author\"," +
                "\"created\":\"" + created + "\"}";
        ObjectContent<CommentResponseDto> commentDtoObjectContent = jacksonTester.parse(dtoJson);

        CommentResponseDto commentDto = new CommentResponseDto(1L, "search text", "author", created);

        assertThat(commentDtoObjectContent).extracting("id").isEqualTo(commentDto.getId());
        assertThat(commentDtoObjectContent).extracting("text").isEqualTo(commentDto.getText());
        assertThat(commentDtoObjectContent).extracting("authorName").isEqualTo(commentDto.getAuthorName());
        assertThat(commentDtoObjectContent).extracting("created").isEqualTo(commentDto.getCreated());
    }


}

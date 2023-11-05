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
public class CommentRequestDtoJsonTest {
    @Autowired
    private JacksonTester<CommentRequestDto> jacksonTester;
    private final CommentRequestDto commentDto = new CommentRequestDto("search text");

    @Test
    void shouldSerialize() throws IOException {
        JsonContent<CommentRequestDto> commentDtoJsonContent = jacksonTester.write(commentDto);

        assertThat(commentDtoJsonContent).extractingJsonPathStringValue(
                "$.text").isEqualTo(commentDto.getText());
    }

    @Test
    void shouldDeserialize() throws IOException {
        String dtoJson = "{\"text\":\"search text\"}";
        ObjectContent<CommentRequestDto> commentDtoObjectContent = jacksonTester.parse(dtoJson);

        assertThat(commentDtoObjectContent).extracting("text").isEqualTo(commentDto.getText());
    }
}

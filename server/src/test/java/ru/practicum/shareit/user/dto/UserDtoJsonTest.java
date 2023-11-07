package ru.practicum.shareit.user.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import org.springframework.boot.test.json.ObjectContent;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class UserDtoJsonTest {
    @Autowired
    private JacksonTester<UserDto> jacksonTester;
    private final UserDto userDto = new UserDto(1L, "user 1", "user@user.com");

    @Test
    void shouldSerialize() throws IOException {
        JsonContent<UserDto> result = jacksonTester.write(userDto);

        assertThat(result).hasJsonPath("$.id");
        assertThat(result).hasJsonPath("$.name");
        assertThat(result).hasJsonPath("$.email");

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(userDto.getId().intValue());
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo(userDto.getName());
        assertThat(result).extractingJsonPathStringValue("$.email").isEqualTo(userDto.getEmail());
    }

    @Test
    void shouldDeserialize() throws IOException {
        String dtoJson = "{\"id\":1,\"name\":\"user 1\",\"email\":\"user@user.com\"}";
        ObjectContent<UserDto> result = jacksonTester.parse(dtoJson);

        assertThat(result).extracting("id").isEqualTo(userDto.getId());
        assertThat(result).extracting("name").isEqualTo(userDto.getName());
        assertThat(result).extracting("email").isEqualTo(userDto.getEmail());
    }
}

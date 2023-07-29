package ru.practicum.shareit.user.model;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * TODO Sprint add-controllers.
 */
@Data
@AllArgsConstructor
public class User {
    private int id;
    private String name;
    private String email;
}

package com.example.task_tracker.model;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class UpsertUserRequest {

    @NotBlank(message = "Имя автора должно быть указано!")
    private String username;

    private String password;

    private String email;
}

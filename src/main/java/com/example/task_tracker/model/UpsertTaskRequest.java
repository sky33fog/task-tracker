package com.example.task_tracker.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpsertTaskRequest {

    @NotBlank(message = "Название задачи должно быть указано!")
    private String name;

    private String description;

    @Pattern(regexp = "TODO|IN_PROGRESS|DONE",
            message = "Статус задачи может иметь только значения: TODO, IN_PROGRESS, DONE ")
    private String status;

    private String assigneeId;

    private Set<String> observerIds;
}

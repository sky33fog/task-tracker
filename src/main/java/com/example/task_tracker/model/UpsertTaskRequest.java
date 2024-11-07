package com.example.task_tracker.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpsertTaskRequest {

    private String name;

    private String description;

    private String status;

    private String authorId;

    private String assigneeId;

    private Set<String> observerIds;
}

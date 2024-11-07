package com.example.task_tracker.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskResponse {

    private String id;

    private String name;

    private String description;

    private String createdAt;

    private String updatedAt;

    private String status;

    private UserResponse author;

    private UserResponse assignee;

    private Set<UserResponse> observers;
}

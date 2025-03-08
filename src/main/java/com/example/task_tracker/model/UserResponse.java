package com.example.task_tracker.model;

import com.example.task_tracker.entity.RoleType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserResponse {

    private String id;

    private String username;

    private String email;

    private Set<RoleType> roles;
}
